package me.freznel.compumancy.vm.execution;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.freznel.compumancy.Compumancy;
import me.freznel.compumancy.ecs.component.IDefinitionStore;
import me.freznel.compumancy.vm.compiler.Vocabulary;
import me.freznel.compumancy.vm.compiler.Word;
import me.freznel.compumancy.vm.exceptions.CompileException;
import me.freznel.compumancy.vm.exceptions.DefinitionNotFoundException;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.StackOverflowException;
import me.freznel.compumancy.vm.execution.frame.CompileFrame;
import me.freznel.compumancy.vm.execution.frame.DefSyncFrame;
import me.freznel.compumancy.vm.execution.frame.ExecutionFrame;
import me.freznel.compumancy.vm.execution.frame.Frame;
import me.freznel.compumancy.vm.objects.ListObject;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.store.InvocationStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class Invocation implements Runnable {

    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();

    private World world;
    private Caster<?> caster;
    private InvocationStore store;

    private ArrayList<VMObject> operandStack;
    private ArrayList<Frame> frameStack;
    private int executionBudget;
    private int currentExecutionBudget;

    private final UUID id;
    private final AtomicBoolean suspended;
    private boolean errored;
    private boolean isRunningSync;
    private long lastRunTimestamp;

    private boolean definitionsAttached;
    private Map<String, ExecutionFrame> cachedDefFrames;
    private ConcurrentHashMap<String, Word> casterDefs;
    private int maxUserDefs;
    private Vocabulary fixedDefs;

    private double charge;
    private long chargeAmountIncreaseTimestamp;
    private double amountToCharge;

    public Invocation() {
        this.id = UUID.randomUUID();
        this.suspended = new AtomicBoolean(true);
        this.isRunningSync = false;
    }

    public Invocation(World world, Caster<?> caster, InvocationStore store, ArrayList<VMObject> contents, int executionBudget) {
        this.operandStack = new ArrayList<>();
        this.frameStack = new ArrayList<>();
        this.executionBudget = executionBudget;
        this.currentExecutionBudget = executionBudget;
        this.caster = caster;
        this.store = store;
        this.world = world;
        this.frameStack.addLast(new ExecutionFrame(contents));
        this.id = UUID.randomUUID();
        this.suspended = new AtomicBoolean(true);
        this.isRunningSync = false;
    }

    public Invocation(World world, Caster<?> caster, InvocationStore store, String compileString, int executionBudget) {
        this.operandStack = new ArrayList<>();
        this.frameStack = new ArrayList<>();
        this.executionBudget = executionBudget;
        this.currentExecutionBudget = executionBudget;
        this.caster = caster;
        this.store = store;
        this.world = world;
        this.id = UUID.randomUUID();
        this.suspended = new AtomicBoolean(true);
        this.isRunningSync = false;
        pushFrame(new CompileFrame(compileString));
    }

    public Invocation(World world, Caster<?> caster, InvocationState state, InvocationStore store) {
        this.caster = caster;
        this.world = world;
        this.executionBudget = state.getExecutionBudget();
        this.operandStack = state.getOperandStack();
        this.frameStack = state.getFrameStack();
        this.id = state.getId();
        this.suspended = new AtomicBoolean(true);
        this.isRunningSync = false;
        this.store = store;
        this.lastRunTimestamp = state.getLastRunTimestamp();
        this.charge = state.getCharge();
    }

    public World getWorld() { return world; }
    public Caster<?> getCaster() { return caster; }
    public InvocationStore getStore() { return store; }

    public void setOperandStack(ArrayList<VMObject> operandStack) { this.operandStack = operandStack; }
    public ArrayList<VMObject> getOperandStack() { return this.operandStack; }
    public void setFrameStack(ArrayList<Frame> frameStack) { this.frameStack = frameStack; }
    public ArrayList<Frame> getFrameStack() { return this.frameStack; }
    public void setExecutionBudget(int executionBudget) { this.executionBudget = executionBudget; }
    public int getExecutionBudget() { return this.executionBudget; }
    public void setCurrentExecutionBudget(int currentExecutionBudget) { this.currentExecutionBudget = currentExecutionBudget; }
    public int getCurrentExecutionBudget() { return this.currentExecutionBudget; }
    public UUID getId() { return this.id; }
    public long getLastRunTimestamp() { return this.lastRunTimestamp; }
    public double getCharge() { return this.charge; }

    public Frame getCurrentFrame() { return frameStack.isEmpty() ? null : frameStack.getLast(); }
    public boolean isFinished() { return frameStack.isEmpty(); }
    public void pushFrame(Frame frame) {
        while (!frameStack.isEmpty() && frameStack.getLast().isFinished()) frameStack.removeLast();
        frameStack.addLast(frame);
        if (frameStack.size() > 128) throw new StackOverflowException("Maximum execution depth of 128 exceeded");
    }
    public boolean isRunningSync() { return this.isRunningSync; }
    public boolean isSuspended() { return this.suspended.get(); }
    public void suspend() {
        this.suspended.set(true);
        attachDefinitionStore(null);
    }

    public int getOperandCount() { return operandStack.size(); }
    public VMObject pop() { return operandStack.removeLast(); }
    public void push(VMObject o) {
        operandStack.addLast(o);
        if (operandStack.size() > 1024) throw new StackOverflowException("Maximum operand stack depth of 1024 exceeded");
    }
    public VMObject peek() { return operandStack.getLast(); }
    public VMObject peek(int depth) { return operandStack.get((operandStack.size() - 1) - depth); }

    public void step() {
        long interruptAt = System.nanoTime() + 1_000_000 * 5; //+5ms maximum
        currentExecutionBudget = executionBudget;
        while (currentExecutionBudget > 0 && !frameStack.isEmpty() && System.nanoTime() < interruptAt && !isSuspended()) {
            var frame = frameStack.getLast();
            if (frame.isFinished()) { frameStack.removeLast(); continue; }
            if (isWrongSync(frame.getFrameSyncType())) break;
            frame.execute(this, interruptAt);
        }
    }

    public boolean isWrongSync(FrameSyncType syncType) {
        return ((syncType == FrameSyncType.Sync && !isRunningSync) || (syncType == FrameSyncType.Async && isRunningSync));
    }

    //Save the state of this invocation to the InvocationComponent.  Cancel the invocation if the entity has become invalid.
    /*private boolean Checkpoint(boolean force, boolean replace) {
        if (!force && System.currentTimeMillis() < nextCheckpoint) return true;
        if (!caster.isValid()) return false;
        final var state = new InvocationState(this);
        world.execute(() -> {
            if (!caster.isValid()) { Cancel(); return; }
            var store = caster.getStore();
            var invocationComponent = store.getComponent(caster, Compumancy.Get().GetInvocationComponentType());
            if (invocationComponent == null) { Cancel(); return; }
            if (replace) {
                if (!invocationComponent.Replace(state)) Cancel();
            } else {
                if (!invocationComponent.Remove(state.GetId())) Cancel();
            }
        });
        nextCheckpoint = System.currentTimeMillis() + Compumancy.Get().GetConfig().CheckpointInterval;
        return true;
    }*/

    private void scheduleSync(int additionalDelay) {
        Compumancy.get().scheduleDaemon(() -> {
            world.execute(this);
        }, Compumancy.get().getConfig().SyncStepDelay + additionalDelay);
    }

    public boolean schedule() {
        if (errored || isFinished()) return false;
        var nextFrame = frameStack.getLast();
        var syncType = nextFrame.getFrameSyncType();
        int additionalDelay = store.resumeDelay();
        if (syncType == FrameSyncType.Neutral) {
            if (isRunningSync) {
                scheduleSync(additionalDelay);
            } else {
                Compumancy.get().scheduleDaemon(this, Compumancy.get().getConfig().AsyncStepDelay + additionalDelay);
            }
        } else if (syncType == FrameSyncType.Sync) {
            if (!isRunningSync) Logger.at(Level.INFO).log(String.format("Invocation %s switched to world thread", id.toString()));
            isRunningSync = true;
            scheduleSync(additionalDelay);
        } else {
            if (isRunningSync) Logger.at(Level.INFO).log(String.format("Invocation %s switched to background thread", id.toString()));
            isRunningSync = false;
            Compumancy.get().scheduleDaemon(this, Compumancy.get().getConfig().AsyncStepDelay + additionalDelay);
        }
        suspended.set(false);
        return true;
    }

    public boolean schedule(Caster<?> caster, World world) {
        this.caster = caster;
        this.world = world;
        return schedule();
    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                //Logger.at(Level.INFO).log(String.format("Stepping invocation %s", id.toString()));
                if (isFinished()) { store.kill(id); return; }
                if (isSuspended()) return;
                step();
                if (isFinished()) { store.kill(id); return; }
                if (isSuspended()) return;
                lastRunTimestamp = System.currentTimeMillis();
                schedule();
            } catch (Exception e) {
                Logger.at(Level.INFO).log(String.format("Invocation %s terminated by %s: %s", id.toString(), e.getClass().getSimpleName(), e.getMessage()));
                var player = Universe.get().getPlayer(store.getOwner());
                if (player != null)
                    player.sendMessage(Message.raw(String.format("An invocation failed with %s: %s", e.getClass().getSimpleName(), e.getMessage())));
                errored = true;
                store.kill(id);
                throw e;
            }
        }
    }

    public boolean isDefinitionStoreAttached() { return definitionsAttached; }
    public void attachDefinitionStore(IDefinitionStore defStore) {
        if (defStore == null) {
            definitionsAttached = false;
            maxUserDefs = 0;
            casterDefs = null;
            fixedDefs = null;
        } else {
            cachedDefFrames = new Object2ObjectOpenHashMap<>();
            maxUserDefs = defStore.getMaxUserDefs();
            casterDefs = defStore.GetUserDefsMap();
            String fixedVocabularyName = defStore.getFixedVocabularyName();
            if (fixedVocabularyName != null) {
                fixedDefs = Vocabulary.getVocabulary(fixedVocabularyName);
                if (fixedDefs == null) Logger.at(Level.WARNING).log(String.format("The fixed vocabulary '%s' was not found", fixedVocabularyName));
            }
            definitionsAttached = true;
        }
    }

    public void executeDefinition(String defName) {
        if (!definitionsAttached) {
            pushFrame(new DefSyncFrame(DefSyncFrame.DefAction.Execute, defName));
            return;
        }
        ExecutionFrame frame;
        Word def;
        if ((frame = cachedDefFrames.get(defName)) != null) {
            frameStack.addLast(frame.copyFull());
        } else if (fixedDefs != null && (def = fixedDefs.get(defName)) != null) {
            frame = def.toExecutionFrame();
            cachedDefFrames.put(defName, frame);
            frameStack.addLast(frame);
        } else if (casterDefs != null && (def = casterDefs.get(defName)) != null) {
            frame = def.toExecutionFrame();
            cachedDefFrames.put(defName, frame);
            frameStack.addLast(frame);
        } else {
            throw new DefinitionNotFoundException(String.format("The definition '%s' was not found", defName));
        }
    }

    public void storeDefinition(String defName, Word word) {
        if (!definitionsAttached) {
            pushFrame(new DefSyncFrame(DefSyncFrame.DefAction.Store, defName, word));
            return;
        }
        if (fixedDefs != null && fixedDefs.contains(defName)) {
            throw new InvalidOperationException(String.format("Attempted to override fixed definition '%s'", defName));
        }
        if (casterDefs == null) throw new CompileException(String.format("Failed to save definition '%s', definition store is read-only", defName));
        if (casterDefs.size() >= maxUserDefs && !casterDefs.containsKey(defName)) throw new CompileException(String.format("Failed to save definition '%s', no maximum definition count of %d reached", defName, maxUserDefs));
        casterDefs.put(defName, word);
        cachedDefFrames.remove(defName);
    }

    public void loadDefinition(String defName) {
        if (!definitionsAttached) {
            pushFrame(new DefSyncFrame(DefSyncFrame.DefAction.Load, defName));
            return;
        }
        Word def;
        ArrayList<VMObject> list;
        if (fixedDefs != null && (def = fixedDefs.get(defName)) != null) {
            list = new ArrayList<>();
            def.addContentsToList(list);
        } else if (casterDefs != null && (def = casterDefs.get(defName)) != null) {
            list = new ArrayList<>();
            def.addContentsToList(list);
        } else {
            throw new DefinitionNotFoundException(String.format("The definition '%s' was not found", defName));
        }
        push(new ListObject(list, def.isExecuteSync()));
    }

    //Consume charge from the buffer.  If the buffer is empty, attempt to refill it.  Multiple refills in quick succession increases the refill amount.
    public boolean consumeCharge(double amount) {
        if (charge >= amount) { charge -= amount; return true; }
        if (System.currentTimeMillis() > chargeAmountIncreaseTimestamp) {
            amountToCharge = Math.round(amountToCharge * 1.5);
        } else if (amountToCharge == 0) amountToCharge = 10;
        double refillAmount = amountToCharge + (amount - charge);




        chargeAmountIncreaseTimestamp = System.currentTimeMillis() + 500;
        return true;
    }

    public void assertInAmbit(double x, double y, double z) {
        //TODO: Implement
    }

    public boolean isInAmbit(double x, double y, double z) {
        return true; //TODO: Implement
    }









}
