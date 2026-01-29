package me.freznel.compumancy.vm.execution;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.Compumancy;
import me.freznel.compumancy.casting.InvocationComponent;
import me.freznel.compumancy.vm.exceptions.StackOverflowException;
import me.freznel.compumancy.vm.execution.frame.ExecutionFrame;
import me.freznel.compumancy.vm.execution.frame.Frame;
import me.freznel.compumancy.vm.objects.VMObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class Invocation implements Runnable {
    private static final long CHECKPOINT_INTERVAL = 1000 * 10;

    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();
    private static final ConcurrentHashMap<UUID, Invocation> RunningInvocations = new ConcurrentHashMap<>();

    public static Invocation GetRunningInvocation(UUID id) {
        if (!RunningInvocations.containsKey(id)) return null;
        return RunningInvocations.get(id);
    }

    private World world;
    private Ref<EntityStore> caster;

    private ArrayList<VMObject> operandStack;
    private ArrayList<Frame> frameStack;
    private int executionBudget;
    private int currentExecutionBudget;

    private final UUID id;
    private long nextCheckpoint;
    private AtomicBoolean isCanceled;

    public Invocation() { id = UUID.randomUUID(); }

    public Invocation(World world, Ref<EntityStore> caster, ArrayList<VMObject> contents, int executionBudget) {
        operandStack = new ArrayList<>();
        frameStack = new ArrayList<>();
        this.executionBudget = executionBudget;
        this.currentExecutionBudget = executionBudget;
        this.caster = caster;
        this.world = world;
        frameStack.addLast(new ExecutionFrame(contents));
        this.id = UUID.randomUUID();
    }

    public Invocation(World world, Ref<EntityStore> caster, InvocationState state) {
        this.caster = caster;
        this.world = world;
        this.executionBudget = state.GetExecutionBudget();
        this.operandStack = state.GetOperandStack();
        this.frameStack = state.GetFrameStack();
        this.id = state.GetId();
    }

    public World GetWorld() { return world; }
    public Ref<EntityStore> GetCaster() { return caster; }

    public void SetOperandStack(ArrayList<VMObject> operandStack) { this.operandStack = operandStack; }
    public ArrayList<VMObject> GetOperandStack() { return this.operandStack; }
    public void SetFrameStack (ArrayList<Frame> frameStack) { this.frameStack = frameStack; }
    public ArrayList<Frame> GetFrameStack() { return this.frameStack; }
    public void SetExecutionBudget(int executionBudget) { this.executionBudget = executionBudget; }
    public int GetExecutionBudget() { return this.executionBudget; }
    public UUID GetId() { return this.id; }

    public Frame GetCurrentFrame() { return frameStack.isEmpty() ? null : frameStack.getLast(); }
    public boolean IsFinished() { return frameStack.isEmpty(); }
    public void PushFrame(Frame frame) {
        frameStack.addLast(frame);
        if (frameStack.size() > 128) throw new StackOverflowException("Maximum execution depth of 128 exceeded");
    }

    public int OperandCount() { return operandStack.size(); }
    public VMObject Pop() { return operandStack.removeLast(); }
    public void Push(VMObject o) {
        operandStack.addLast(o);
        if (operandStack.size() > 1024) throw new StackOverflowException("Maximum operand stack depth of 1024 exceeded");
    }
    public VMObject Peek() { return operandStack.getLast(); }
    public VMObject Peek(int depth) { return operandStack.get((operandStack.size() - 1) - depth); }

    public void Step() {
        currentExecutionBudget = executionBudget;
        while (currentExecutionBudget > 0 && !frameStack.isEmpty()) {
            var frame = frameStack.getLast();
            frame.Execute(this);
            if (frame.IsFinished() && frameStack.getLast() == frame) frameStack.removeLast();
        }
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

    @Override
    public void run() {
        RunningInvocations.put(id, this);
        try {
            while (!IsCancelled() && !frameStack.isEmpty()) {
                Step();
                if (!Checkpoint(false, true)) break;
            }
            Checkpoint(true, false);
        } catch(Exception e) {
            Logger.at(Level.INFO).log(String.format("Invocation %s terminated by %s: %s", id.toString(), e.getClass().getSimpleName(), e.getMessage()));
            throw e;
        } finally {
            RunningInvocations.remove(id);
        }
    }
}
