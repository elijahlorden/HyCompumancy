package me.freznel.compumancy.vm.interfaces;

import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.execution.Invocation;

/*
    Marks an object as Executable.  Execute() is only called by flow control actions, and not directly by Invocations.
    Flow control actions will look for IExecutable first, and fall back to IEvaluatable if available.
    Flow control actions will always remove their arguments from the stack, so argument cloning is not required.
 */
public interface IExecutable {

    public int ExecutionBudgetCost();
    public void Execute(Invocation invocation) throws VMException;
    public boolean IsExecuteSynchronous();

}