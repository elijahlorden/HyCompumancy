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
import me.freznel.compumancy.vm.exceptions.DefinitionNotFoundException;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.StackOverflowException;
import me.freznel.compumancy.vm.execution.frame.CompileFrame;
import me.freznel.compumancy.vm.execution.frame.ExecutionFrame;
import me.freznel.compumancy.vm.execution.frame.Frame;
import me.freznel.compumancy.vm.objects.VMObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class Invocation implements Runnable {
    private static final long CHECKPOINT_INTERVAL = 1000 * 10;
    private static final long SCHEDULE_DELAY_ASYNC = 50;
    private static final long SCHEDULE_DELAY_SYNC = 100;

    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();
    private static final ConcurrentHashMap<UUID, Invocation> RunningInvocations = new ConcurrentHashMap<>();

    public static Invocation GetRunningInvocation(UUID id) {
        if (!RunningInvocations.containsKey(id)) return null;
        return RunningInvocations.get(id);
    }

    private World world;
    private Ref<EntityStore> caster;
    private UUID owner;

    private ArrayList<VMObject> operandStack;
    private ArrayList<Frame> frameStack;
    private int executionBudget;
    private int currentExecutionBudget;

    private final UUID id;
    private long nextCheckpoint;
    private final AtomicBoolean isCanceled;
    private boolean isRunningSync;

    private boolean definitionsAttached;
    private Map<String, ExecutionFrame> cachedDefFrames;
    private ConcurrentHashMap<String, Word> casterDefs;
    private int maxUserDefs;
    private Vocabulary fixedDefs;

    public Invocation() {
        this.id = UUID.randomUUID();
        this.isCanceled = new AtomicBoolean(false);
        this.isRunningSync = false;
    }

    public Invocation(World world, Ref<EntityStore> caster, UUID owner, ArrayList<VMObject> contents, int executionBudget) {
        this.operandStack = new ArrayList<>();
        this.frameStack = new ArrayList<>();
        this.executionBudget = executionBudget;
        this.currentExecutionBudget = executionBudget;
        this.caster = caster;
        this.owner = owner;
        this.world = world;
        this.frameStack.addLast(new ExecutionFrame(contents));
        this.id = UUID.randomUUID();
        this.isCanceled = new AtomicBoolean(false);
        this.isRunningSync = false;
        TryAttachDefinitionComponent();
    }

    public Invocation(World world, Ref<EntityStore> caster, UUID owner, String compileString, int executionBudget) {
        this.operandStack = new ArrayList<>();
        this.frameStack = new ArrayList<>();
        this.executionBudget = executionBudget;
        this.currentExecutionBudget = executionBudget;
        this.caster = caster;
        this.owner = owner;
        this.world = world;
        this.id = UUID.randomUUID();
        this.isCanceled = new AtomicBoolean(false);
        this.isRunningSync = false;
        PushFrame(new CompileFrame(compileString));
        TryAttachDefinitionComponent();
    }

    public Invocation(World world, Ref<EntityStore> caster, InvocationState state) {
        this.caster = caster;
        this.world = world;
        this.executionBudget = state.GetExecutionBudget();
        this.operandStack = state.GetOperandStack();
        this.frameStack = state.GetFrameStack();
        this.id = state.GetId();
        this.isCanceled = new AtomicBoolean(false);
        this.isRunningSync = false;
        this.owner = state.GetOwner();
        TryAttachDefinitionComponent();
    }

    public World GetWorld() { return world; }
    public Ref<EntityStore> GetCaster() { return caster; }
    public UUID GetOwner() { return owner; }

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
        while (currentExecutionBudget > 0 && !frameStack.isEmpty() && System.nanoTime() < interruptAt) {
            var frame = frameStack.getLast();
            if (frame.IsFinished()) { frameStack.removeLast(); continue; }
            if (IsWrongSync(frame.GetFrameSyncType())) break;
            frame.Execute(this, interruptAt);
        }
    }

    public boolean IsWrongSync(FrameSyncType syncType) {
        return ((syncType == FrameSyncType.Sync && !isRunningSync) || (syncType == FrameSyncType.Async && isRunningSync));
    }

    public void Cancel() { isCanceled.set(true); }
    public boolean IsCancelled() { return isCanceled.get(); }

    //Save the state of this invocation to the InvocationComponent.  Cancel the invocation if the entity has become invalid.
    private boolean Checkpoint(boolean force, boolean replace) {
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
        nextCheckpoint = System.currentTimeMillis() + CHECKPOINT_INTERVAL;
        return true;
    }

    private void ScheduleSync() {
        Compumancy.Get().Schedule(() -> {
            world.execute(this);
        }, SCHEDULE_DELAY_SYNC);
    }

    private void Schedule() {
        var nextFrame = frameStack.getLast();
        var syncType = nextFrame.GetFrameSyncType();
        if (syncType == FrameSyncType.Neutral) {
            if (isRunningSync) {
                ScheduleSync();
            } else {
                Compumancy.Get().Schedule(this, SCHEDULE_DELAY_ASYNC);
            }
        } else if (syncType == FrameSyncType.Sync) {
            if (!isRunningSync) Logger.at(Level.INFO).log(String.format("Invocation %s switched to world thread", id.toString()));
            isRunningSync = true;
            ScheduleSync();
        } else {
            if (isRunningSync) Logger.at(Level.INFO).log(String.format("Invocation %s switched to background thread", id.toString()));
            isRunningSync = false;
            Compumancy.Get().Schedule(this, SCHEDULE_DELAY_ASYNC);
        }
    }

    public void Start() {
        if (RunningInvocations.containsKey(id) || IsCancelled() || IsFinished()) return;
        nextCheckpoint = System.currentTimeMillis() + CHECKPOINT_INTERVAL;
        RunningInvocations.put(id, this);
        Schedule();
    }

    private void End() {
        RunningInvocations.remove(id);
        isCanceled.set(true);
        Checkpoint(true, false);
        Logger.at(Level.INFO).log(String.format("Ended invocation %s", id.toString()));
    }

    @Override
    public void run() {
        //Logger.at(Level.INFO).log(String.format("Running invocation %s", id.toString()));
        try {
            if (IsCancelled()) { End(); return; }
            //Logger.at(Level.INFO).log(String.format("Stepping invocation %s", id.toString()));
            Step();
            if (Checkpoint(false, true) && !IsFinished() && !IsCancelled()) {
                Schedule();
            } else {
                End();
            }
        } catch (Exception e) {
            Logger.at(Level.INFO).log(String.format("Invocation %s terminated by %s: %s", id.toString(), e.getClass().getSimpleName(), e.getMessage()));
            var player = Universe.get().getPlayer(owner);
            if (player != null) player.sendMessage(Message.raw(String.format("An invocation failed with %s: %s", e.getClass().getSimpleName(), e.getMessage())));
            End();
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
        } else if ((def = casterDefs.get(defName)) != null) {
            frame = def.ToExecutionFrame();
            cachedDefFrames.put(defName, frame);
            frameStack.addLast(frame);
        } else {
            throw new DefinitionNotFoundException(String.format("The definition '%s' was not found", defName));
        }
    }

    public void AddDefinition(String defName, Word word) {
        if (fixedDefs != null && fixedDefs.Contains(defName)) {
            throw new InvalidOperationException(String.format("Attempted to override fixed definition '%s'", defName));
        }
        casterDefs.put(defName, word);
        cachedDefFrames.remove(defName);
    }













}
