package me.freznel.compumancy.vm;

import me.freznel.compumancy.vm.actions.ActionHelpers;
import me.freznel.compumancy.vm.actions.entity.*;
import me.freznel.compumancy.vm.actions.flow.ForAction;
import me.freznel.compumancy.vm.actions.stack.*;
import me.freznel.compumancy.vm.compiler.Compiler;
import me.freznel.compumancy.vm.compiler.Vocabulary;
import me.freznel.compumancy.vm.compiler.Word;
import me.freznel.compumancy.vm.execution.frame.*;
import me.freznel.compumancy.vm.objects.*;
import me.freznel.compumancy.vm.operators.*;
import me.freznel.compumancy.vm.operators.binary.*;
import me.freznel.compumancy.vm.operators.unary.*;

public class RegisterVMObjects {

    public static void Register() {

        //Unary Operators
        Vocabulary.BASE.Add("len", new Word(new UnaryOperatorObject(UnaryOperator.Length)));
        Vocabulary.BASE.AddAlias("len", "abs");
        Vocabulary.BASE.Add("negate", new Word(new UnaryOperatorObject(UnaryOperator.SignedNegate)));
        Vocabulary.BASE.Add("not", new Word(new UnaryOperatorObject(UnaryOperator.UnsignedNegate)));

        //Binary Operators
        Vocabulary.BASE.Add("+", new Word(new BinaryOperatorObject(BinaryOperator.Add)));
        Vocabulary.BASE.Add("-", new Word(new BinaryOperatorObject(BinaryOperator.Subtract)));
        Vocabulary.BASE.Add("*", new Word(new BinaryOperatorObject(BinaryOperator.Multiply)));
        Vocabulary.BASE.Add("/", new Word(new BinaryOperatorObject(BinaryOperator.Divide)));
        Vocabulary.BASE.Add("mod", new Word(new BinaryOperatorObject(BinaryOperator.Mod)));

        Vocabulary.BASE.Add("and", new Word(new BinaryOperatorObject(BinaryOperator.And)));
        Vocabulary.BASE.Add("nand", new Word(new BinaryOperatorObject(BinaryOperator.Nand)));
        Vocabulary.BASE.Add("or", new Word(new BinaryOperatorObject(BinaryOperator.Or)));
        Vocabulary.BASE.Add("xor", new Word(new BinaryOperatorObject(BinaryOperator.Xor)));

        Vocabulary.BASE.Add("?eq", new Word(new BinaryOperatorObject(BinaryOperator.Equal)));
        Vocabulary.BASE.Add("?neq", new Word(new BinaryOperatorObject(BinaryOperator.NotEqual)));
        Vocabulary.BASE.Add("?gt", new Word(new BinaryOperatorObject(BinaryOperator.GreaterThan)));
        Vocabulary.BASE.Add("?lt", new Word(new BinaryOperatorObject(BinaryOperator.LessThan)));
        Vocabulary.BASE.Add("?geq", new Word(new BinaryOperatorObject(BinaryOperator.GreaterThanOrEqualTo)));
        Vocabulary.BASE.Add("?leq", new Word(new BinaryOperatorObject(BinaryOperator.LessThanOrEqualTo)));

        RegisterCodecs();
        RegisterOperatorSets();
        RegisterActions();
        RegisterCompilerWords();
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
        VMObject.CODEC.register("Def", DefinitionRefObject.class, DefinitionRefObject.CODEC);
        VMObject.CODEC.register("Meta", MetaObject.class, MetaObject.CODEC);

        //Frames
        Frame.CODEC.register("Exe", ExecutionFrame.class, ExecutionFrame.CODEC);
        Frame.CODEC.register("Numeric", NumericIteratorFrame.class, NumericIteratorFrame.CODEC);
        Frame.CODEC.register("Compile", CompileFrame.class, CompileFrame.CODEC);
        Frame.CODEC.register("DefBuilder", DefBuilderFrame.class, DefBuilderFrame.CODEC);

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
        ActionHelpers.RegisterSimpleAction("dup", DuplicateAction.class, new DuplicateAction());
        ActionHelpers.RegisterSimpleAction("drop", DropAction.class, new DropAction());
        ActionHelpers.RegisterSimpleAction("swap", SwapAction.class, new SwapAction());
        ActionHelpers.RegisterSimpleAction("over", OverAction.class, new OverAction());

        //Logic actions
        Vocabulary.BASE.Add("true", new Word(BoolObject.TRUE));
        Vocabulary.BASE.Add("false", new Word(BoolObject.FALSE));

        ActionHelpers.RegisterSimpleAction("?", SelectAction.class, new SelectAction());

        //Flow control actions
        ActionHelpers.RegisterSimpleAction("for", ForAction.class, new ForAction());
        Vocabulary.BASE.Add(":", new Word(MetaObject.START_DEF));
        Vocabulary.BASE.Add(";", new Word(MetaObject.END_DEF));
        Vocabulary.BASE.Add("(", new Word(MetaObject.START_LIST));
        Vocabulary.BASE.Add(")", new Word(MetaObject.END_LIST));
        Vocabulary.BASE.Add("eval", new Word(MetaObject.EVAL));
        Vocabulary.BASE.Add("eval/cc", new Word(MetaObject.EVAL_CC));

        //Entity actions
        ActionHelpers.RegisterSimpleAction("caster", GetCasterAction.class, new GetCasterAction());
        ActionHelpers.RegisterSimpleAction("send-message", SendMessageAction.class, new SendMessageAction());
        ActionHelpers.RegisterSimpleAction("entity:get-position", GetEntityPositionAction.class, new GetEntityPositionAction());
        ActionHelpers.RegisterSimpleAction("entity:get-rotation", GetEntityRotationAction.class, new GetEntityRotationAction());
        ActionHelpers.RegisterSimpleAction("entity:get-velocity", GetEntityVelocityAction.class, new GetEntityVelocityAction());
        ActionHelpers.RegisterSimpleAction("entity:get-head-position", GetEntityHeadPositionAction.class, new GetEntityHeadPositionAction());
        ActionHelpers.RegisterSimpleAction("entity:get-look", GetEntityHeadRotationAction.class, new GetEntityHeadRotationAction());

    }

    public static void RegisterCompilerWords() {
        //Definition shorthand word
        Compiler.Register(":", (tokenizer, stack) -> {




        });




    }

}
