package me.freznel.compumancy.vm.compiler;

import java.util.Stack;

public interface ICompilerWord {
    public void Compile(Tokenizer tokenizer, Stack<CompileList> stack);
}
