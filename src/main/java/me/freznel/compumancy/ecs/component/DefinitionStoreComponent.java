package me.freznel.compumancy.ecs.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.vm.compiler.Word;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class DefinitionStoreComponent implements Component<EntityStore>, IDefinitionStore {
    public static final BuilderCodec<DefinitionStoreComponent> CODEC = BuilderCodec.builder(DefinitionStoreComponent.class, DefinitionStoreComponent::new)
            .append(new KeyedCodec<>("Map", new MapCodec<>(Word.CODEC, HashMap::new)),
                    (o, v) -> {
                        if (!v.isEmpty()) o.userDefs.putAll(v);
                    },
                    o -> o.userDefs)
            .add()
            .append(new KeyedCodec<>("Max", Codec.INTEGER), (o, v) -> o.maxUserDefs = v, o -> o.maxUserDefs)
            .add()
            .append(new KeyedCodec<>("Fixed", Codec.STRING), (o, v) -> o.fixedVocabularyName = v, o -> o.fixedVocabularyName)
            .add()
            .build();


    private final ConcurrentHashMap<String, Word> userDefs;
    private int maxUserDefs;
    private String fixedVocabularyName;

    public DefinitionStoreComponent() { userDefs = new ConcurrentHashMap<>(); maxUserDefs = 0; }
    public DefinitionStoreComponent(int maxUserDefs) { userDefs = new ConcurrentHashMap<>(); this.maxUserDefs = maxUserDefs; }


    public DefinitionStoreComponent(DefinitionStoreComponent other) {
        this.maxUserDefs = other.maxUserDefs;
        this.fixedVocabularyName = other.fixedVocabularyName;
        this.userDefs = new ConcurrentHashMap<>();
        for (var kv : other.userDefs.entrySet())
        {
            this.userDefs.put(kv.getKey(), kv.getValue().clone());
        }
    }

    public ConcurrentHashMap<String, Word> GetUserDefsMap() { return userDefs; }

    public int getMaxUserDefs() { return maxUserDefs; }
    public void setMaxUserDefs(int maxUserDefs) { this.maxUserDefs = maxUserDefs; }

    public String getFixedVocabularyName() { return fixedVocabularyName; }
    public void setFixedVocabularyName(String fixedVocabularyName) { this.fixedVocabularyName = fixedVocabularyName; }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public @Nullable Component<EntityStore> clone() {
        return new DefinitionStoreComponent(this);
    }


}
