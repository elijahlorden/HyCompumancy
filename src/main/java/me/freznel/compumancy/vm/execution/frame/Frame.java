package me.freznel.compumancy.vm.execution.frame;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import me.freznel.compumancy.vm.execution.FrameSyncType;
import me.freznel.compumancy.vm.execution.Invocation;

public abstract class Frame implements Cloneable {
    public static final CodecMapCodec<Frame> CODEC = new CodecMapCodec<>();

    public abstract int getSize();
    public abstract boolean isFinished();
    public abstract void execute(Invocation invocation, long interruptAt);

    public abstract FrameSyncType getFrameSyncType();

    @Override
    public abstract Frame clone();
}