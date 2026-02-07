package me.freznel.compumancy.vm.execution;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import me.freznel.compumancy.vm.execution.frame.Frame;
import me.freznel.compumancy.vm.objects.VMObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class InvocationState implements Cloneable {
    public static final BuilderCodec<InvocationState> CODEC = BuilderCodec.builder(InvocationState.class, InvocationState::new)
            .append(new KeyedCodec<>("Id", Codec.UUID_BINARY), (o, v) -> o.id = v, o -> o.id)
            .add()
            .append(new KeyedCodec<>("Operands", new ArrayCodec<VMObject>(VMObject.CODEC, VMObject[]::new)), (o, v) -> o.operandStack.addAll(List.of(v)), o -> o.operandStack.toArray(new VMObject[0]))
            .add()
            .append(new KeyedCodec<>("Frames", new ArrayCodec<Frame>(Frame.CODEC, Frame[]::new)), (o, v) -> o.frameStack.addAll(List.of(v)), o -> o.frameStack.toArray(new Frame[0]))
            .add()
            .append(new KeyedCodec<>("Budget", Codec.INTEGER), (o, v) -> o.executionBudget = v == null ? 0 : v, o -> o.executionBudget)
            .add()
            .build();

    private final ArrayList<VMObject> operandStack;
    private final ArrayList<Frame> frameStack;
    private UUID id;
    private int executionBudget;
    private long lastRunTimestamp;

    public ArrayList<VMObject> getOperandStack() { return this.operandStack; }
    public ArrayList<Frame> getFrameStack() { return this.frameStack; }
    public int getExecutionBudget() { return this.executionBudget; }
    public UUID getId() { return this.id; }
    public long getLastRunTimestamp() { return this.lastRunTimestamp; }

    public InvocationState() {
        operandStack = new ArrayList<>();
        frameStack = new ArrayList<>();
    }

    public InvocationState(Invocation invocation) {
        var operands = invocation.getOperandStack();
        operandStack = new ArrayList<>(operands.size());
        for (var operand : operands) operandStack.add(operand.clone());

        var frames = invocation.getFrameStack();
        frameStack = new ArrayList<>(frames.size());
        for (var frame : frames) frameStack.add(frame.clone());

        executionBudget = invocation.getExecutionBudget();
        id = invocation.getId();
        lastRunTimestamp = invocation.getLastRunTimestamp();
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public InvocationState clone() {
        return this; //Immutable object
    }
}