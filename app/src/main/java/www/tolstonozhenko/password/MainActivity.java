package www.tolstonozhenko.password;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.Lbutton);
        Button button2 = (Button) findViewById(R.id.LRbutton);

        Button button3 = (Button) findViewById(R.id.GetButton);

        EditText etUPass = (EditText) findViewById(R.id.ETUnicPasswordMain);
        etUPass.setText("");
        etUPass.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.TVUnicPasswordMain)).setVisibility(View.GONE);
        TextView tVPass = (TextView) findViewById(R.id.TVUnicPasswordMain2);
        tVPass.setText("");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });
        MainActivity m = this;
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
                }else{
                    RequestQueue queue = Volley.newRequestQueue(m);
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("password", pass);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, //POST - API-запрос для получение данных
                            "http://localhost:8000/simple/getUnicPassword/", jsonBody, new Response.Listener<JSONObject>() {
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
                    }, new Response.ErrorListener() { // в случае возникновеня ошибки
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
                    });
                    queue.add(request);
                }
            }
        });


    }
}