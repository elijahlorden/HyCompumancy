package me.freznel.compumancy.vm.execution.frame;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.interfaces.IExecutable;
import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;

public class NumericIteratorFrame extends IteratorFrame {
    public static final BuilderCodec<NumericIteratorFrame> CODEC = BuilderCodec.builder(NumericIteratorFrame.class, NumericIteratorFrame::new)
            .append(new KeyedCodec<>("Eval", VMObject.CODEC), NumericIteratorFrame::SetEvaluatable, NumericIteratorFrame::GetEvaluatable)
            .add()
            .append(new KeyedCodec<>("Start", Codec.INTEGER), NumericIteratorFrame::SetStart, NumericIteratorFrame::GetStart)
            .add()
            .append(new KeyedCodec<>("End", Codec.INTEGER), NumericIteratorFrame::SetEnd, NumericIteratorFrame::GetEnd)
            .add()
            .append(new KeyedCodec<>("Inc", Codec.INTEGER), NumericIteratorFrame::SetIncrement, NumericIteratorFrame::GetIncrement)
            .add()
            .append(new KeyedCodec<>("Current", Codec.INTEGER), NumericIteratorFrame::SetCurrent, NumericIteratorFrame::GetCurrent)
            .add()
            .build();

    private VMObject evaluatable;
    private int start;
    private int end;
    private int inc;
    private int current;

    public NumericIteratorFrame() { }
    public NumericIteratorFrame(int start, int end, int inc, VMObject evaluatable) {
        this.start = start;
        this.end = end;
        this.inc = inc;
        this.current = start;
        this.evaluatable = evaluatable;
    }
    public NumericIteratorFrame(NumericIteratorFrame other) {
        this.start = other.start;
        this.end = other.end;
        this.inc = other.inc;
        this.current = other.current;
        this.evaluatable = other.evaluatable.clone();
    }

    public VMObject GetEvaluatable() { return evaluatable; }
    public void SetEvaluatable(VMObject evaluatable) { this.evaluatable = evaluatable; }

    public int GetStart() { return start; }
    public void SetStart(int start) { this.start = start; }

    public int GetEnd() { return end; }
    public void SetEnd(int end) { this.end = end; }

    public int GetIncrement() { return inc; }
    public void SetIncrement(int inc) { this.inc = inc; }

    public int GetCurrent() { return current; }
    public void SetCurrent(int current) { this.current = current; }

    @Override
    public int GetSize() {
        return evaluatable.GetObjectSize();
    }

    @Override
    public boolean IsFinished() { return inc == 0 || ((inc > 0) ? current > end : current < end); }

    @Override
    public void Execute(Invocation invocation, long interruptAt) {
        if (IsFinished()) return;
        if (evaluatable != null) {
            invocation.Push(new NumberObject(current));
            if (evaluatable instanceof IExecutable exe) exe.Execute(invocation);
            else if (evaluatable instanceof IEvaluatable eval) eval.Evaluate(invocation);
        }
        current += inc;
    }

    @Override
    public Frame clone() {
        return new NumericIteratorFrame(this);
    }
}
