package www.tolstonozhenko.password;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import www.tolstonozhenko.password.configuration.DB;
import www.tolstonozhenko.password.request.VolleyResponseListener;
import www.tolstonozhenko.password.request.VolleyUtils;

public class MainActivity extends AppCompatActivity {
    float x1,x2,y1,y2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button3 = (Button) findViewById(R.id.GetButton);

        EditText etUPass = (EditText) findViewById(R.id.ETUnicPasswordMain);
        etUPass.setText("");
        etUPass.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.TVUnicPasswordMain)).setVisibility(View.GONE);
        TextView tVPass = (TextView) findViewById(R.id.TVUnicPasswordMain2);
        tVPass.setText("");

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = ((EditText) findViewById(R.id.ETPasswordMain)).getText().toString();
                EditText etUPass = (EditText) findViewById(R.id.ETUnicPasswordMain);
                etUPass.setText("");
                etUPass.setVisibility(View.GONE);
                ((TextView) findViewById(R.id.TVUnicPasswordMain)).setVisibility(View.GONE);
                TextView tVPass = (TextView) findViewById(R.id.TVUnicPasswordMain2);
                tVPass.setText("");
                if(pass.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter test password", Toast.LENGTH_SHORT).show();
                }else {
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("password", pass);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    VolleyUtils.makeJsonObjectRequest(MainActivity.this, DB.HTPP_URL_GET_UNIC_PASSWORD, jsonBody, new VolleyResponseListener() {
                        @Override
                        public void onError(String message) {

                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                EditText etUPass = (EditText) findViewById(R.id.ETUnicPasswordMain);
                                etUPass.setText(response.getString("uniqPassword"));
                                etUPass.setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.TVUnicPasswordMain)).setVisibility(View.VISIBLE);
                                TextView tVPass = (TextView) findViewById(R.id.TVUnicPasswordMain2);
                                tVPass.setText("Time to hack " + response.getString("aboutPassword"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
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
                    startActivity(new Intent(MainActivity.this, Login.class));
                }else if(x1 > x2 && ((x1-x2)/width) > 0.6){
                    startActivity(new Intent(MainActivity.this, Register.class));
                }
            }
        }
        return false;
    }
}

