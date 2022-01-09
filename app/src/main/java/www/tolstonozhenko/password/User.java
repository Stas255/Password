package www.tolstonozhenko.password;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import www.tolstonozhenko.password.adapters.BugsSystemAdapter;
import www.tolstonozhenko.password.configuration.Roles;
import www.tolstonozhenko.password.configuration.URL;
import www.tolstonozhenko.password.request.VolleyStringResponseListener;
import www.tolstonozhenko.password.request.VolleyUtils;

public class User extends AppCompatActivity {
    private static SharedPreferences mSettings;
    JSONObject userJs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mSettings = getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(LoginActivity.APP_PREFERENCES_USER)) {
            userJs = null;
            try {
                userJs = new JSONObject(mSettings.getString(LoginActivity.APP_PREFERENCES_USER, ""));
                ((TextView) findViewById(R.id.textViewUserName)).setText(userJs.getString("username"));
                ((TextView) findViewById(R.id.textViewEmail)).setText(userJs.getString("email"));
                Button b = (Button) findViewById(R.id.buttonSend);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String password = ((EditText) findViewById(R.id.et_password)).getText().toString();
                        String newPass = ((EditText) findViewById(R.id.et_new_password)).getText().toString();
                        String verPass = ((EditText) findViewById(R.id.et_ver_new_password)).getText().toString();
                        String err = "";
                        if (password.isEmpty()) {
                            err += "Enter password. ";
                        }
                        if (newPass.isEmpty()) {
                            err += "Enter new password. ";
                        }
                        if (!newPass.equals(verPass)) {
                            err += "New pass not ver pass. ";
                        }
                        if (!err.isEmpty()) {
                            Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                        } else {
                            JSONObject jsonBody = new JSONObject();
                            try {
                                RequestQueue queue = Volley.newRequestQueue(User.this);
                                jsonBody.put("email", userJs.getString("email"));
                                jsonBody.put("newPassword", password);
                                jsonBody.put("oldPassword", newPass);

                                VolleyUtils.makeStringObjectRequest(Request.Method.POST, User.this, URL.HTPP_URL_UPDATE_USER, jsonBody, new VolleyStringResponseListener() {
                                    @Override
                                    public void onError(String message) {

                                    }

                                    @Override
                                    public void onResponse(String response) {
                                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        (findViewById(R.id.qr_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(User.this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(User.this, new String[]{Manifest.permission.CAMERA},
                            50);
                }
                startActivity(new Intent(User.this, QRActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Roles.IsAdmin(this)) {
            getMenuInflater().inflate(R.menu.main_admin_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem user = menu.findItem(R.id.action_user);
        if (mSettings.contains(LoginActivity.APP_PREFERENCES_USER)) {
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
        if (Roles.IsAdmin(this)) {
            switch (id) {
                case R.id.user_block:
                    AdminBugReportSystemActivity.report = BugsSystemAdapter.Report.USER_BLOCK;
                    startActivity(new Intent(this, AdminBugReportSystemActivity.class));
                    return true;
                case R.id.user_bugs:
                    AdminBugReportSystemActivity.report = BugsSystemAdapter.Report.USER_BUG;
                    startActivity(new Intent(this, AdminBugReportSystemActivity.class));
                    return true;
                case R.id.system_bugs:
                    AdminBugReportSystemActivity.report = BugsSystemAdapter.Report.SYSTEM_BUG;
                    startActivity(new Intent(this, AdminBugReportSystemActivity.class));
                    return true;
                case R.id.action_user:
                    return true;
                case R.id.action_logout:
                    SharedPreferences mSettings = getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                    mSettings.edit().clear().commit();
                    startActivity(new Intent(this, MainActivity.class));
                    return true;
            }
        } else {
            switch (id) {
                case R.id.action_password:
                    startActivity(new Intent(this, Passwords.class));
                    return true;
                case R.id.action_user:
                    return true;
                case R.id.action_logout:
                    SharedPreferences mSettings = getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                    mSettings.edit().clear().commit();
                    startActivity(new Intent(this, MainActivity.class));
                    return true;
            }
        }
        //headerView.setText(item.getTitle());
        return super.onOptionsItemSelected(item);
    }


}