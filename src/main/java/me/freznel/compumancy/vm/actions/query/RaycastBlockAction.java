package me.freznel.compumancy.vm.actions.query;

import com.hypixel.hytale.server.core.util.TargetUtil;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.NullObject;
import me.freznel.compumancy.vm.objects.Vector3Object;

//(position rotation - hitposition|null)
public class RaycastBlockAction extends VMAction {

    @Override
    public boolean isExecuteSynchronous() {
        return true;
    }

    @Override
    public int executionBudgetCost() {
        return 100;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.getOperandCount() < 2) throw new StackUnderflowException("raycast-block: Expected at least 2 operands");
        var b = invocation.pop(); //Rotation Vector3
        var a = invocation.pop(); //Position Vector3
        if (!(a instanceof Vector3Object posObj) || !(b instanceof Vector3Object rotObj)) throw new InvalidOperationException(String.format("raycast-block: expected Vector3 Vector3, got %s %s", a.getObjectName(), b.getObjectName()));
        double x = posObj.getX(), y = posObj.getY(), z = posObj.getZ();
        invocation.assertInAmbit(x, y, z);

        var result = TargetUtil.getTargetBlock(invocation.getWorld(), (id, fluidId) -> {
            return id != 0;
        }, x, y, z, rotObj.getX(), rotObj.getY(), rotObj.getZ(), 16);

        invocation.push(result == null ? NullObject.NULL : new Vector3Object(result.x, result.y, result.z));
    }
}
