package me.freznel.compumancy.vm.compiler;

import me.freznel.compumancy.vm.objects.VMObject;
import org.bouncycastle.jcajce.provider.asymmetric.CONTEXT;

import java.util.Arrays;
import java.util.List;

public class Word {

    private final VMObject[] contents;

    public Word(VMObject o) {
        this.contents = new VMObject[1];
        contents[0] = o;
    }

    public Word(VMObject... contents) {
        this.contents = contents;
    }

    public void AddContentsToList(List<VMObject> list) {
        for (var obj : contents) {
            list.add(obj.clone());
        }
    }

}
