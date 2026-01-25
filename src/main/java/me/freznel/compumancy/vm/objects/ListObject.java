package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.interfaces.IExecutable;

import java.util.ArrayList;
import java.util.Arrays;

public class ListObject extends VMObject implements IExecutable {
    public static final BuilderCodec<ListObject> CODEC = BuilderCodec.builder(ListObject.class, ListObject::new)
            .append(new KeyedCodec<>("List", new ArrayCodec<VMObject>(VMObject.CODEC, VMObject[]::new)), ListObject::SetContentsArray, ListObject::GetContentsArray)
            .add()
            .build();

    static {
        VMObject.CODEC.register("List", ListObject.class, CODEC);
    }

    private ArrayList<VMObject> contents;

    public ListObject() { contents = new ArrayList<>(); }
    public ListObject(ArrayList<VMObject> contents) { this.contents = contents; }

    public ArrayList<VMObject> GetContents() { return this.contents; }
    public void SetContents(ArrayList<VMObject> contents) { this.contents = contents; }

    public VMObject[] GetContentsArray() { return this.contents.toArray(new VMObject[0]); }
    public void SetContentsArray(VMObject[] contents) {
        this.contents = new ArrayList<>();
        this.contents.addAll(Arrays.asList(contents));
    }

    @Override
    public String GetName() {
        return "List";
    }

    @Override
    public int GetSize() {
        if (contents == null) return 0;
        int size = 0;
        for (VMObject obj : contents) {
            size += obj.GetSize();
        }
        return size;
    }

    @Override
    public String toString() {
        if (contents == null || contents.isEmpty()) return "{ }";
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for (int i=0; i<contents.size(); i++) {
            sb.append(contents.get(i).toString());
            if (i != contents.size() - 1) sb.append(", ");
        }
        sb.append(" }");
        return sb.toString();
    }

    @Override
    public VMObject clone() {
        if (contents == null) return new ListObject();
        ArrayList<VMObject> newList = new ArrayList<>();
        for (VMObject obj : contents) {
            newList.add(obj.clone());
        }
        return new ListObject(newList);
    }

    @Override
    public int ExecutionBudgetCost() {
        return contents != null ? contents.size() / 10 : 1;
    }

    @Override
    public void Execute(Invocation invocation) throws VMException {
        invocation.Push(this.clone());
    }
}
