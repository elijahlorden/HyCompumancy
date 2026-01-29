package me.freznel.compumancy.vm.execution.frame;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import me.freznel.compumancy.vm.execution.Invocation;

public abstract class Frame implements Cloneable {
    public static final CodecMapCodec<Frame> CODEC = new CodecMapCodec<>();

    public abstract int GetSize();
    public abstract boolean IsFinished();
    public abstract void Execute(Invocation invocation);

    @Override
    public abstract Frame clone();
}