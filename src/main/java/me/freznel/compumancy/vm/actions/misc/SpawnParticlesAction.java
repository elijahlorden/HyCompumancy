package me.freznel.compumancy.vm.actions.misc;

import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.Vector3Object;

public class SpawnParticlesAction extends VMAction {
    @Override
    public int ExecutionBudgetCost() {
        return 50;
    }

    @Override
    public void Execute(Invocation invocation) {
        if (invocation.OperandCount() < 1) throw new StackUnderflowException("spawn-particle: Expected at least 1 operand");
        var a = invocation.Pop();
        if (!(a instanceof Vector3Object vec)) throw new InvalidOperationException(String.format("spawn-particle: Expected Vector3, got %s", a.GetObjectName()));
        invocation.AssertInAmbit(vec.GetX(), vec.GetY(), vec.GetZ());

        ParticleUtil.spawnParticleEffect("Block_Land_Soft_Crystal", vec.GetVector3d(), invocation.GetWorld().getEntityStore().getStore());
    }
}
