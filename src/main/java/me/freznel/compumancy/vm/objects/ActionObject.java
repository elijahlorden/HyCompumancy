package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.logger.HytaleLogger;
import me.freznel.compumancy.codec.CustomMapCodec;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.InvalidActionException;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;

public final class ActionObject extends VMObject implements IEvaluatable {
    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();

    public static final CustomMapCodec<ActionObject> CODEC = new CustomMapCodec<>(
            ActionObject.class,
            "Ref",
            ActionObject::GetActionName,
            VMAction::GetObject
    );

    /*public static final BuilderCodec<ActionObject> CODEC = BuilderCodec.builder(ActionObject.class, ActionObject::new)
            .append(new KeyedCodec<>("Ref", Codec.STRING), ActionObject::SetActionName, ActionObject::GetActionName)
            .add()
            .build();*/

    private final String actionName;
    private final VMAction ref;

    public ActionObject(VMAction action, String actionName) {
        this.ref = action;
        this.actionName = actionName;
    }

    public VMAction GetActionRef() { return ref; }
    public String GetActionName() { return actionName; }

    @Override
    public String GetObjectName() { return "Action"; }

    @Override
    public String toString() { return (actionName != null && !actionName.isEmpty()) ? "Action: " + actionName : "Action"; }

    @Override
    public int GetObjectSize() { return 1; }

    @Override
    public VMObject clone() {
        return this; //Immutable object
    }

    @Override
    public int ExecutionBudgetCost() {
        return ref == null ? 1 : ref.ExecutionBudgetCost();
    }

    @Override
    public void Evaluate(Invocation invocation) throws VMException {
        if (ref == null) throw new InvalidActionException(String.format("The action '%s' was not found", actionName));
        ref.Execute(invocation);
    }

    @Override
    public boolean IsEvalSynchronous() {
        return ref != null && ref.ExecuteSynchronous();
    }
}
