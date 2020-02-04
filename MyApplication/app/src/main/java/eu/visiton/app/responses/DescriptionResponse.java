package eu.visiton.app.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import eu.visiton.app.model.Language;

import java.util.List;

public class DescriptionResponse {

    @SerializedName("language")
    @Expose
    private Language language;
    @SerializedName("spanishDescription")
    @Expose
    private String spanishDescription;
    @SerializedName("translations")
    @Expose
    private List<TranslationDescriptionResponse> translations = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public DescriptionResponse() {
    }

    /**
     *
     * @param translations
     * @param language
     * @param spanishDescription
     */
    public DescriptionResponse(Language language, String spanishDescription, List<TranslationDescriptionResponse> translations) {
        super();
        this.language = language;
        this.spanishDescription = spanishDescription;
        this.translations = translations;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getSpanishDescription() {
        return spanishDescription;
    }

    public void setSpanishDescription(String spanishDescription) {
        this.spanishDescription = spanishDescription;
    }

    public List<TranslationDescriptionResponse> getTranslations() {
        return translations;
    }

    public void setTranslations(List<TranslationDescriptionResponse> translations) {
        this.translations = translations;
    }

}

