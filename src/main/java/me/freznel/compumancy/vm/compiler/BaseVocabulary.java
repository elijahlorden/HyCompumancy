package me.freznel.compumancy.vm.compiler;

import com.hypixel.hytale.logger.HytaleLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class BaseVocabulary {
    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();
    private static final Map<String, Word> map = new ConcurrentHashMap<>();

    public static void Register(String key, Word word) {
        if (map.containsKey(key)) {
            Logger.at(Level.SEVERE).log(String.format("Attempted to register duplicate word '%s'", key));
            return;
        }
        map.put(key, word);
    }

    public static void RegisterAlias(String key, String alias) {
        if (!map.containsKey(key)) throw new IllegalArgumentException(String.format("Attempted to alias nonexistent word '%s'", key));
        if (map.containsKey(alias)) {
            Logger.at(Level.SEVERE).log(String.format("Attempted to register duplicate word '%s'", alias));
            return;
        }
        map.put(alias, map.get(key));
    }

    public static Word Get(String key) {
        if (!map.containsKey(key)) return null;
        return map.get(key);
    }

}
