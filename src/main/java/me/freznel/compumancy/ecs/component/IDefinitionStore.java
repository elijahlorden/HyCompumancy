package me.freznel.compumancy.ecs.component;

import me.freznel.compumancy.vm.compiler.Word;

import java.util.concurrent.ConcurrentHashMap;

public interface IDefinitionStore {

    public ConcurrentHashMap<String, Word> GetUserDefsMap();

    public int getMaxUserDefs();
    public void setMaxUserDefs(int maxUserDefs);

    public String getFixedVocabularyName();
    public void setFixedVocabularyName(String fixedVocabularyName);

}
