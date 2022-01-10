package www.tolstonozhenko.password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import www.tolstonozhenko.password.configuration.URL;
import www.tolstonozhenko.password.request.VolleyJsonResponseListener;
import www.tolstonozhenko.password.request.VolleyStringResponseListener;
import www.tolstonozhenko.password.request.VolleyUtils;

public class QRActivity extends AppCompatActivity {
    int CAMERA_REQUEST_CODE = 101;

    CodeScanner mCodeScanner;
    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qractivity);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mSettings = getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonBody = new JSONObject();
                            jsonBody.put("idQR", result.getText());
                            if(mSettings.contains(LoginActivity.APP_PREFERENCES_TOKEN) && mSettings.contains(LoginActivity.APP_PREFERENCES_USER)) {
                                jsonBody.put("user", mSettings.getString(LoginActivity.APP_PREFERENCES_USER,""));
                            }
                            VolleyUtils.makeJsonObjectRequest(QRActivity.this, URL.HTPP_QR_RESPONSE, jsonBody, new VolleyJsonResponseListener() {
                                @Override
                                public void onError(String message) {

                                }

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Toast.makeText(QRActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(new Intent(QRActivity.this, User.class));
                                }
                            });
                        } catch (JSONException e) {
                            Log.e("MYAPP", "unexpected JSON exception", e);
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

}