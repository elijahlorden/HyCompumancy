package me.freznel.compumancy.vm.actions;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.vm.compiler.Vocabulary;
import me.freznel.compumancy.vm.compiler.Word;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.OutOfAmbitException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.ActionObject;
import me.freznel.compumancy.vm.objects.EntityRefObject;

public final class ActionHelpers {

    public static void RegisterSimpleAction(String word, Class<? extends VMAction> cls, VMAction action) {
        VMAction.Register(word, action);
        Vocabulary.BASE.Add(word, new Word(new ActionObject(cls)));
    }

    public static Ref<EntityStore> GetSyncEntityArgument(Invocation invocation, String operationName) {
        var a = invocation.Pop(); //EntityRefObject
        if (!(a instanceof EntityRefObject refObj)) throw new InvalidOperationException(operationName + ": Expected Entity, got " + a.GetObjectName());
        var world = refObj.GetWorld();
        if (world != invocation.GetWorld()) throw new OutOfAmbitException(operationName + ": Attempted to get an entity in another world");
        final Ref<EntityStore> ref = refObj.GetEntity();
        if (ref == null || !ref.isValid()) return null;
        return ref;
    }



}
