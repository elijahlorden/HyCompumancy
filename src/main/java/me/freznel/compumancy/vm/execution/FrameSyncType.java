package me.freznel.compumancy.vm.execution;

//Denotes which thread an invocation frame should execute on.  Neutral frames
public enum FrameSyncType {
    Neutral,
    BackgroundThread,
    CasterThread;
}
