package www.tolstonozhenko.password.classes;

public class UserBlock {
    public String id;
    public String description;
    public String userId;
    public String userName;

    public UserBlock(String id, String description, String userId, String userName) {
        this.id = id;
        this.description = description;
        this.userId = userId;
        this.userName = userName;
    }
}
