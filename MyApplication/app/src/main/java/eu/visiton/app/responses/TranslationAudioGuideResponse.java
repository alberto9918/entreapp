
package eu.visiton.app.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TranslationAudioGuideResponse {

    @SerializedName("language")
    @Expose
    private LanguageIdResponse language;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("translatedFile")
    @Expose
    private String translatedFile;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TranslationAudioGuideResponse() {
    }

    /**
     * 
     * @param translatedFile
     * @param language
     * @param id
     */
    public TranslationAudioGuideResponse(LanguageIdResponse language, String id, String translatedFile) {
        super();
        this.language = language;
        this.id = id;
        this.translatedFile = translatedFile;
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

    public String getTranslatedFile() {
        return translatedFile;
    }

    public void setTranslatedFile(String translatedFile) {
        this.translatedFile = translatedFile;
    }

}
