
package eu.visiton.app.responses;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AudioguidesResponse {

    @SerializedName("language")
    @Expose
    private LanguageEmptyResponse language;
    @SerializedName("translations")
    @Expose
    private List<TranslationAudioGuideResponse> translations = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public AudioguidesResponse() {
    }

    /**
     * 
     * @param translations
     * @param language
     */
    public AudioguidesResponse(LanguageEmptyResponse language, List<TranslationAudioGuideResponse> translations) {
        super();
        this.language = language;
        this.translations = translations;
    }

    public LanguageEmptyResponse getLanguage() {
        return language;
    }

    public void setLanguage(LanguageEmptyResponse language) {
        this.language = language;
    }

    public List<TranslationAudioGuideResponse> getTranslations() {
        return translations;
    }

    public void setTranslations(List<TranslationAudioGuideResponse> translations) {
        this.translations = translations;
    }

}
