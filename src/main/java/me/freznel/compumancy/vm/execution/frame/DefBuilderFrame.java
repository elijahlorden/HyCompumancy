package me.freznel.compumancy.vm.execution.frame;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import me.freznel.compumancy.vm.compiler.Word;
import me.freznel.compumancy.vm.exceptions.CompileException;
import me.freznel.compumancy.vm.execution.FrameSyncType;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.objects.DefinitionRefObject;
import me.freznel.compumancy.vm.objects.MetaObject;
import me.freznel.compumancy.vm.objects.VMObject;

import java.util.ArrayList;
import java.util.List;

public class DefBuilderFrame extends Frame {
    public static final BuilderCodec<DefBuilderFrame> CODEC = BuilderCodec.builder(DefBuilderFrame.class, DefBuilderFrame::new)
            .append(new KeyedCodec<>("List", new ArrayCodec<VMObject>(VMObject.CODEC, VMObject[]::new)), (o, v) -> o.contents = new ArrayList<>(List.of(v)), o -> o.contents.toArray(new VMObject[0]))
            .add()
            .append(new KeyedCodec<>("Sync", Codec.BOOLEAN), (o, v) -> o.evalSync = v, o -> o.evalSync)
            .add()
            .append(new KeyedCodec<>("Done", Codec.BOOLEAN), (o, v) -> o.done = v, o -> o.done)
            .add()
            .append(new KeyedCodec<>("Key", Codec.STRING), (o, v) -> o.key = v, o -> o.key)
            .add()
            .append(new KeyedCodec<>("Size", Codec.INTEGER), (o, v) -> o.size = v, o -> o.size)
            .add()
            .build();

    private boolean done;
    private boolean evalSync;
    private String key;
    private ArrayList<VMObject> contents;
    private int size;

    public DefBuilderFrame() { contents = new ArrayList<>(); }
    public DefBuilderFrame(String key) { contents = new ArrayList<>(); done = false; }
    public DefBuilderFrame(DefBuilderFrame other) {
        this.done = other.done;
        this.evalSync = other.evalSync;;
        this.key = other.key;
        this.size = other.size;
        this.contents = new ArrayList<>(other.contents.size());
        for (var obj : other.contents) {
            this.contents.add(obj.clone());
        }
    }

    @Override
    public int getSize() { return size; }

    @Override
    public boolean isFinished() { return done; }

    @Override
    public void execute(Invocation invocation, long interruptAt) {
        if (done) return;
        var frameStack = invocation.getFrameStack();
        if (frameStack.getLast() != this) throw new CompileException("Unknown error, definition builder frame not on top");
        if (frameStack.size() < 2) throw new CompileException("Definition failed, no execution frame found");
        var frame = frameStack.get(frameStack.size() - 2);
        if (!(frame instanceof ExecutionFrame exeFrame)) throw new CompileException("Definition failed, no execution frame found");

        VMObject obj;
        if (key == null) {
            if (exeFrame.isFinished()) throw new CompileException("Failed to start definition, no DefinitionRef");
            obj = exeFrame.pop();
            if (!(obj instanceof DefinitionRefObject defDef)) {
                throw new CompileException(String.format("Expected DefinitionRef, got %s", obj.getObjectName()));
            }
            key = defDef.GetDefName();
            if (key == null || key.trim().isEmpty()) throw new CompileException("Failed to start definition, DefinitionRef was empty");
        }

        if (exeFrame.isFinished()) throw new CompileException("Failed to start definition, no body after DefinitionRef");

        int count = 0;
        int balance = 0; //Track inner start/end def markers.  Only end the definition if a balanced end marker is encountered.
        while (count++ < 50) {
            if (exeFrame.isFinished()) throw new CompileException(String.format("Failed to complete definition '%s', end of frame", key));
            obj = exeFrame.pop();
            if (obj instanceof MetaObject meta) {
                var op = meta.getOperation();
                if (op == MetaObject.MetaOperation.StartDef) {
                    balance++;
                } else if (op == MetaObject.MetaOperation.EndDef) {
                    if (balance > 0) balance--;
                    else {
                        invocation.storeDefinition(key, new Word(contents, evalSync));
                        invocation.setCurrentExecutionBudget(0);
                        done = true;
                        return;
                    }
                }
            }
            size += obj.getObjectSize();
            if (size > 1024) throw new CompileException(String.format("Definition '%s' exceeded maximum size of 1024", key));
            contents.addLast(obj.clone());
            if (obj instanceof IEvaluatable eval) evalSync |= eval.isEvalSynchronous();
        }
    }

    @Override
    public FrameSyncType getFrameSyncType() { return FrameSyncType.Async; }

    @Override
    public Frame clone() { return new DefBuilderFrame(this); }
}
