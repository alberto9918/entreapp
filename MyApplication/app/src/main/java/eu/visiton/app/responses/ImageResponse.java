package eu.visiton.app.responses;

public class ImageResponse {

    String key;

    public ImageResponse(String url) {
        this.key = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String url) {
        this.key = url;
    }
}
