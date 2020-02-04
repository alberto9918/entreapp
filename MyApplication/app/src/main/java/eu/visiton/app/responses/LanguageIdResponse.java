
package eu.visiton.app.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageIdResponse {

    @SerializedName("language")
    @Expose
    private String language;

    /**
     * No args constructor for use in serialization
     * 
     */
    public LanguageIdResponse() {
    }

    /**
     * 
     * @param language
     */
    public LanguageIdResponse(String language) {
        super();
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
