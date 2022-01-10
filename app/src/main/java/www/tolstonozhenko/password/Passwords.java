package www.tolstonozhenko.password;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import www.tolstonozhenko.password.adapters.PasswordAdapter;
import www.tolstonozhenko.password.adapters.PasswordsAdapter;
import www.tolstonozhenko.password.classes.Password;
import www.tolstonozhenko.password.configuration.URL;
import www.tolstonozhenko.password.request.VolleyJsonArrayResponseListener;
import www.tolstonozhenko.password.request.VolleyUtils;

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

    //private static final String API_URL_USER = "http://localhost:8000/api/user/getpasswords";
    private static SharedPreferences mSettings;

    public void setAllPaswords() {
        //RequestQueue queue = Volley.newRequestQueue(this);

        mSettings = getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);

        VolleyUtils.makeJsonArrayObjectRequest(Request.Method.POST,Passwords.this, URL.HTPP_URL_USER_GET_ALL_PASSWORDS,null, new VolleyJsonArrayResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(JSONArray response) {
                SetPasswordsInLayout(response);
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem user = menu.findItem(R.id.action_user);
        if(mSettings.contains(LoginActivity.APP_PREFERENCES_USER)){
            JSONObject userJs = null;
            try {
                userJs = new JSONObject(mSettings.getString(LoginActivity.APP_PREFERENCES_USER, ""));
                user.setTitle(userJs.getString("username"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_password :
                return true;
            case R.id.action_user:
                startActivity(new Intent(this, User.class));
                return true;
            case R.id.action_logout:
                SharedPreferences mSettings = getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                mSettings.edit().clear().commit();
                startActivity(new Intent(this, MainActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}