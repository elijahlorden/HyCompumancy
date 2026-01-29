package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class NullObject extends VMObject {
    public static final NullObject NULL = new NullObject();

    public static final BuilderCodec<NumberObject> CODEC = BuilderCodec.builder(NumberObject.class, NumberObject::new)
            .build();

    @Override
    public String GetObjectName() { return "Null"; }

    @Override
    public String toString() { return "null"; }

    @Override
    public int GetObjectSize() { return 1; }

    @Override
    public VMObject clone() {
        return new NullObject();
    }

}
