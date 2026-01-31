package me.freznel.compumancy.casting;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.vm.compiler.Word;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class DefinitionStoreComponent implements Component<EntityStore> {

    private ConcurrentHashMap<String, Word> userDefs;
    private String fixedVocabularyName;



    @Override
    public @Nullable Component<EntityStore> clone() {
        return null;
    }


}
