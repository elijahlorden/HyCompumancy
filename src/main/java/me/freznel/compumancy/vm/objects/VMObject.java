package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;

public abstract class VMObject implements Cloneable {
    public static final CodecMapCodec<VMObject> CODEC = new CodecMapCodec<>();

    public abstract String getObjectName();
    public abstract int getObjectSize();

    @Override
    public abstract String toString();

    @Override
    public abstract VMObject clone();

}