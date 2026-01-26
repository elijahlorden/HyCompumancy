package me.freznel.compumancy.vm.compiler;

import me.freznel.compumancy.vm.objects.VMObject;

import java.util.ArrayList;

public class CompileList extends ArrayList<VMObject> {

    public int StartLine;

    public CompileList() { super(); }
    public CompileList(int line) { super(); StartLine = line; }

}
