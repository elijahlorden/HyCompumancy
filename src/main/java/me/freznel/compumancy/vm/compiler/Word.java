package me.freznel.compumancy.vm.compiler;

import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.objects.VMObject;
import org.bouncycastle.jcajce.provider.asymmetric.CONTEXT;

import java.util.Arrays;
import java.util.List;

public class Word {

    private final VMObject[] contents;
    private boolean executeSync;

    public Word(VMObject o) {
        this.contents = new VMObject[1];
        contents[0] = o;
        executeSync = (o instanceof IEvaluatable eval && eval.IsEvalSynchronous());
    }

    public Word(VMObject... contents) {
        this.contents = contents;
        boolean sync = false;
        for (var obj : contents) {
            sync = (obj instanceof IEvaluatable eval && eval.IsEvalSynchronous());
            if (sync) break;
        }

    }

    public boolean GetExecuteSync() { return this.executeSync; }

    public void AddContentsToList(List<VMObject> list) {
        for (var obj : contents) {
            list.add(obj.clone());
        }
    }

}
