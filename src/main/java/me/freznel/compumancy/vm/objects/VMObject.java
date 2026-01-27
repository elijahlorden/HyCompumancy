package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;

public abstract class VMObject implements Cloneable {
    public static final CodecMapCodec<VMObject> CODEC = new CodecMapCodec<>();

    public abstract String GetName();
    public abstract int GetSize();

    @Override
    public String toString()
    {
        return "Object";
    }

    @Override
    public abstract VMObject clone();

}