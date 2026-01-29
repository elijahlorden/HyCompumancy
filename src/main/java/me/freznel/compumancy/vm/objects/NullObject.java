package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class NullObject extends VMObject {
    public static final NullObject NULL = new NullObject();

    public static final BuilderCodec<NumberObject> CODEC = BuilderCodec.builder(NumberObject.class, NumberObject::new)
            .build();

    static {
        VMObject.CODEC.register("Null", NullObject.class, CODEC);
    }

    @Override
    public String GetName() { return "Null"; }

    @Override
    public String toString() { return "null"; }

    @Override
    public int GetSize() { return 1; }

    @Override
    public VMObject clone() {
        return new NullObject();
    }

}
