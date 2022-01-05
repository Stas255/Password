package www.tolstonozhenko.password;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
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

    public static final String ARG_ACCOUNT_TYPE = "accountType";
    public static final String ARG_AUTH_TOKEN_TYPE = "authTokenType";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "isAddingNewAccount";
    public static final String PARAM_USER_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button button = (Button) findViewById(R.id.bLogin);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
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
                                JSONObject response1 = response;

                            }
                        }, new Response.ErrorListener() { // в случае возникновеня ошибки
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                try {
                                    String responseBody = new String( volleyError.networkResponse.data, "utf-8" );
                                    JSONObject jsonObject = new JSONObject( responseBody );
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch ( JSONException e ) {
                                    //Handle a malformed json response
                                } catch (UnsupportedEncodingException error){

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