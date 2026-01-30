package me.freznel.compumancy.vm.actions.entity;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.OutOfAmbitException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.EntityRefObject;
import me.freznel.compumancy.vm.objects.NullObject;
import me.freznel.compumancy.vm.objects.Vector3Object;

public class GetEntityPositionAction extends VMAction {
    @Override
    public int ExecutionBudgetCost() {
        return 0;
    }

    @Override
    public boolean ExecuteSynchronous() {
        return true;
    }

    @Override
    public void Execute(Invocation invocation) {
        var a = invocation.Pop(); //EntityRefObject
        if (!(a instanceof EntityRefObject refObj)) throw new InvalidOperationException("entity:get-position: expected Entity, got " + a.GetObjectName());
        var world = refObj.GetWorld();
        if (world != invocation.GetWorld()) throw new OutOfAmbitException("Attempted to get the position of an entity in another world");
        final Ref<EntityStore> ref = refObj.GetEntity();
        if (ref == null || !ref.isValid()) { invocation.Push(NullObject.NULL); return; }
        var store = ref.getStore();
        var transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) { invocation.Push(NullObject.NULL); return; }
        invocation.Push(new Vector3Object(transform.getPosition()));
    }
}
