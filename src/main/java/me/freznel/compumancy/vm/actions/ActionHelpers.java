package me.freznel.compumancy.vm.actions;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.vm.compiler.Vocabulary;
import me.freznel.compumancy.vm.compiler.Word;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.OutOfAmbitException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.EntityRefObject;

public final class ActionHelpers {

    public static void registerSimpleAction(String word, Class<? extends VMAction> cls, VMAction action) {
        VMAction.register(word, action);
        Vocabulary.BASE.add(word, new Word(VMAction.getObject(cls)));
    }

    public static Ref<EntityStore> getSyncEntityArgument(Invocation invocation, String operationName) {
        var a = invocation.pop(); //EntityRefObject
        if (!(a instanceof EntityRefObject refObj)) throw new InvalidOperationException(operationName + ": Expected Entity, got " + a.getObjectName());
        var world = refObj.getWorld();
        if (world != invocation.getWorld()) throw new OutOfAmbitException(operationName + ": Attempted to get an entity in another world");
        final Ref<EntityStore> ref = refObj.getEntity();
        if (ref == null || !ref.isValid()) return null;
        return ref;
    }



}
