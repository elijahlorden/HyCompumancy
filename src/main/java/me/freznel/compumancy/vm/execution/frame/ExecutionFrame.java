package me.freznel.compumancy.vm.execution.frame;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import me.freznel.compumancy.vm.exceptions.InvalidActionException;
import me.freznel.compumancy.vm.execution.FrameSyncType;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.objects.ListObject;
import me.freznel.compumancy.vm.objects.VMObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ExecutionFrame extends Frame {
    public static final BuilderCodec<ExecutionFrame> CODEC = BuilderCodec.builder(ExecutionFrame.class, ExecutionFrame::new)
            .append(new KeyedCodec<>("List", new ArrayCodec<VMObject>(VMObject.CODEC, VMObject[]::new)), ExecutionFrame::SetContentsArray, ExecutionFrame::GetContentsArray)
            .add()
            .append(new KeyedCodec<>("ExeSync", Codec.BOOLEAN), ExecutionFrame::SetExecuteSync, ExecutionFrame::GetExecuteSync)
            .add()
            .build();

    private ArrayList<VMObject> contents;
    private int index;
    private boolean executeSync;

    public ExecutionFrame() { this.contents = new ArrayList<>(); index = 0; executeSync = false; }
    public ExecutionFrame(ArrayList<VMObject> contents) { this.contents = contents; index = 0; CalcExecuteSync(); }
    public ExecutionFrame(ArrayList<VMObject> contents, boolean executeSync) { this.contents = contents; index = 0; this.executeSync = executeSync; }
    public ExecutionFrame(ExecutionFrame other) {
        this.contents = new ArrayList<>(other.contents.size() - other.index);
        for (int i=other.index; i < other.contents.size(); i++) {
            var obj = other.contents.get(i);
            this.contents.add(obj.clone());
            executeSync |= (obj instanceof IEvaluatable eval && eval.IsEvalSynchronous());
        }
        this.index = 0;
        this.executeSync = other.GetExecuteSync();
    }

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

    public void SetContents(ArrayList<VMObject> contents) {
        this.contents = contents;
        index = 0;
    }

    @Override
    public int GetSize() {
        return Math.max(contents.size() - index, 0);
    }

    public boolean GetExecuteSync() { return this.executeSync; }
    public void SetExecuteSync(boolean executeSync) { this.executeSync = executeSync; }

    public void CalcExecuteSync() {
        for (int i=index; i<contents.size(); i++) {
            var obj = contents.get(i);
            executeSync |= (obj instanceof IEvaluatable eval && eval.IsEvalSynchronous());
            if (executeSync) break;
        }
    }

    @Override
    public boolean IsFinished() { return contents.isEmpty() || index >= contents.size(); }

    @Override
    public void Execute(Invocation invocation, long interruptAt)
    {
        if (IsFinished()) return;
        int budget = invocation.GetCurrentExecutionBudget();
        boolean executingSync = invocation.IsRunningSync();
        do {
            VMObject next = contents.get(index);
            if (!(next instanceof IEvaluatable evaluatableNext)) throw new InvalidActionException("Attempted to execute a " + next.GetObjectName());
            if (!executingSync && evaluatableNext.IsEvalSynchronous()) { executeSync = true; break; } //Stop if the next word requires sync and not currently sync
            budget -= evaluatableNext.ExecutionBudgetCost();
            evaluatableNext.Evaluate(invocation);
            index++;
        } while (invocation.GetCurrentFrame() == this && budget > 0 && index < contents.size()); //Execute until another frame is pushed or the budget runs out
        invocation.SetCurrentExecutionBudget(budget);
    }

    @Override
    public FrameSyncType GetFrameSyncType() {
        if (IsFinished()) return FrameSyncType.Neutral;
        if (executeSync) return FrameSyncType.Sync;
        if (GetSize() > 10) return FrameSyncType.Async; //Only force a thread change for large frames
        return FrameSyncType.Neutral;
    }

    @Override
    public Frame clone() {
        return new ExecutionFrame(this);
    }

    //Create a full copy of the frame regardless of current index
    public ExecutionFrame CopyFull() {
        var newFrame = new ExecutionFrame();
        newFrame.SetContents(this.contents);
        newFrame.SetExecuteSync(this.executeSync);
        return newFrame;
    }
}