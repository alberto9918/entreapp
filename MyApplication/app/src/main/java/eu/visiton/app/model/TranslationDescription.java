package eu.visiton.app.model;


public class TranslationDescription {

    private String id;
    private String translatedDescription;

    public TranslationDescription() {

    }

    public TranslationDescription(String id, String translatedDescription) {
        this.id = id;
        this.translatedDescription = translatedDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTranslatedFile() {
        return translatedDescription;
    }

    public void setTranslatedFile(String translatedDescription) {
        this.translatedDescription = translatedDescription;
    }
}
