package me.freznel.compumancy.vm.execution.frame;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import me.freznel.compumancy.vm.compiler.CompileList;
import me.freznel.compumancy.vm.compiler.Compiler;
import me.freznel.compumancy.vm.execution.FrameSyncType;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.VMObject;

public class CompileFrame extends Frame {
    public static final BuilderCodec<CompileFrame> CODEC = BuilderCodec.builder(CompileFrame.class, CompileFrame::new)
            .append(new KeyedCodec<>("String", Codec.STRING), (o, v) -> o.compileString = v, o -> o.compileString)
            .add()
            .append(new KeyedCodec<>("Done", Codec.BOOLEAN), (o, v) -> o.done = v, o -> o.done)
            .add()
            .build();

    private String compileString;
    private boolean done;

    public CompileFrame() { done = true; }
    public CompileFrame(String compileString) { done = false; this.compileString = compileString; }
    public CompileFrame(CompileFrame other) {
        compileString = other.compileString;
        done = other.done;
    }

    @Override
    public int GetSize() {
        return compileString == null ? 1 : (int)Math.ceil(Math.sqrt(compileString.length()));
    }

    @Override
    public boolean IsFinished() {
        return done;
    }

    @Override
    public void Execute(Invocation invocation, long interruptAt) {
        var program = Compiler.Compile(compileString);
        if (!program.isEmpty()) invocation.PushFrame(new ExecutionFrame(program));
        done = true;
    }

    @Override
    public FrameSyncType GetFrameSyncType() {
        return FrameSyncType.Async; //Never invoke the compiler on the world thread
    }

    @Override
    public Frame clone() {
        return new CompileFrame(this);
    }
}
