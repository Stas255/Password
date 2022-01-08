package www.tolstonozhenko.password;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.AccountAuthenticatorActivity;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Register extends AccountAuthenticatorActivity {
    float x1,x2,y1,y2;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_USER = "User";
    public static final String APP_PREFERENCES_TOKEN = "Token";
    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button button = (Button) findViewById(R.id.bRegister);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.etUsername);
                EditText email = (EditText) findViewById(R.id.etEmail);
                EditText password = (EditText) findViewById(R.id.etPassword);
                if (email.getText().length() > 0 && password.getText().length() > 0 && username.getText().length() > 0) {
                    try {
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("username", username.getText());
                        jsonBody.put("email", email.getText());
                        jsonBody.put("password", password.getText());
                        jsonBody.put("roles", "['user']");
                        final StringRequest request = new StringRequest(Request.Method.POST, //POST - API-запрос для получение данных
                                "http://localhost:8000/api/auth/signup", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String result = response;
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, Login.class));
                            }
                        }, new Response.ErrorListener() { // в случае возникновеня ошибки
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                JSONObject body;
                                if (error.networkResponse != null && error.networkResponse.data != null) {
                                    try {
                                        body = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                                        Toast.makeText(getApplicationContext(), body.getString("message"), Toast.LENGTH_SHORT).show();
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }) {
                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                return jsonBody.toString().getBytes();
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json";
                            }
                        };
                        queue.add(request);
                    } catch (JSONException e) {
                        Log.e("MYAPP", "unexpected JSON exception", e);
                        // Do something to recover ... or kill the app.
                    }
                } else {
                    String toastMessage = "Username, Email or Password are not populated";
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
                    startActivity(new Intent(Register.this, MainActivity.class));
                }else if(x1 > x2 && ((x1-x2)/width) > 0.6){
                    startActivity(new Intent(Register.this, Login.class));
                }
            }
        }
        return false;
    }
}