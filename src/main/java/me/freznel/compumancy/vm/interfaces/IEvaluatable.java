package me.freznel.compumancy.vm.interfaces;

import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.exceptions.VMException;

/*
    Marks an object as evaluatable.  Invocations will call Evaluate directly when encountering an object extending this interface.
 */
public interface IEvaluatable {

    public int ExecutionBudgetCost();
    public void Evaluate(Invocation invocation) throws VMException;

}