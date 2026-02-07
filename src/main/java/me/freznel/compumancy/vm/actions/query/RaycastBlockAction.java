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
    public boolean ExecuteSynchronous() {
        return true;
    }

    @Override
    public int ExecutionBudgetCost() {
        return 100;
    }

    @Override
    public void Execute(Invocation invocation) {
        if (invocation.OperandCount() < 2) throw new StackUnderflowException("raycast-block: Expected at least 2 operands");
        var b = invocation.Pop(); //Rotation Vector3
        var a = invocation.Pop(); //Position Vector3
        if (!(a instanceof Vector3Object posObj) || !(b instanceof Vector3Object rotObj)) throw new InvalidOperationException(String.format("raycast-block: expected Vector3 Vector3, got %s %s", a.GetObjectName(), b.GetObjectName()));
        double x = posObj.GetX(), y = posObj.GetY(), z = posObj.GetZ();
        invocation.AssertInAmbit(x, y, z);

        var result = TargetUtil.getTargetBlock(invocation.GetWorld(), (id, fluidId) -> {
            return id != 0;
        }, x, y, z, rotObj.GetX(), rotObj.GetY(), rotObj.GetZ(), 16);

        invocation.Push(result == null ? NullObject.NULL : new Vector3Object(result.x, result.y, result.z));
    }
}
