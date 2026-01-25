package me.freznel.compumancy.vm;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.actions.stack.DuplicateAction;
import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.operators.BinaryOperatorSet;
import me.freznel.compumancy.vm.operators.UnaryOperatorSet;
import me.freznel.compumancy.vm.operators.binary.NumberNumberBinaryOperatorSet;
import me.freznel.compumancy.vm.operators.unary.NumberUnaryOperatorSet;

public class RegisterVMObjects {

    public static void Register() {
        RegisterOperandSets();
        RegisterActions();
    }

    private static void RegisterOperandSets() {
        //Number operand sets
        UnaryOperatorSet.Register(new NumberUnaryOperatorSet(), NumberObject.class);
        BinaryOperatorSet.Register(new NumberNumberBinaryOperatorSet(), NumberObject.class, NumberObject.class);

        //Boolean operand sets

        //List operand sets

    }

    private static void RegisterActions() {
        //Stack actions
        VMAction.Register("dup", new DuplicateAction());








    }

}
