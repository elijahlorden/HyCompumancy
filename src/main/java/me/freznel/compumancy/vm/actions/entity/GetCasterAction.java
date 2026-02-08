package me.freznel.compumancy.vm.actions.entity;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.execution.Caster;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.EntityRefObject;
import me.freznel.compumancy.vm.objects.NullObject;
import me.freznel.compumancy.vm.objects.Vector3Object;

public class GetCasterAction extends VMAction {

    @Override
    public int executionBudgetCost() {
        return 1;
    }

    @Override
    public void execute(Invocation invocation) {
        var caster = invocation.getCaster();
        if (caster == null || !caster.isValid()) { invocation.push(NullObject.NULL); return; }
        var type = caster.getType();
        if (type == Caster.CasterType.Entity) {
            //noinspection unchecked
            invocation.push(EntityRefObject.fromRef((Ref<EntityStore>)caster.GetRef()));
        } else if (type == Caster.CasterType.Block) {
            invocation.push(new Vector3Object(caster.getBlockPosition()));
        } else {
            invocation.push(NullObject.NULL);
        }

    }
}
