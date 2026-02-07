package me.freznel.compumancy.vm.compiler;

import java.util.Stack;

public interface ICompilerWord {
    public void compile(Tokenizer tokenizer, Stack<CompileList> stack);
}
