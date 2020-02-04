package eu.visiton.app.responses;

public class UserLikesResponse {

    private String id;
    private String name;

    public UserLikesResponse() {

    }

    public UserLikesResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
