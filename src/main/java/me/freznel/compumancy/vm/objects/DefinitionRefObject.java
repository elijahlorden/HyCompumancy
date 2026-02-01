package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.interfaces.IExecutable;

public final class DefinitionRefObject extends VMObject implements IEvaluatable, IExecutable {
    public static final BuilderCodec<DefinitionRefObject> CODEC = BuilderCodec.builder(DefinitionRefObject.class, DefinitionRefObject::new)
            .append(new KeyedCodec<>("Def", Codec.STRING), (o, v) -> o.def = v, o -> o.def)
            .add()
            .build();

    private String def;

    public DefinitionRefObject() { }
    public DefinitionRefObject(String def) { this.def = def; }

    public String GetDefName() { return def; }

    @Override
    public String GetObjectName() {
        return "DefinitionRef";
    }

    @Override
    public int GetObjectSize() {
        return def == null ? 1 : 1 + (def.length() / 50);
    }

    @Override
    public String toString() {
        return def == null ? "Def []" : String.format("Def [%s]", def);
    }

    @Override
    public VMObject clone() {
        return this; //Immutable object
    }

    @Override
    public int ExecutionBudgetCost() {
        return 1;
    }

    @Override
    public void Execute(Invocation invocation) throws VMException {
        invocation.ExecuteDefinition(def);
    }

    @Override
    public boolean IsExecuteSynchronous() {
        return false;
    }

    @Override
    public void Evaluate(Invocation invocation) throws VMException {
        invocation.ExecuteDefinition(def);
    }

    @Override
    public boolean IsEvalSynchronous() {
        return false;
    }
}
