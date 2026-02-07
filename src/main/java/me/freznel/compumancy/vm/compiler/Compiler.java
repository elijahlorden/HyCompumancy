package me.freznel.compumancy.vm.compiler;

import com.hypixel.hytale.logger.HytaleLogger;
import me.freznel.compumancy.vm.exceptions.CompileException;
import me.freznel.compumancy.vm.objects.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class Compiler {
    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();
    private static final Map<String, ICompilerWord> compilerWords = new ConcurrentHashMap<>();

    public static void Register(String key, ICompilerWord word) {
        if (compilerWords.containsKey(key)) {
            Logger.at(Level.SEVERE).log(String.format("Attempted to register duplicate compiler word '%s'", key));
            return;
        }
        compilerWords.put(key, word);
    }

    public static ArrayList<VMObject> compile(String compileString) {
        Tokenizer tokenizer = new Tokenizer(compileString);
        Stack<CompileList> stack = new Stack<>();
        stack.push(new CompileList(1)); //Push top-level list

        Tokenizer.Token tkn;

        while ((tkn = tokenizer.next()) != null) {

            switch (tkn.type()) {
                case OpenBrace -> {
                    stack.push(new CompileList(tkn.line()));
                    if (stack.size() > 10) throw new CompileException(String.format("Line %d: Compile-time list depth exceeded 10", tkn.line()));
                }

                case CloseBrace -> {
                    if (stack.size() <= 1) throw new CompileException(String.format("Line %d: Expected {", tkn.line()));
                    var list = stack.pop();
                    stack.peek().addLast(new ListObject(list, list.executeSync));
                }

                case Number -> stack.peek().addLast(new NumberObject((double)tkn.value()));
                case String -> throw new CompileException("TODO: String objects");
                case Vector3 -> stack.peek().addLast((Vector3Object)tkn.value());

                case Word -> {
                    String wordKey = (String)tkn.value();
                    if (compilerWords.containsKey(wordKey)) {
                        compilerWords.get(wordKey).compile(tokenizer, stack);
                    } else if (Vocabulary.BASE.contains(wordKey)) {
                        var word = Vocabulary.BASE.get(wordKey);
                        var list = stack.peek();
                        word.addContentsToList(list);
                        list.executeSync |= word.isExecuteSync(); //Flag the list as executeSync if the word is executeSync
                    } else { //Late binding definition reference.  Will throw at runtime if definition is not present.
                        stack.peek().addLast(new DefinitionRefObject(wordKey));
                    }
                }

                case Invalid -> throw new CompileException(String.format("Line %d: Invalid token", tkn.line()));
                default -> throw new CompileException(String.format("Line %d: Unhandled token type '%s'", tkn.line(), tkn.type()));
            }

        }

        if (stack.size() > 1) { //Handle unterminated lists
            var list = stack.pop();
            throw new CompileException(String.format("Line %d: Unterminated list", list.startLine));
        }

        return stack.pop();
    }

}
