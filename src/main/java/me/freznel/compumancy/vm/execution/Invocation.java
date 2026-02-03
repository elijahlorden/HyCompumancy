package me.freznel.compumancy.vm.execution;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.freznel.compumancy.Compumancy;
import me.freznel.compumancy.vm.compiler.Vocabulary;
import me.freznel.compumancy.vm.compiler.Word;
import me.freznel.compumancy.vm.exceptions.CompileException;
import me.freznel.compumancy.vm.exceptions.DefinitionNotFoundException;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.StackOverflowException;
import me.freznel.compumancy.vm.execution.frame.CompileFrame;
import me.freznel.compumancy.vm.execution.frame.ExecutionFrame;
import me.freznel.compumancy.vm.execution.frame.Frame;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.store.InvocationStore;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class Invocation implements Runnable {

    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();

    private World world;
    private Ref<EntityStore> caster;
    private InvocationStore store;

    private ArrayList<VMObject> operandStack;
    private ArrayList<Frame> frameStack;
    private int executionBudget;
    private int currentExecutionBudget;

    private final UUID id;
    private long nextCheckpoint;
    private final AtomicBoolean suspended;
    private boolean errored;
    private boolean isRunningSync;

    public final AtomicReference<CompletableFuture<InvocationState>> SuspendFuture;

    private boolean definitionsAttached;
    private Map<String, ExecutionFrame> cachedDefFrames;
    private ConcurrentHashMap<String, Word> casterDefs;
    private int maxUserDefs;
    private Vocabulary fixedDefs;

    public Invocation() {
        this.id = UUID.randomUUID();
        this.suspended = new AtomicBoolean(true);
        this.isRunningSync = false;
        this.SuspendFuture = new AtomicReference<>();
    }

    public Invocation(World world, Ref<EntityStore> caster, InvocationStore store, ArrayList<VMObject> contents, int executionBudget) {
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
        this.SuspendFuture = new AtomicReference<>();
        TryAttachDefinitionComponent();
    }

    public Invocation(World world, Ref<EntityStore> caster, InvocationStore store, String compileString, int executionBudget) {
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
        this.SuspendFuture = new AtomicReference<>();
        PushFrame(new CompileFrame(compileString));
        TryAttachDefinitionComponent();
    }

    public Invocation(World world, Ref<EntityStore> caster, InvocationState state, InvocationStore store) {
        this.caster = caster;
        this.world = world;
        this.executionBudget = state.GetExecutionBudget();
        this.operandStack = state.GetOperandStack();
        this.frameStack = state.GetFrameStack();
        this.id = state.GetId();
        this.suspended = new AtomicBoolean(true);
        this.isRunningSync = false;
        this.SuspendFuture = new AtomicReference<>();
        TryAttachDefinitionComponent();
    }

    public World GetWorld() { return world; }
    public Ref<EntityStore> GetCaster() { return caster; }
    public InvocationStore GetStore() { return store; }

    public void SetOperandStack(ArrayList<VMObject> operandStack) { this.operandStack = operandStack; }
    public ArrayList<VMObject> GetOperandStack() { return this.operandStack; }
    public void SetFrameStack (ArrayList<Frame> frameStack) { this.frameStack = frameStack; }
    public ArrayList<Frame> GetFrameStack() { return this.frameStack; }
    public void SetExecutionBudget(int executionBudget) { this.executionBudget = executionBudget; }
    public int GetExecutionBudget() { return this.executionBudget; }
    public void SetCurrentExecutionBudget(int currentExecutionBudget) { this.currentExecutionBudget = currentExecutionBudget; }
    public int GetCurrentExecutionBudget() { return this.currentExecutionBudget; }
    public UUID GetId() { return this.id; }

    public Frame GetCurrentFrame() { return frameStack.isEmpty() ? null : frameStack.getLast(); }
    public boolean IsFinished() { return frameStack.isEmpty(); }
    public void PushFrame(Frame frame) {
        frameStack.addLast(frame);
        if (frameStack.size() > 128) throw new StackOverflowException("Maximum execution depth of 128 exceeded");
    }
    public boolean IsRunningSync() { return this.isRunningSync; }
    public boolean IsSuspended() { return this.suspended.get(); }
    public void Suspend() { this.suspended.set(true); }

    public int OperandCount() { return operandStack.size(); }
    public VMObject Pop() { return operandStack.removeLast(); }
    public void Push(VMObject o) {
        operandStack.addLast(o);
        if (operandStack.size() > 1024) throw new StackOverflowException("Maximum operand stack depth of 1024 exceeded");
    }
    public VMObject Peek() { return operandStack.getLast(); }
    public VMObject Peek(int depth) { return operandStack.get((operandStack.size() - 1) - depth); }

    public void Step() {
        long interruptAt = System.nanoTime() + 1_000_000 * 5; //+5ms maximum
        currentExecutionBudget = executionBudget;
        while (currentExecutionBudget > 0 && !frameStack.isEmpty() && System.nanoTime() < interruptAt && !IsSuspended()) {
            var frame = frameStack.getLast();
            if (frame.IsFinished()) { frameStack.removeLast(); continue; }
            if (IsWrongSync(frame.GetFrameSyncType())) break;
            frame.Execute(this, interruptAt);
        }
    }

    public boolean IsWrongSync(FrameSyncType syncType) {
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

    private void ScheduleSync(int additionalDelay) {
        Compumancy.Get().Schedule(() -> {
            world.execute(this);
        }, Compumancy.Get().GetConfig().SyncStepDelay + additionalDelay);
    }

    public boolean Schedule() {
        if (errored || IsFinished()) return false;
        suspended.set(false);
        var nextFrame = frameStack.getLast();
        var syncType = nextFrame.GetFrameSyncType();
        int additionalDelay = store.resumeDelay();
        if (syncType == FrameSyncType.Neutral) {
            if (isRunningSync) {
                ScheduleSync(additionalDelay);
            } else {
                Compumancy.Get().Schedule(this, Compumancy.Get().GetConfig().AsyncStepDelay + additionalDelay);
            }
        } else if (syncType == FrameSyncType.Sync) {
            if (!isRunningSync) Logger.at(Level.INFO).log(String.format("Invocation %s switched to world thread", id.toString()));
            isRunningSync = true;
            ScheduleSync(additionalDelay);
        } else {
            if (isRunningSync) Logger.at(Level.INFO).log(String.format("Invocation %s switched to background thread", id.toString()));
            isRunningSync = false;
            Compumancy.Get().Schedule(this, Compumancy.Get().GetConfig().AsyncStepDelay + additionalDelay);
        }
        return true;
    }

    @Override
    public void run() {
        try {
            //Logger.at(Level.INFO).log(String.format("Stepping invocation %s", id.toString()));
            Step();
            if (IsSuspended()) {
                var future = SuspendFuture.getAndSet(null);
                if (future != null) future.complete(new InvocationState(this));
            }
            if (!IsFinished() && !IsSuspended()) Schedule();
        } catch (Exception e) {
            Logger.at(Level.INFO).log(String.format("Invocation %s terminated by %s: %s", id.toString(), e.getClass().getSimpleName(), e.getMessage()));
            var player = Universe.get().getPlayer(store.GetOwner());
            if (player != null) player.sendMessage(Message.raw(String.format("An invocation failed with %s: %s", e.getClass().getSimpleName(), e.getMessage())));
            errored = true;
            store.Kill(id);
            throw e;
        }
    }

    private void TryAttachDefinitionComponent() {
        if (caster == null || !caster.isValid()) return;
        var store = caster.getStore();
        var defComp = store.getComponent(caster, Compumancy.Get().GetDefinitionStoreComponentType());
        if (defComp != null) {
            cachedDefFrames = new Object2ObjectOpenHashMap<>();
            maxUserDefs = defComp.GetMaxUserDefs();
            casterDefs = defComp.GetUserDefsMap();
            String fixedVocabularyName = defComp.GetFixedVocabularyName();
            if (fixedVocabularyName != null) {
                fixedDefs = Vocabulary.GetVocabulary(fixedVocabularyName);
                if (fixedDefs == null) Logger.at(Level.WARNING).log(String.format("The fixed vocabulary '%s' was not found", fixedVocabularyName));
            }
            definitionsAttached = true;
        }
    }

    public void ExecuteDefinition(String defName) {
        if (!definitionsAttached) throw new DefinitionNotFoundException(String.format("The definition '%s' was not found", defName));
        ExecutionFrame frame;
        Word def;
        if ((frame = cachedDefFrames.get(defName)) != null) {
            frameStack.addLast(frame.CopyFull());
        } else if (fixedDefs != null && (def = fixedDefs.Get(defName)) != null) {
            frame = def.ToExecutionFrame();
            cachedDefFrames.put(defName, frame);
            frameStack.addLast(frame);
        } else if (casterDefs != null && (def = casterDefs.get(defName)) != null) {
            frame = def.ToExecutionFrame();
            cachedDefFrames.put(defName, frame);
            frameStack.addLast(frame);
        } else {
            throw new DefinitionNotFoundException(String.format("The definition '%s' was not found", defName));
        }
    }

    public void AddDefinition(String defName, Word word) {
        if (!definitionsAttached) throw new CompileException(String.format("Failed to save definition '%s', no definition store found", defName));
        if (fixedDefs != null && fixedDefs.Contains(defName)) {
            throw new InvalidOperationException(String.format("Attempted to override fixed definition '%s'", defName));
        }
        if (casterDefs == null) throw new CompileException(String.format("Failed to save definition '%s', definition store is read-only", defName));
        if (casterDefs.size() >= maxUserDefs && !casterDefs.containsKey(defName)) throw new CompileException(String.format("Failed to save definition '%s', no maximum definition count of %d reached", defName, maxUserDefs));
        casterDefs.put(defName, word);
        cachedDefFrames.remove(defName);
    }













}
