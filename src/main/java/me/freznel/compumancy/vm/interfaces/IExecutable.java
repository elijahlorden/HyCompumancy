package me.freznel.compumancy.vm.interfaces;

import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.exceptions.VMException;

public interface IExecutable {

    public int ExecutionBudgetCost();
    public void Execute(Invocation invocation) throws VMException;

}