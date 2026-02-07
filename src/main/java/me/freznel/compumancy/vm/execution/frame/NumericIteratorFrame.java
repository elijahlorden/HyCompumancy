package me.freznel.compumancy.vm.execution.frame;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import me.freznel.compumancy.vm.execution.FrameSyncType;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.interfaces.IExecutable;
import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;

public class NumericIteratorFrame extends IteratorFrame {
    public static final BuilderCodec<NumericIteratorFrame> CODEC = BuilderCodec.builder(NumericIteratorFrame.class, NumericIteratorFrame::new)
            .append(new KeyedCodec<>("Eval", VMObject.CODEC), NumericIteratorFrame::setEvaluatable, NumericIteratorFrame::getEvaluatable)
            .add()
            .append(new KeyedCodec<>("Start", Codec.INTEGER), NumericIteratorFrame::setStart, NumericIteratorFrame::getStart)
            .add()
            .append(new KeyedCodec<>("End", Codec.INTEGER), NumericIteratorFrame::getEnd, NumericIteratorFrame::getEnd)
            .add()
            .append(new KeyedCodec<>("Inc", Codec.INTEGER), NumericIteratorFrame::setIncrement, NumericIteratorFrame::getIncrement)
            .add()
            .append(new KeyedCodec<>("Current", Codec.INTEGER), NumericIteratorFrame::setCurrent, NumericIteratorFrame::getCurrent)
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

    public VMObject getEvaluatable() { return evaluatable; }
    public void setEvaluatable(VMObject evaluatable) { this.evaluatable = evaluatable; }

    public int getStart() { return start; }
    public void setStart(int start) { this.start = start; }

    public int getEnd() { return end; }
    public void getEnd(int end) { this.end = end; }

    public int getIncrement() { return inc; }
    public void setIncrement(int inc) { this.inc = inc; }

    public int getCurrent() { return current; }
    public void setCurrent(int current) { this.current = current; }

    @Override
    public int getSize() {
        return evaluatable.getObjectSize();
    }

    @Override
    public boolean isFinished() { return inc == 0 || ((inc > 0) ? current > end : current < end); }

    @Override
    public void execute(Invocation invocation, long interruptAt) {
        if (isFinished()) return;
        if (evaluatable != null) {
            invocation.push(new NumberObject(current));
            if (evaluatable instanceof IExecutable exe) exe.execute(invocation);
            else if (evaluatable instanceof IEvaluatable eval) eval.evaluate(invocation);
        }
        current += inc;
    }

    @Override
    public FrameSyncType getFrameSyncType() {
        if (isFinished()) return FrameSyncType.Neutral;
        if (evaluatable instanceof IExecutable exe && exe.isExecuteSynchronous()) return FrameSyncType.Sync;
        if (evaluatable instanceof IEvaluatable eval && eval.isEvalSynchronous()) return FrameSyncType.Sync;
        int remaining = (Math.abs(end - start) + 1) * evaluatable.getObjectSize();
        return (remaining > 10) ? FrameSyncType.Async : FrameSyncType.Neutral; //Only force a thread change for large frames
    }

    @Override
    public Frame clone() {
        return new NumericIteratorFrame(this);
    }
}
