package me.freznel.compumancy.vm;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.actions.entity.*;
import me.freznel.compumancy.vm.actions.stack.*;
import me.freznel.compumancy.vm.compiler.Vocabulary;
import me.freznel.compumancy.vm.compiler.Word;
import me.freznel.compumancy.vm.execution.frame.ExecutionFrame;
import me.freznel.compumancy.vm.execution.frame.Frame;
import me.freznel.compumancy.vm.execution.frame.NumericIteratorFrame;
import me.freznel.compumancy.vm.objects.*;
import me.freznel.compumancy.vm.operators.*;
import me.freznel.compumancy.vm.operators.binary.*;
import me.freznel.compumancy.vm.operators.unary.*;

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

        RegisterCodecs();
        RegisterOperatorSets();
        RegisterActions();
    }

    private static void RegisterCodecs() {
        //Objects
        VMObject.CODEC.register("EntityRef", EntityRefObject.class, EntityRefObject.CODEC);
        VMObject.CODEC.register("Bool", BoolObject.class, BoolObject.CODEC);
        VMObject.CODEC.register("ACT", ActionObject.class, ActionObject.CODEC);
        VMObject.CODEC.register("OP2", BinaryOperatorObject.class, BinaryOperatorObject.CODEC);
        VMObject.CODEC.register("List", ListObject.class, ListObject.CODEC);
        VMObject.CODEC.register("Null", NullObject.class, NullObject.CODEC);
        VMObject.CODEC.register("Number", NumberObject.class, NumberObject.CODEC);
        VMObject.CODEC.register("OP1", UnaryOperatorObject.class, UnaryOperatorObject.CODEC);
        VMObject.CODEC.register("Vector3", Vector3Object.class, Vector3Object.CODEC);

        //Frames
        Frame.CODEC.register("Exe", ExecutionFrame.class, ExecutionFrame.CODEC);
        Frame.CODEC.register("Numeric", NumericIteratorFrame.class, NumericIteratorFrame.CODEC);

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

    private static void RegisterSimpleAction(String word, Class<? extends VMAction> cls, VMAction action) {
        VMAction.Register(word, action);
        Vocabulary.Register(word, new Word(new ActionObject(cls)));
    }

    private static void RegisterActions() {
        //Stack actions
        RegisterSimpleAction("dup", DuplicateAction.class, new DuplicateAction());
        RegisterSimpleAction("drop", DropAction.class, new DropAction());
        RegisterSimpleAction("swap", SwapAction.class, new SwapAction());
        RegisterSimpleAction("over", OverAction.class, new OverAction());

        //Logic actions
        Vocabulary.Register("true", new Word(BoolObject.TRUE));
        Vocabulary.Register("false", new Word(BoolObject.FALSE));

        RegisterSimpleAction("?", SelectAction.class, new SelectAction());

        //Flow control actions
        RegisterSimpleAction("eval", EvalAction.class, new EvalAction());
        RegisterSimpleAction("for", ForAction.class, new ForAction());

        //Entity actions
        RegisterSimpleAction("caster", GetCasterAction.class, new GetCasterAction());
        RegisterSimpleAction("send-message", SendMessageAction.class, new SendMessageAction());
        RegisterSimpleAction("entity:get-position", GetEntityPositionAction.class, new GetEntityPositionAction());
        RegisterSimpleAction("entity:get-rotation", GetEntityRotationAction.class, new GetEntityRotationAction());
        RegisterSimpleAction("entity:get-velocity", GetEntityVelocityAction.class, new GetEntityVelocityAction());
        RegisterSimpleAction("entity:get-head-position", GetEntityHeadPositionAction.class, new GetEntityHeadPositionAction());
        RegisterSimpleAction("entity:get-look", GetEntityHeadRotationAction.class, new GetEntityHeadRotationAction());

    }

}
