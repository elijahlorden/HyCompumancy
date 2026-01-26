package me.freznel.compumancy.vm.compiler;

import com.hypixel.hytale.logger.HytaleLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Vocabulary {
    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();
    private static final Map<String, Word> map = new HashMap<>();

    public static void Register(String key, Word word) {
        if (map.containsKey(key)) {
            Logger.at(Level.SEVERE).log(String.format("Attempted to register duplicate word '%s'", key));
            return;
        }
        map.put(key, word);
    }

    public static Word Get(String key) {
        if (!map.containsKey(key)) return null;
        return map.get(key);
    }

}
