package me.freznel.compumancy.vm.execution;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.vm.objects.VMObject;

import java.util.ArrayList;

public class Invocation {

    private World world;
    private Ref<EntityStore> caster;

    private ArrayList<VMObject> operandStack;
    private ArrayList<Frame> frameStack;
    private int executionBudget;

    public Invocation() {}

    public Invocation(World world, Ref<EntityStore> caster, ArrayList<VMObject> contents, int executionBudget) {
        operandStack = new ArrayList<>();
        frameStack = new ArrayList<>();
        this.executionBudget = executionBudget;
        this.caster = caster;
        frameStack.addLast(new ExecutionFrame(contents));
    }

    public World GetWorld() { return world; }
    public Ref<EntityStore> GetCaster() { return caster; }

    public void SetOperandStack(ArrayList<VMObject> operandStack) { this.operandStack = operandStack; }
    public ArrayList<VMObject> GetOperandStack() { return this.operandStack; }
    public void SetFrameStack (ArrayList<Frame> frameStack) { this.frameStack = frameStack; }
    public ArrayList<Frame> GetFrameStack() { return this.frameStack; }
    public void SetExecutionBudget(int executionBudget) { this.executionBudget = executionBudget; }
    public int GetExecutionBudget() { return this.executionBudget; }

    public Frame GetCurrentFrame() { return frameStack.isEmpty() ? null : frameStack.getLast(); }
    public boolean IsFinished() { return frameStack.isEmpty(); }
    public void PushFrame(Frame frame) { frameStack.addLast(frame); }

    public int OperandCount() { return operandStack.size(); }
    public VMObject Pop() { return operandStack.removeLast(); }
    public void Push(VMObject o) { operandStack.addLast(o); }
    public VMObject Peek() { return operandStack.getLast(); }
    public VMObject Peek(int depth) { return operandStack.get((operandStack.size() - 1) - depth); }

    public void Run() {

        while (executionBudget > 0 && !frameStack.isEmpty()) {
            var frame = frameStack.getLast();
            frame.Execute(this);
            if (frame.IsFinished()) frameStack.removeLast();
        }

    }

}
