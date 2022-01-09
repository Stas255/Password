package www.tolstonozhenko.password;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import www.tolstonozhenko.password.adapters.BugsSystemAdapter;

public class MainAdminActivity extends AppCompatActivity {
    private static SharedPreferences mSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
        mSettings = getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_admin_menu, menu);
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
                startActivity(new Intent(this, User.class));
                return true;
            case R.id.action_logout:
                SharedPreferences mSettings = getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                mSettings.edit().clear().commit();
                startActivity(new Intent(MainAdminActivity.this, MainActivity.class));
                return true;
        }
        //headerView.setText(item.getTitle());
        return super.onOptionsItemSelected(item);
    }
}