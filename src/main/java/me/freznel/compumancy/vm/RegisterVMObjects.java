package me.freznel.compumancy.vm;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.actions.entity.GetCasterAction;
import me.freznel.compumancy.vm.actions.entity.SendMessageAction;
import me.freznel.compumancy.vm.actions.stack.DropAction;
import me.freznel.compumancy.vm.actions.stack.DuplicateAction;
import me.freznel.compumancy.vm.actions.stack.EvalAction;
import me.freznel.compumancy.vm.actions.stack.SelectAction;
import me.freznel.compumancy.vm.compiler.Vocabulary;
import me.freznel.compumancy.vm.compiler.Word;
import me.freznel.compumancy.vm.objects.*;
import me.freznel.compumancy.vm.operators.*;
import me.freznel.compumancy.vm.operators.binary.*;
import me.freznel.compumancy.vm.operators.unary.*;

import java.nio.channels.SelectableChannel;

public class RegisterVMObjects {

    public static void Register() {

        //Unary Operators
        Vocabulary.Register("len", new Word(new UnaryOperatorObject(UnaryOperator.Length)));
        Vocabulary.RegisterAlias("len", "abs");
        Vocabulary.Register("negate", new Word(new UnaryOperatorObject(UnaryOperator.SignedNegate)));
        Vocabulary.Register("not", new Word(new UnaryOperatorObject(UnaryOperator.UnsignedNegate)));

        //Binary Operators
        Vocabulary.Register("+", new Word(new BinaryOperatorObject(BinaryOperator.Add)));
        Vocabulary.Register("-", new Word(new BinaryOperatorObject(BinaryOperator.Subtract)));
        Vocabulary.Register("*", new Word(new BinaryOperatorObject(BinaryOperator.Multiply)));
        Vocabulary.Register("/", new Word(new BinaryOperatorObject(BinaryOperator.Divide)));
        Vocabulary.Register("mod", new Word(new BinaryOperatorObject(BinaryOperator.Mod)));

        Vocabulary.Register("and", new Word(new BinaryOperatorObject(BinaryOperator.And)));
        Vocabulary.Register("nand", new Word(new BinaryOperatorObject(BinaryOperator.Nand)));
        Vocabulary.Register("or", new Word(new BinaryOperatorObject(BinaryOperator.Or)));
        Vocabulary.Register("xor", new Word(new BinaryOperatorObject(BinaryOperator.Xor)));

        RegisterOperatorSets();
        RegisterActions();
    }

    private static void RegisterOperatorSets() {
        //Number operator sets
        UnaryOperatorSet.Register(new NumberUnaryOperatorSet(), NumberObject.class);
        BinaryOperatorSet.Register(new NumberNumberBinaryOperatorSet(), NumberObject.class, NumberObject.class);

        //Boolean operator sets
        UnaryOperatorSet.Register(new BooleanUnaryOperatorSet(), BoolObject.class);
        BinaryOperatorSet.Register(new BooleanBooleanBinaryOperatorSet(), BoolObject.class, BoolObject.class);

        //Boolean/Number and Number/Boolean operator sets
        BinaryOperatorSet.Register(new BooleanNumberBinaryOperatorSet(), BoolObject.class, NumberObject.class);
        BinaryOperatorSet.Register(new NumberBooleanBinaryOperatorSet(), NumberObject.class, BoolObject.class);

        //String operator sets

        //List operator sets

    }

    private static void RegisterActions() {
        //Stack actions
        VMAction.Register("dup", new DuplicateAction());
        Vocabulary.Register("dup", new Word(new ActionObject(DuplicateAction.class)));

        VMAction.Register("drop", new DropAction());
        Vocabulary.Register("drop", new Word(new ActionObject(DropAction.class)));

        //Logic actions
        Vocabulary.Register("true", new Word(BoolObject.TRUE));
        Vocabulary.Register("false", new Word(BoolObject.FALSE));

        VMAction.Register("?", new SelectAction());
        Vocabulary.Register("?", new Word(new ActionObject(SelectAction.class)));

        //Flow control actions
        VMAction.Register("eval", new EvalAction());
        Vocabulary.Register("eval", new Word(new ActionObject(EvalAction.class)));

        //Entity actions
        VMAction.Register("caster", new GetCasterAction());
        Vocabulary.Register("caster", new Word(new ActionObject(GetCasterAction.class)));

        VMAction.Register("send-message", new SendMessageAction());
        Vocabulary.Register("send-message", new Word(new ActionObject(SendMessageAction.class)));

    }

}
