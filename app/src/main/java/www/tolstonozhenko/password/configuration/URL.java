package www.tolstonozhenko.password.configuration;

public class URL {
    static public String HTPP_URL = "http://192.168.0.71:8000";
    static public String HTPP_URL_LOGIN = HTPP_URL + "/api/auth/signin";
    static public String HTPP_URL_GET_UNIC_PASSWORD = HTPP_URL + "/simple/getUnicPassword/";
    static public String HTPP_URL_USER_SAVE_PASSWORD = HTPP_URL + "/api/user/savepassword/";
    static public String HTPP_URL_USER_GET_ALL_PASSWORDS = HTPP_URL + "/api/user/getpasswords";
    static public String HTPP_URL_USER_GET_PASSWORD = HTPP_URL + "/api/user/getpassword";
    static public String HTPP_URL_USER_RESET_PASSWORD = HTPP_URL + "/api/user/resetpassword";
    static public String HTPP_URL_UPDATE_USER = HTPP_URL + "/api/user/updateUser";
}
