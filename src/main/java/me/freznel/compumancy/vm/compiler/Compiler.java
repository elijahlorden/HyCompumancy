package me.freznel.compumancy.vm.compiler;

import me.freznel.compumancy.vm.exceptions.CompileException;
import me.freznel.compumancy.vm.objects.ListObject;
import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;

import java.util.ArrayList;
import java.util.Stack;

public class Compiler {

    public static ArrayList<VMObject> Compile(String compileString) {
        Tokenizer tokenizer = new Tokenizer(compileString);
        Stack<CompileList> stack = new Stack<>();
        stack.push(new CompileList(1)); //Push top-level list

        Tokenizer.Token tkn;

        while ((tkn = tokenizer.Next()) != null) {

            switch (tkn.type()) {
                case OpenBrace -> {
                    stack.push(new CompileList(tkn.line()));
                }

                case CloseBrace -> {
                    if (stack.size() <= 1) throw new CompileException(String.format("Line %d: Expected {", tkn.line()));
                    var list = stack.pop();
                    stack.peek().addLast(new ListObject(list));
                }

                case Number -> stack.peek().addLast(new NumberObject((double)tkn.value()));
                case String -> throw new CompileException("TODO: String objects");
                case Vector3 -> throw new CompileException("TODO: Vector3 objects");

                case Word -> { //TODO: Implement late binding WordRefObject for words not found in the Vocabulary
                    var word = Vocabulary.Get((String)tkn.value());
                    if (word == null) throw new CompileException(String.format("Line %d: Unresolved word '%s'", tkn.line(), tkn.value()));
                    word.AddContentsToList(stack.peek());
                }

                case Invalid -> throw new CompileException(String.format("Line %d: Invalid token", tkn.line()));
                default -> throw new CompileException(String.format("Line %d: Unhandled token type '%s'", tkn.line(), tkn.type().toString()));
            }

        }

        if (stack.size() > 1) { //Handle unterminated lists
            var list = stack.pop();
            throw new CompileException(String.format("Line %d: Unterminated list", list.StartLine));
        }

        return stack.pop();
    }

}
