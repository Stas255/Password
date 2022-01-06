package www.tolstonozhenko.password;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import java.io.UnsupportedEncodingException;

public class Login extends AccountAuthenticatorActivity {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_USER = "User";
    public static final String APP_PREFERENCES_TOKEN = "Token";
    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button button = (Button) findViewById(R.id.bLogin);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
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
                        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, //POST - API-запрос для получение данных
                                "http://localhost:8000/api/auth/signin", jsonBody, new Response.Listener<JSONObject>() {
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
                        }, new Response.ErrorListener() { // в случае возникновеня ошибки
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                JSONObject body;
                                if(error.networkResponse.data!=null) {
                                    try {
                                        body = new JSONObject(new String(error.networkResponse.data,"UTF-8"));
                                        Toast.makeText(getApplicationContext(), body.getString("message"), Toast.LENGTH_SHORT).show();
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        queue.add(request);
                    } catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                        // Do something to recover ... or kill the app.
                    }
                } else {
                    String toastMessage = "Username or Password are not populated";
                    Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}