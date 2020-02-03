package com.mario.myapplication.model;


public class TranslationAudioGuide {

    private String id;
    private String translatedFile;
    private String language;

    public TranslationAudioGuide() {

    }

    public TranslationAudioGuide(String id, String translatedFile) {
        this.id = id;
        this.translatedFile = translatedFile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTranslatedFile() {
        return translatedFile;
    }

    public void setTranslatedFile(String translatedFile) {
        this.translatedFile = translatedFile;
    }
}
