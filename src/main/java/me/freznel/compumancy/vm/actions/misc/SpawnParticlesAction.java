package me.freznel.compumancy.vm.actions.misc;

import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.Vector3Object;

public class SpawnParticlesAction extends VMAction {
    @Override
    public int executionBudgetCost() {
        return 50;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.getOperandCount() < 1) throw new StackUnderflowException("spawn-particle: Expected at least 1 operand");
        var a = invocation.pop();
        if (!(a instanceof Vector3Object vec)) throw new InvalidOperationException(String.format("spawn-particle: Expected Vector3, got %s", a.getObjectName()));
        invocation.assertInAmbit(vec.getX(), vec.getY(), vec.getZ());

        ParticleUtil.spawnParticleEffect("Block_Land_Soft_Crystal", vec.getVector3d(), invocation.getWorld().getEntityStore().getStore());
    }
}
