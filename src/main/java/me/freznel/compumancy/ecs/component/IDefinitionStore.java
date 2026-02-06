package me.freznel.compumancy.ecs.component;

import me.freznel.compumancy.vm.compiler.Word;

import java.util.concurrent.ConcurrentHashMap;

public interface IDefinitionStore {

    public ConcurrentHashMap<String, Word> GetUserDefsMap();

    public int GetMaxUserDefs();
    public void SetMaxUserDefs(int maxUserDefs);

    public String GetFixedVocabularyName();
    public void SetFixedVocabularyName(String fixedVocabularyName);

}
