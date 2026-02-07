package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public final class NullObject extends VMObject {
    public static final NullObject NULL = new NullObject();

    public static final BuilderCodec<NullObject> CODEC = BuilderCodec.builder(NullObject.class, () -> NULL)
            .build();

    @Override
    public String getObjectName() { return "Null"; }

    @Override
    public String toString() { return "null"; }

    @Override
    public int getObjectSize() { return 1; }

    @Override
    public VMObject clone() {
        return NULL;
    }

}
