package me.freznel.compumancy.vm.execution;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;

public abstract class Frame {
    public static final CodecMapCodec<Frame> CODEC = new CodecMapCodec<>();

    public  abstract int GetSize();
    public abstract boolean IsFinished();
    public abstract void Execute(Invocation invocation);
}
