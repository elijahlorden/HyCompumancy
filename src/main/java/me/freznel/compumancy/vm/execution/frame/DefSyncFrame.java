package me.freznel.compumancy.vm.execution.frame;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import me.freznel.compumancy.Compumancy;
import me.freznel.compumancy.vm.compiler.Word;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.execution.FrameSyncType;
import me.freznel.compumancy.vm.execution.Invocation;

public class DefSyncFrame extends Frame {

    public static BuilderCodec<DefSyncFrame> CODEC = BuilderCodec.builder(DefSyncFrame.class, DefSyncFrame::new)
            .append(new KeyedCodec<>("Action", new EnumCodec<DefAction>(DefAction.class)), (o, v) -> o.action = v, o -> o.action)
            .add()
            .append(new KeyedCodec<>("Name", Codec.STRING), (o, v) -> o.defName = v, o -> o.defName)
            .add()
            .append(new KeyedCodec<>("Word", Word.CODEC), (o, v) -> o.word = v, o -> o.word)
            .add()
            .append(new KeyedCodec<>("Done", Codec.BOOLEAN), (o, v) -> o.done = v, o -> o.done)
            .add()
            .build();

    public enum DefAction {
        Invalid,
        Load,
        Store,
        Execute;
    }

    private DefAction action;
    private String defName;
    private Word word;
    private boolean done;

    public DefSyncFrame() { }

    public DefSyncFrame(DefAction action, String defName) {
        this.action = action;
        this.defName = defName;
        this.word = null;
        this.done = false;
    }

    public DefSyncFrame(DefAction action, String defName, Word word) {
        this.action = action;
        this.defName = defName;
        this.word = word;
        this.done = false;
    }

    public DefSyncFrame(DefSyncFrame other) {
        this.action = other.action;
        this.defName = other.defName;
        this.word = other.word;
        this.done = other.done;
    }

    @Override
    public int getSize() {
        return word == null ? 1 : word.getSize();
    }

    @Override
    public boolean isFinished() {
        return done;
    }

    @Override
    public void execute(Invocation invocation, long interruptAt) {
        if (!invocation.isDefinitionStoreAttached()) {
            invocation.attachDefinitionStore(invocation.getCaster().getDefinitionStoreComponent());
        }
        switch (action) {
            case Store -> {
                if (!invocation.isDefinitionStoreAttached()) throw new VMException(String.format("Failed to store definition '%s', no definition store found", defName));
                invocation.storeDefinition(defName, word);
            }
            case Load -> {
                if (!invocation.isDefinitionStoreAttached()) throw new VMException(String.format("Failed to load definition '%s', no definition store found", defName));
                invocation.loadDefinition(defName);
            }
            case Execute -> {
                if (!invocation.isDefinitionStoreAttached()) throw new VMException(String.format("Failed to execute definition '%s', no definition store found", defName));
                invocation.executeDefinition(defName);
            }
            default -> throw new VMException("Invalid DefSyncFrame action encountered");
        }
        done = true;
        if (invocation.getCurrentFrame() == this) invocation.getFrameStack().removeLast();
    }

    @Override
    public FrameSyncType getFrameSyncType() {
        return FrameSyncType.Sync;
    }

    @Override
    public Frame clone() {
        return new DefSyncFrame(this);
    }
}
