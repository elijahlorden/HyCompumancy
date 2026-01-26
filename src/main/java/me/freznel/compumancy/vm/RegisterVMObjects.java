package me.freznel.compumancy.vm;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.actions.stack.DuplicateAction;
import me.freznel.compumancy.vm.compiler.Vocabulary;
import me.freznel.compumancy.vm.compiler.Word;
import me.freznel.compumancy.vm.objects.ActionObject;
import me.freznel.compumancy.vm.objects.BinaryOperatorObject;
import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.UnaryOperatorObject;
import me.freznel.compumancy.vm.operators.BinaryOperator;
import me.freznel.compumancy.vm.operators.BinaryOperatorSet;
import me.freznel.compumancy.vm.operators.UnaryOperator;
import me.freznel.compumancy.vm.operators.UnaryOperatorSet;
import me.freznel.compumancy.vm.operators.binary.NumberNumberBinaryOperatorSet;
import me.freznel.compumancy.vm.operators.unary.NumberUnaryOperatorSet;

public class RegisterVMObjects {

    public static void Register() {

        //Unary Operators
        var lenWord = new Word(new UnaryOperatorObject(UnaryOperator.Length));
        Vocabulary.Register("len", lenWord);
        Vocabulary.Register("abs", lenWord);
        Vocabulary.Register("negate", new Word(new UnaryOperatorObject(UnaryOperator.Negate)));

        //Binary Operators
        Vocabulary.Register("+", new Word(new BinaryOperatorObject(BinaryOperator.Add)));
        Vocabulary.Register("-", new Word(new BinaryOperatorObject(BinaryOperator.Subtract)));
        Vocabulary.Register("*", new Word(new BinaryOperatorObject(BinaryOperator.Multiply)));
        Vocabulary.Register("/", new Word(new BinaryOperatorObject(BinaryOperator.Divide)));
        Vocabulary.Register("mod", new Word(new BinaryOperatorObject(BinaryOperator.Mod)));

        RegisterOperatorSets();
        RegisterActions();
    }

    private static void RegisterOperatorSets() {
        //Number operator sets
        UnaryOperatorSet.Register(new NumberUnaryOperatorSet(), NumberObject.class);
        BinaryOperatorSet.Register(new NumberNumberBinaryOperatorSet(), NumberObject.class, NumberObject.class);

        //Boolean operator sets

        //String operator sets

        //List operator sets

    }

    private static void RegisterActions() {
        //Stack actions
        VMAction.Register("dup", new DuplicateAction());
        Vocabulary.Register("dup", new Word(new ActionObject(DuplicateAction.class)));




    }

}
