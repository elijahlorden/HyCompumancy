package me.freznel.compumancy.vm;

import me.freznel.compumancy.vm.actions.ActionHelpers;
import me.freznel.compumancy.vm.actions.entity.*;
import me.freznel.compumancy.vm.actions.flow.ForAction;
import me.freznel.compumancy.vm.actions.misc.SpawnParticlesAction;
import me.freznel.compumancy.vm.actions.query.RaycastBlockAction;
import me.freznel.compumancy.vm.actions.stack.*;
import me.freznel.compumancy.vm.compiler.Vocabulary;
import me.freznel.compumancy.vm.compiler.Word;
import me.freznel.compumancy.vm.execution.frame.*;
import me.freznel.compumancy.vm.objects.*;
import me.freznel.compumancy.vm.operators.*;
import me.freznel.compumancy.vm.operators.binary.*;
import me.freznel.compumancy.vm.operators.binary.vector3.NumberVector3BinaryOperatorSet;
import me.freznel.compumancy.vm.operators.binary.vector3.Vector3NumberBinaryOperatorSet;
import me.freznel.compumancy.vm.operators.binary.vector3.Vector3Vector3BinaryOperatorSet;
import me.freznel.compumancy.vm.operators.unary.*;

public class RegisterVMObjects {

    public static void register() {

        //Unary Operators
        Vocabulary.BASE.add("len", new Word(UnaryOperator.Length.Instance));
        Vocabulary.BASE.addAlias("len", "abs");
        Vocabulary.BASE.add("negate", new Word(UnaryOperator.SignedNegate.Instance));
        Vocabulary.BASE.add("not", new Word(UnaryOperator.UnsignedNegate.Instance));

        //Binary Operators
        Vocabulary.BASE.add("+", new Word(BinaryOperator.Add.Instance));
        Vocabulary.BASE.add("-", new Word(BinaryOperator.Subtract.Instance));
        Vocabulary.BASE.add("*", new Word(BinaryOperator.Multiply.Instance));
        Vocabulary.BASE.add("/", new Word(BinaryOperator.Divide.Instance));
        Vocabulary.BASE.add("mod", new Word(BinaryOperator.Mod.Instance));

        Vocabulary.BASE.add("and", new Word(BinaryOperator.And.Instance));
        Vocabulary.BASE.add("nand", new Word(BinaryOperator.Nand.Instance));
        Vocabulary.BASE.add("or", new Word(BinaryOperator.Or.Instance));
        Vocabulary.BASE.add("xor", new Word(BinaryOperator.Xor.Instance));

        Vocabulary.BASE.add("?eq", new Word(BinaryOperator.Equal.Instance));
        Vocabulary.BASE.add("?neq", new Word(BinaryOperator.NotEqual.Instance));
        Vocabulary.BASE.add("?gt", new Word(BinaryOperator.GreaterThan.Instance));
        Vocabulary.BASE.add("?lt", new Word(BinaryOperator.LessThan.Instance));
        Vocabulary.BASE.add("?geq", new Word(BinaryOperator.GreaterThanOrEqualTo.Instance));
        Vocabulary.BASE.add("?leq", new Word(BinaryOperator.LessThanOrEqualTo.Instance));

        registerCodecs();
        registerOperatorSets();
        registerActions();
        registerCompilerWords();
    }

    private static void registerCodecs() {
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
        Frame.CODEC.register("NumIter", NumericIteratorFrame.class, NumericIteratorFrame.CODEC);
        Frame.CODEC.register("Compile", CompileFrame.class, CompileFrame.CODEC);
        Frame.CODEC.register("DefBuilder", DefBuilderFrame.class, DefBuilderFrame.CODEC);
        Frame.CODEC.register("DefSync", DefSyncFrame.class, DefSyncFrame.CODEC);

    }

