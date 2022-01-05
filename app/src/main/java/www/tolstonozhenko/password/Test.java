package www.tolstonozhenko.password;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Test extends AppCompatActivity {

    public void onButtonClick1(View view) throws JSONException {
        final TextView textView1 = (TextView) findViewById(R.id.text1);
        final TextView textView2 = (TextView) findViewById(R.id.text2);

        JSONObject testJson = new JSONObject();
        testJson.put("userId", "123456");
        testJson.put("title", "Test title");

        RequestQueue requestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);

        requestQueue.start();

        String url ="https://jsonplaceholder.typicode.com/posts";
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, testJson,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            textView1.setText("Response is: "+ response.getString("userId"));
                            textView2.setText("Response is: "+ response.getString("title"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textView1.setText("That didn't work!");
                    }
                });

        requestQueue.add(stringRequest);
        //queue.add(stringRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }



}