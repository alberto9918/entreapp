package com.mario.myapplication.model;

public class Description {

    private Language language;
    private String originalDescription;
    private TranslationDescription[] translations;

    public Description() {
    }

    public Description(Language language, String originalDescription, TranslationDescription[] translations) {
        this.language = language;
        this.originalDescription = originalDescription;
        this.translations = translations;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getOriginalDescription() {
        return originalDescription;
    }

    public void setOriginalDescription(String originalDescription) {
        this.originalDescription = originalDescription;
    }

    public TranslationDescription[] getTranslations() {
        return translations;
    }

    public void setTranslations(TranslationDescription[] translations) {
        this.translations = translations;
    }
}
