package me.freznel.compumancy.vm.compiler;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import me.freznel.compumancy.vm.execution.frame.ExecutionFrame;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.objects.VMObject;

import java.util.ArrayList;
import java.util.List;

public final class Word implements Cloneable {
    public static final BuilderCodec<Word> CODEC = BuilderCodec.builder(Word.class, Word::new)
            .append(new KeyedCodec<>("List", new ArrayCodec<VMObject>(VMObject.CODEC, VMObject[]::new)), (o, v) -> o.contents = v, o -> o.contents)
            .add()
            .append(new KeyedCodec<>("ExeSync", Codec.BOOLEAN), (o, v) -> o.executeSync = v, o -> o.executeSync)
            .add()
            .build();

    private VMObject[] contents;
    private boolean executeSync;

    public Word(VMObject o) {
        this.contents = new VMObject[1];
        contents[0] = o;
        executeSync = (o instanceof IEvaluatable eval && eval.isEvalSynchronous());
    }

    public Word(VMObject... contents) {
        this.contents = contents;
        boolean sync = false;
        for (var obj : contents) {
            sync = (obj instanceof IEvaluatable eval && eval.isEvalSynchronous());
            if (sync) break;
        }
        executeSync = sync;
    }

    public Word(ArrayList<VMObject> contents, boolean executeSync) {
        this.executeSync = executeSync;
        this.contents = contents.toArray(new VMObject[0]);
    }

    public int getSize() { return this.contents.length; }

    public boolean isExecuteSync() { return this.executeSync; }

    public void addContentsToList(List<VMObject> list) {
        for (var obj : contents) {
            list.add(obj.clone());
        }
    }

    public ExecutionFrame toExecutionFrame() {
        var frame = new ExecutionFrame();
        frame.setContentsArray(this.contents);
        frame.setExecuteSync(this.executeSync);
        return frame;
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Word clone() {
        return this; //Immutable object
    }
}
