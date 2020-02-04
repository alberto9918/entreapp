
package eu.visiton.app.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TranslationDescriptionResponse {

    @SerializedName("language")
    @Expose
    private LanguageIdResponse language;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("translatedDescription")
    @Expose
    private String translatedDescription;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TranslationDescriptionResponse() {
    }

    /**
     * 
     * @param translatedDescription
     * @param language
     * @param id
     */
    public TranslationDescriptionResponse(LanguageIdResponse language, String id, String translatedDescription) {
        super();
        this.language = language;
        this.id = id;
        this.translatedDescription = translatedDescription;
    }

    public LanguageIdResponse getLanguage() {
        return language;
    }

    public void setLanguage(LanguageIdResponse language) {
        this.language = language;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTranslatedDescription() {
        return translatedDescription;
    }

    public void setTranslatedDescription(String translatedDescription) {
        this.translatedDescription = translatedDescription;
    }

}
