package me.freznel.compumancy.vm.compiler;

import me.freznel.compumancy.vm.objects.VMObject;

import java.util.ArrayList;

public class CompileList extends ArrayList<VMObject> {

    public int startLine;
    public boolean executeSync;

    public CompileList() { super(); }
    public CompileList(int line) { super(); startLine = line; }

}
