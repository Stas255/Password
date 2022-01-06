package www.tolstonozhenko.password.classes;

public class Password {
    public String id;
    public String namePassword;
    public String newPassword;
    public String oldPassword;
    public String confirmNewPassword;

    public Password(String id, String namePassword, String newPassword, String oldPassword, String confirmNewPassword) {
        this.id = id;
        this.namePassword = namePassword;
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
        this.confirmNewPassword = confirmNewPassword;
    }
}
