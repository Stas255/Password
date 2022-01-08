package www.tolstonozhenko.password;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import www.tolstonozhenko.password.configuration.DB;
import www.tolstonozhenko.password.request.VolleyResponseListener;
import www.tolstonozhenko.password.request.VolleyUtils;

public class Login extends AccountAuthenticatorActivity {
    float x1,x2,y1,y2;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_USER = "User";
    public static final String APP_PREFERENCES_TOKEN = "Token";
    SharedPreferences mSettings;
    Login l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.l = this;
        setContentView(R.layout.activity_login);
        Button button = (Button) findViewById(R.id.bLogin);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if(mSettings.contains(Login.APP_PREFERENCES_TOKEN) && mSettings.contains(Login.APP_PREFERENCES_USER)){
            startActivity(new Intent(this, Passwords.class));
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.etEmail);
                EditText password = (EditText) findViewById(R.id.etPassword);
                if (email.getText().length() > 0 && password.getText().length() > 0) {
                    try {
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("email", email.getText());
                        jsonBody.put("password", password.getText());
                        VolleyUtils.makeJsonObjectRequest(l, DB.HTPP_URL_LOGIN,jsonBody, new VolleyResponseListener() {
                            @Override
                            public void onError(String message) {

                            }

                            @Override
                            public void onResponse(JSONObject response) {
                                SharedPreferences.Editor editor = mSettings.edit();
                                try {
                                    editor.putString(APP_PREFERENCES_TOKEN, response.getString("accessToken"));
                                    editor.putString(APP_PREFERENCES_USER, response.toString());
                                    editor.apply();
                                    startActivity(new Intent(Login.this, Passwords.class));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                    }
                } else {
                    String toastMessage = "Username or Password are not populated";
                    Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                x1 = event.getX();
                y1 = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP:{
                x2 = event.getX();
                y2 = event.getY();
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                if(x1 <x2 && ((x2-x1)/width) > 0.6){
                    startActivity(new Intent(Login.this, Register.class));
                }else if(x1 > x2 && ((x1-x2)/width) > 0.6){
                    startActivity(new Intent(Login.this, MainActivity.class));
                }
            }
        }
        return false;
    }

}