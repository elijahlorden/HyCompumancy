package me.freznel.compumancy.vm.execution;

//Denotes which thread an invocation frame should execute on
public enum FrameSyncType {
    Neutral,
    Sync,
    Async;
}
