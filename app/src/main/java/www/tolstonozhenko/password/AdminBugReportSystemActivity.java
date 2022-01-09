package www.tolstonozhenko.password;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import www.tolstonozhenko.password.adapters.BugsSystemAdapter;
import www.tolstonozhenko.password.classes.UserBlock;
import www.tolstonozhenko.password.classes.UserBug;
import www.tolstonozhenko.password.configuration.URL;
import www.tolstonozhenko.password.request.VolleyJsonArrayResponseListener;
import www.tolstonozhenko.password.request.VolleyJsonResponseListener;
import www.tolstonozhenko.password.request.VolleyStringResponseListener;
import www.tolstonozhenko.password.request.VolleyUtils;

public class AdminBugReportSystemActivity extends AppCompatActivity {
    public static BugsSystemAdapter.Report report = BugsSystemAdapter.Report.SYSTEM_BUG;
    SharedPreferences mSettings;
    RecyclerView.Adapter bugSystemAdapter;
    RecyclerView.Adapter bugUserAdapter;
    RecyclerView bugRecyclerView;
    RecyclerView.LayoutManager bugLayoutManager;
    String detailNameFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bug_report_system);
        mSettings = getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        if (report == BugsSystemAdapter.Report.SYSTEM_BUG) {
            ViewAllSystemBugs();
        } else if (report == BugsSystemAdapter.Report.USER_BUG){
            ViewAllUserBugs();
        } else if (report == BugsSystemAdapter.Report.USER_BLOCK){
            ViewAllUserBlock();
        }
    }

    private void ViewAllUserBlock() {
        VolleyUtils.makeJsonArrayObjectRequest(Request.Method.POST, AdminBugReportSystemActivity.this, URL.HTPP_URL_ALL_USER_BLOCK, null, new VolleyJsonArrayResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<UserBlock>>() {
                }.getType();
                ArrayList<UserBlock> contactList = gson.fromJson(response.toString(), type);
                bugRecyclerView = findViewById(R.id.list_bugs);
                bugRecyclerView.setHasFixedSize(true);
                bugLayoutManager = new LinearLayoutManager(AdminBugReportSystemActivity.this);
                bugRecyclerView.setLayoutManager(bugLayoutManager);
                bugSystemAdapter = new BugsSystemAdapter(contactList, AdminBugReportSystemActivity.this);
                bugRecyclerView.setAdapter(bugSystemAdapter);
            }
        });
    }

    private void ViewAllUserBugs() {
        VolleyUtils.makeJsonArrayObjectRequest(Request.Method.POST, AdminBugReportSystemActivity.this, URL.HTPP_URL_ALL_USER_MESSAGES, null, new VolleyJsonArrayResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<UserBug>>() {
                }.getType();
                ArrayList<UserBug> contactList = gson.fromJson(response.toString(), type);
                bugRecyclerView = findViewById(R.id.list_bugs);
                bugRecyclerView.setHasFixedSize(true);
                bugLayoutManager = new LinearLayoutManager(AdminBugReportSystemActivity.this);
                bugRecyclerView.setLayoutManager(bugLayoutManager);
                bugSystemAdapter = new BugsSystemAdapter(AdminBugReportSystemActivity.this, contactList, null);
                bugRecyclerView.setAdapter(bugSystemAdapter);
            }
        });
    }

    private void ViewAllSystemBugs() {
        VolleyUtils.makeJsonArrayObjectRequest(Request.Method.POST, AdminBugReportSystemActivity.this, URL.HTPP_URL_ALL_SYSTEM_MESSAGES, null, new VolleyJsonArrayResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(JSONArray response) {
                ArrayList<String> list = new ArrayList<String>();
                try {
                    for (int i = 0, l = response.length(); i < l; i++) {
                        list.add(response.get(i).toString());
                    }
                    bugRecyclerView = findViewById(R.id.list_bugs);
                    bugRecyclerView.setHasFixedSize(true);
                    bugLayoutManager = new LinearLayoutManager(AdminBugReportSystemActivity.this);
                    bugRecyclerView.setLayoutManager(bugLayoutManager);
                    bugSystemAdapter = new BugsSystemAdapter(AdminBugReportSystemActivity.this, list);
                    bugRecyclerView.setAdapter(bugSystemAdapter);
                } catch (JSONException e) {
                }
                bugSystemAdapter = new BugsSystemAdapter(AdminBugReportSystemActivity.this, (ArrayList<String>) null);
            }
        });
    }


    public void GetDeteilSystemBugByName(String nameFile) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("fileName", nameFile);
            VolleyUtils.makeJsonObjectRequest(AdminBugReportSystemActivity.this, URL.HTPP_URL_GET_SYSTEM_MESSAGE, jsonBody, new VolleyJsonResponseListener() {
                @Override
                public void onError(String message) {

                }

                @Override
                public void onResponse(JSONObject response) {
                    detailNameFile = nameFile;
                    TextView tv = findViewById(R.id.TS_message);
                    tv.setText(response.toString());
                }
            });
        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
        }
    }

    public void DeleteSystemBugReportByName(String nameFile) {
        try {
            if (detailNameFile.equals(nameFile)) {
                TextView tv = findViewById(R.id.TS_message);
                tv.setText(null);
                detailNameFile = null;
            }
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("fileName", nameFile);
            VolleyUtils.makeStringObjectRequest(Request.Method.POST, AdminBugReportSystemActivity.this, URL.HTPP_URL_DELETE_SYSTEM_MESSAGE, jsonBody, new VolleyStringResponseListener() {
                @Override
                public void onError(String message) {

                }

                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    ViewAllSystemBugs();
                }
            });
        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_admin_menu, menu);
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
        switch (id) {
            case R.id.user_block:
                AdminBugReportSystemActivity.report = BugsSystemAdapter.Report.USER_BLOCK;
                startActivity(getIntent());
                return true;
            case R.id.user_bugs:
                AdminBugReportSystemActivity.report = BugsSystemAdapter.Report.USER_BUG;
                startActivity(getIntent());
                return true;
            case R.id.system_bugs:
                AdminBugReportSystemActivity.report = BugsSystemAdapter.Report.SYSTEM_BUG;
                startActivity(getIntent());
                return true;
            case R.id.action_user:
                startActivity(new Intent(this, User.class));
                return true;
            case R.id.action_logout:
                SharedPreferences mSettings = getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                mSettings.edit().clear().commit();
                startActivity(new Intent(AdminBugReportSystemActivity.this, MainActivity.class));
                return true;
        }
        //headerView.setText(item.getTitle());
        return super.onOptionsItemSelected(item);
    }

    public void SetDeteilUserBug(String description, String id) {
        detailNameFile = id;
        TextView tv = findViewById(R.id.TS_message);
        tv.setText(description);
    }

    public void DeleteUserBugReportById(String id) {
        try {
            if (detailNameFile != null && detailNameFile.equals(id)) {
                TextView tv = findViewById(R.id.TS_message);
                tv.setText(null);
                detailNameFile = null;
            }
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("messageId", id);
            VolleyUtils.makeStringObjectRequest(Request.Method.POST, AdminBugReportSystemActivity.this, URL.HTPP_URL_DELETE_USER_MESSAGE, jsonBody, new VolleyStringResponseListener() {
                @Override
                public void onError(String message) {

                }

                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    ViewAllUserBugs();
                }
            });
        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
        }
    }

    public void SetDeteilUserBlock(String description, String id) {
        detailNameFile = id;
        TextView tv = findViewById(R.id.TS_message);
        tv.setText(description);
    }

    public void DeleteUserBlockReportById(String id) {
        try {
            if (detailNameFile != null && detailNameFile.equals(id)) {
                TextView tv = findViewById(R.id.TS_message);
                tv.setText(null);
                detailNameFile = null;
            }
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("userId", id);
            VolleyUtils.makeStringObjectRequest(Request.Method.POST, AdminBugReportSystemActivity.this, URL.HTPP_URL_DELETE_USER_BLOCK, jsonBody, new VolleyStringResponseListener() {
                @Override
                public void onError(String message) {

                }

                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    ViewAllUserBlock();
                }
            });
        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
        }
    }
}