    private static void registerOperatorSets() {
        //Number operator sets
        UnaryOperatorSet.Register(new NumberUnaryOperatorSet(), NumberObject.class);
        BinaryOperatorSet.Register(new NumberNumberBinaryOperatorSet(), NumberObject.class, NumberObject.class);

        //Boolean operator sets
        UnaryOperatorSet.Register(new BooleanUnaryOperatorSet(), BoolObject.class);
        BinaryOperatorSet.Register(new BooleanBooleanBinaryOperatorSet(), BoolObject.class, BoolObject.class);

        //Boolean/Number and Number/Boolean operator sets
        BinaryOperatorSet.Register(new BooleanNumberBinaryOperatorSet(), BoolObject.class, NumberObject.class);
        BinaryOperatorSet.Register(new NumberBooleanBinaryOperatorSet(), NumberObject.class, BoolObject.class);

        //Vector3 operator sets
        UnaryOperatorSet.Register(new Vector3UnaryOperatorSet(), Vector3Object.class);
        BinaryOperatorSet.Register(new Vector3Vector3BinaryOperatorSet(), Vector3Object.class, Vector3Object.class);
        BinaryOperatorSet.Register(new Vector3NumberBinaryOperatorSet(), Vector3Object.class, NumberObject.class);
        BinaryOperatorSet.Register(new NumberVector3BinaryOperatorSet(), NumberObject.class, Vector3Object.class);

        //String operator sets

        //List operator sets

    }

    private static void registerActions() {
        //Stack actions
        ActionHelpers.registerSimpleAction("dup", DuplicateAction.class, new DuplicateAction());
        ActionHelpers.registerSimpleAction("drop", DropAction.class, new DropAction());
        ActionHelpers.registerSimpleAction("swap", SwapAction.class, new SwapAction());
        ActionHelpers.registerSimpleAction("over", OverAction.class, new OverAction());

        //Logic actions
        Vocabulary.BASE.add("true", new Word(BoolObject.TRUE));
        Vocabulary.BASE.add("false", new Word(BoolObject.FALSE));

        ActionHelpers.registerSimpleAction("?", SelectAction.class, new SelectAction());

        //Flow control actions
        ActionHelpers.registerSimpleAction("for", ForAction.class, new ForAction());
        Vocabulary.BASE.add(":", new Word(MetaObject.MetaOperation.StartDef.Instance));
        Vocabulary.BASE.add(";", new Word(MetaObject.MetaOperation.EndDef.Instance));
        Vocabulary.BASE.add("(", new Word(MetaObject.MetaOperation.StartList.Instance));
        Vocabulary.BASE.add(")", new Word(MetaObject.MetaOperation.EndList.Instance));
        Vocabulary.BASE.add("eval", new Word(MetaObject.MetaOperation.Eval.Instance));
        Vocabulary.BASE.add("eval/cc", new Word(MetaObject.MetaOperation.EvalCC.Instance));

        //Entity actions
        ActionHelpers.registerSimpleAction("caster", GetCasterAction.class, new GetCasterAction());
        ActionHelpers.registerSimpleAction("send-message", SendMessageAction.class, new SendMessageAction());
        ActionHelpers.registerSimpleAction("entity:get-position", GetEntityPositionAction.class, new GetEntityPositionAction());
        ActionHelpers.registerSimpleAction("entity:get-rotation", GetEntityRotationAction.class, new GetEntityRotationAction());
        ActionHelpers.registerSimpleAction("entity:get-velocity", GetEntityVelocityAction.class, new GetEntityVelocityAction());
        ActionHelpers.registerSimpleAction("entity:get-head-position", GetEntityHeadPositionAction.class, new GetEntityHeadPositionAction());
        ActionHelpers.registerSimpleAction("entity:get-look", GetEntityHeadRotationAction.class, new GetEntityHeadRotationAction());

        //Query actions
        ActionHelpers.registerSimpleAction("raycast-block", RaycastBlockAction.class, new RaycastBlockAction());

        //Misc. actions
        ActionHelpers.registerSimpleAction("spawn-particle", SpawnParticlesAction.class, new SpawnParticlesAction());
    }

    public static void registerCompilerWords() {




    }

}
