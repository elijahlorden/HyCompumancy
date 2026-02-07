package me.freznel.compumancy.vm.compiler;

import com.hypixel.hytale.logger.HytaleLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class Vocabulary {
    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();
    public static final Vocabulary BASE = new Vocabulary();
    public static final ConcurrentHashMap<String, Vocabulary> vocabularies = new ConcurrentHashMap<>();

    public static void registerVocabulary(String key, Vocabulary vocabulary) {
        if (vocabularies.containsKey(key)) {
            Logger.at(Level.SEVERE).log(String.format("Attempted to register duplicate vocabulary '%s'", key));
            return;
        }
        vocabularies.put(key, vocabulary);
    }

    public static Vocabulary getVocabulary(String key) {
        return vocabularies.get(key);
    }

    private final Map<String, Word> map = new ConcurrentHashMap<>();

    public void add(String key, Word word) {
        if (map.containsKey(key)) {
            Logger.at(Level.SEVERE).log(String.format("Attempted to register duplicate word '%s'", key));
            return;
        }
        map.put(key, word);
    }

    public void addAlias(String key, String alias) {
        if (!map.containsKey(key)) throw new IllegalArgumentException(String.format("Attempted to alias nonexistent word '%s'", key));
        if (map.containsKey(alias)) {
            Logger.at(Level.SEVERE).log(String.format("Attempted to register duplicate word '%s'", alias));
            return;
        }
        map.put(alias, map.get(key));
    }

    public Word get(String key) {
        return map.get(key);
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

}
