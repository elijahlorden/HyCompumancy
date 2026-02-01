package me.freznel.compumancy.vm.execution.frame;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import me.freznel.compumancy.vm.exceptions.CompileException;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.execution.FrameSyncType;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.DefinitionRefObject;
import me.freznel.compumancy.vm.objects.VMObject;

import java.util.ArrayList;
import java.util.List;

public class DefBuilderFrame extends Frame {
    public static final BuilderCodec<DefBuilderFrame> CODEC = BuilderCodec.builder(DefBuilderFrame.class, DefBuilderFrame::new)
            .append(new KeyedCodec<>("List", new ArrayCodec<VMObject>(VMObject.CODEC, VMObject[]::new)), (o, v) -> o.contents = new ArrayList<>(List.of(v)), o -> o.contents.toArray(new VMObject[0]))
            .add()
            .append(new KeyedCodec<>("Done", Codec.BOOLEAN), (o, v) -> o.done = v, o -> o.done)
            .add()
            .append(new KeyedCodec<>("Key", Codec.STRING), (o, v) -> o.key = v, o -> o.key)
            .add()
            .build();

    private boolean done;
    private String key;
    private ArrayList<VMObject> contents;

    public DefBuilderFrame() { contents = new ArrayList<>(); }
    public DefBuilderFrame(String key) { contents = new ArrayList<>(); done = false; }
    public DefBuilderFrame(DefBuilderFrame other) {
        this.done = other.done;
        this.key = other.key;
        this.contents = new ArrayList<>(other.contents.size());
        for (var obj : other.contents) {
            this.contents.add(obj.clone());
        }
    }

    @Override
    public int GetSize() { return contents.size(); }

    @Override
    public boolean IsFinished() { return done; }

    @Override
    public void Execute(Invocation invocation, long interruptAt) {
        if (done) return;
        var frameStack = invocation.GetFrameStack();
        if (frameStack.getLast() != this) throw new CompileException("Unknown error, definition builder frame not on top");
        if (frameStack.size() < 2) throw new CompileException("Definition failed, no execution frame found");
        var frame = frameStack.get(frameStack.size() - 2);
        if (!(frame instanceof ExecutionFrame exeFrame)) throw new CompileException("Definition failed, no execution frame found");

        VMObject obj;
        if (key == null) {
            if (exeFrame.IsFinished()) throw new CompileException("Failed to start definition, no DefinitionRef");
            obj = exeFrame.Pop();
            if (!(obj instanceof DefinitionRefObject defDef)) {
                throw new CompileException(String.format("Expected DefinitionRef, got %s", obj.GetObjectName()));
            }
            key = defDef.GetDefName();
            if (key == null || key.trim().isEmpty()) throw new CompileException("Failed to start definition, DefinitionRef was empty");
        }

        if (exeFrame.IsFinished()) throw new CompileException("Failed to start definition, no body after DefinitionRef");

        int count = 0;
        while (count++ < 50) {
            if (exeFrame.IsFinished()) throw new CompileException(String.format("Failed to complete definition '%s', end of frame", key));
            obj = exeFrame.Pop();

        }

        if (done) invocation.SetCurrentExecutionBudget(0); //This is an expensive operation, only one per execution
    }

    @Override
    public FrameSyncType GetFrameSyncType() { return FrameSyncType.Async; }

    @Override
    public Frame clone() { return new DefBuilderFrame(this); }
}
