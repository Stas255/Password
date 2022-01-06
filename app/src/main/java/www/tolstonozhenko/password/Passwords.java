package www.tolstonozhenko.password;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import www.tolstonozhenko.password.classes.Password;

public class Passwords extends AppCompatActivity {
    RecyclerView pRecyclerView;
    RecyclerView.LayoutManager pLayoutManager;
    Button bAddNewPass;
    RecyclerView.Adapter pAdapter;
    RecyclerView.Adapter pNewAdapter;

    ArrayList<Password> passwords = new ArrayList<Password>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwords);
        setAllPaswords();
        bAddNewPass = (Button) findViewById(R.id.AddNewButton);
        pAdapter = new PasswordsAdapter(null, this);
        Passwords p = this;
        bAddNewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pRecyclerView = findViewById(R.id.onePass);
                pRecyclerView.setHasFixedSize(true);
                pLayoutManager = new LinearLayoutManager(v.getContext());
                pNewAdapter = new PasswordAdapter(null, PasswordAdapter.Action.CREATE, p);
                pRecyclerView.setLayoutManager(pLayoutManager);
                pRecyclerView.setAdapter(pNewAdapter);
            }
        });
    }

    private static final String API_URL_USER = "http://localhost:8000/api/user/";
    private static SharedPreferences mSettings;

    public void setAllPaswords() {
        RequestQueue queue = Volley.newRequestQueue(this);

        mSettings = getSharedPreferences(Login.APP_PREFERENCES, Context.MODE_PRIVATE);

        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.POST,
                API_URL_USER + "getpasswords",null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        SetPasswordsInLayout(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        JSONObject body;
                        if(error.networkResponse != null && error.networkResponse.data!=null) {
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
                })

        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                String token = null;
                if(mSettings.contains(Login.APP_PREFERENCES_TOKEN)){
                    token = mSettings.getString(Login.APP_PREFERENCES_TOKEN, "");
                }
                headers.put("x-access-token", token);

                return headers;
            }
        };
        queue.add(jsObjRequest);
    }

    private void SetPasswordsInLayout(JSONArray responses) {
        passwords = new ArrayList<Password>();
        for (int i=0; i < responses.length(); i++) {
            try {
                JSONObject response = responses.getJSONObject(i);
                passwords.add(new Password(response.getString("id"),response.getString("namePassword"),null,null,null));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        pRecyclerView = findViewById(R.id.list);
        pRecyclerView.setHasFixedSize(true);
        pLayoutManager = new LinearLayoutManager(this);
        pAdapter = new PasswordsAdapter(passwords, this);
        pRecyclerView.setLayoutManager(pLayoutManager);
        pRecyclerView.setAdapter(pAdapter);
    }

    public void GetPasswordById(String id, PasswordAdapter.Action action) {
        int len=passwords.size();
        Password password = null;
        for(int i=0; i<len; i++) {
            if (passwords.get(i).id.equals(id)) {
                password = passwords.get(i);
                break;
            }
        }
        if(password == null){
            Toast.makeText(getApplicationContext(), "Cannot find password", Toast.LENGTH_SHORT).show();
            return;
        }else {
            pRecyclerView = findViewById(R.id.onePass);
            pRecyclerView.setHasFixedSize(true);
            pLayoutManager = new LinearLayoutManager(this);
            pNewAdapter = new PasswordAdapter(password, action, this);
            pRecyclerView.setLayoutManager(pLayoutManager);
            pRecyclerView.setAdapter(pNewAdapter);
        }

    }
}