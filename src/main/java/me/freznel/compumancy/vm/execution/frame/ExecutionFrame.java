package me.freznel.compumancy.vm.execution.frame;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import me.freznel.compumancy.vm.exceptions.InvalidActionException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.objects.VMObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ExecutionFrame extends Frame {
    public static final BuilderCodec<ExecutionFrame> CODEC = BuilderCodec.builder(ExecutionFrame.class, ExecutionFrame::new)
            .append(new KeyedCodec<>("List", new ArrayCodec<VMObject>(VMObject.CODEC, VMObject[]::new)), ExecutionFrame::SetContentsArray, ExecutionFrame::GetContentsArray)
            .add()
            .build();

    static {
        Frame.CODEC.register("Exe", ExecutionFrame.class, CODEC);
    }

    private ArrayList<VMObject> contents;
    private int index;

    public ExecutionFrame() { this.contents = new ArrayList<>(); index = 0; }
    public ExecutionFrame(ArrayList<VMObject> contents) { this.contents = contents; index = 0; }
    public ExecutionFrame(ExecutionFrame other) {
        this.contents = new ArrayList<>(other.contents.size() - other.index);
        for (int i=other.index; i < other.contents.size(); i++) {
            this.contents.add(other.contents.get(i).clone());
        }
        this.index = 0;
    }

    public int GetIndex() { return index; }
    public void SetIndex(int index) { this.index = index; }

    public VMObject Peek() { return (index < contents.size()) ? contents.get(index) : null; }
    public VMObject Pop() { return (index < contents.size()) ? contents.get(index++) : null; }

    public VMObject[] GetContentsArray() {
        if (IsFinished()) return new VMObject[0];
        return this.contents.subList(index, this.contents.size()).toArray(new VMObject[0]); //Return only the objects that haven't been executed yet
    }

    public void SetContentsArray(VMObject[] contents) {
        this.contents = new ArrayList<>();
        this.contents.addAll(Arrays.asList(contents));
        index = 0;
    }

    @Override
    public int GetSize() {
        return Math.max(contents.size() - index, 0);
    }

    @Override
    public boolean IsFinished() { return contents.isEmpty() || index >= contents.size(); }

    @Override
    public void Execute(Invocation invocation)
    {
        if (IsFinished()) return;
        int budget = invocation.GetExecutionBudget();
        do {
            VMObject next = contents.get(index++);
            if (!(next instanceof IEvaluatable evaluatableNext)) throw new InvalidActionException("Attempted to execute a " + next.GetName());
            budget -= evaluatableNext.ExecutionBudgetCost();
            evaluatableNext.Evaluate(invocation);
        } while (invocation.GetCurrentFrame() == this && budget > 0 && index < contents.size()); //Execute until another frame is pushed or the budget runs out
        invocation.SetExecutionBudget(budget);
    }

    @Override
    public Frame clone() {
        return new ExecutionFrame(this);
    }
}