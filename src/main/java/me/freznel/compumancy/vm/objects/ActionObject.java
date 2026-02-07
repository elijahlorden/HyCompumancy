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
            ActionObject::getActionName,
            VMAction::getObject
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

    public VMAction getActionRef() { return ref; }
    public String getActionName() { return actionName; }

    @Override
    public String getObjectName() { return "Action"; }

    @Override
    public String toString() { return (actionName != null && !actionName.isEmpty()) ? "Action: " + actionName : "Action"; }

    @Override
    public int getObjectSize() { return 1; }

    @Override
    public VMObject clone() {
        return this; //Immutable object
    }

    @Override
    public int executionBudgetCost() {
        return ref == null ? 1 : ref.executionBudgetCost();
    }

    @Override
    public void evaluate(Invocation invocation) throws VMException {
        if (ref == null) throw new InvalidActionException(String.format("The action '%s' was not found", actionName));
        ref.execute(invocation);
    }

    @Override
    public boolean isEvalSynchronous() {
        return ref != null && ref.isExecuteSynchronous();
    }
}
