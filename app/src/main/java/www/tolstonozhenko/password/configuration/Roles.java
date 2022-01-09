package www.tolstonozhenko.password.configuration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import www.tolstonozhenko.password.LoginActivity;
import www.tolstonozhenko.password.MainAdminActivity;
import www.tolstonozhenko.password.Passwords;

public class Roles {
    private static SharedPreferences mSettings;

    public static boolean IsAdmin(Context context){
        return checkRole(context, "ROLE_ADMIN");
    }

    public static boolean IsUser(Context context){
        return checkRole(context, "ROLE_USER");
    }

    private static  boolean checkRole(Context context, String role){
        mSettings = context.getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        if(mSettings.contains(LoginActivity.APP_PREFERENCES_TOKEN) && mSettings.contains(LoginActivity.APP_PREFERENCES_USER)){
            if(mSettings.contains(LoginActivity.APP_PREFERENCES_USER)){
                JSONObject userJs = null;
                try {
                    userJs = new JSONObject(mSettings.getString(LoginActivity.APP_PREFERENCES_USER, ""));
                    JSONArray jsonArray = userJs.getJSONArray("roles");
                    for (int i=0;i<jsonArray.length();i++){
                        if(jsonArray.get(i).toString().equals(role)){
                            return true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static void CheckLogin(Context contextFrom){
        if(IsAdmin(contextFrom)){
            contextFrom.startActivity(new Intent(contextFrom, MainAdminActivity.class));
        }else if(IsUser(contextFrom)){
            contextFrom.startActivity(new Intent(contextFrom, Passwords.class));
        }
    }
}
