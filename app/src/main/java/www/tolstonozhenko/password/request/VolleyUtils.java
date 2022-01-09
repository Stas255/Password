package www.tolstonozhenko.password.request;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import www.tolstonozhenko.password.LoginActivity;
import www.tolstonozhenko.password.MainActivity;

public class VolleyUtils {
    static SharedPreferences mSettings;
    public static void makeJsonObjectRequest(Context context, String url, JSONObject jsonBody, final VolleyJsonResponseListener listener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (url, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse != null && error.networkResponse.statusCode == 401){
                            String text = null;
                            try {
                                text = new String(error.networkResponse.data, "UTF-8");
                                if(text.equals("Time working token lost!")){
                                    SharedPreferences mSettings = context.getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                                    mSettings.edit().clear().commit();
                                    Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                                    context.startActivity(new Intent(context, LoginActivity.class));
                                    return;
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        listener.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                String token = null;
                mSettings = context.getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                if (mSettings.contains(LoginActivity.APP_PREFERENCES_TOKEN)) {
                    token = mSettings.getString(LoginActivity.APP_PREFERENCES_TOKEN, "");
                }
                headers.put("x-access-token", token);

                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };

        // Access the RequestQueue through singleton class.
        Volley.newRequestQueue(context).add(jsonObjectRequest);
        ;
    }

    public static void makeStringObjectRequest(int method, Context context, String url, JSONObject jsonBody, final VolleyStringResponseListener listener) {
        StringRequest jsonObjectRequest = new StringRequest
                (method, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        JSONObject body;
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                body = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                                listener.onError(body.toString());
                                return;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(error.networkResponse != null && error.networkResponse.statusCode == 401){
                                String text = null;
                                try {
                                    text = new String(error.networkResponse.data, "UTF-8");
                                    if(text.equals("Time working token lost!")){
                                        SharedPreferences mSettings = context.getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                                        mSettings.edit().clear().commit();
                                        Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                                        context.startActivity(new Intent(context, LoginActivity.class));
                                        return;
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        error.printStackTrace();
                        listener.onError(error.toString());
                    }
                }) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                String token = null;
                mSettings = context.getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                if (mSettings.contains(LoginActivity.APP_PREFERENCES_TOKEN)) {
                    token = mSettings.getString(LoginActivity.APP_PREFERENCES_TOKEN, "");
                }
                headers.put("x-access-token", token);

                return headers;
            }


            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        // Access the RequestQueue through singleton class.
        Volley.newRequestQueue(context).add(jsonObjectRequest);
        ;
    }

    public static void makeJsonArrayObjectRequest(int method, Context context, String url, JSONArray jsonBody, final VolleyJsonArrayResponseListener listener) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (method, url,jsonBody, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        JSONObject body;
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                body = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                                listener.onError(body.toString());
                                return;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if(error.networkResponse != null && error.networkResponse.statusCode == 401){
                            String text = null;
                            try {
                                text = new String(error.networkResponse.data, "UTF-8");
                                if(text.equals("Time working token lost!")){
                                    SharedPreferences mSettings = context.getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                                    mSettings.edit().clear().commit();
                                    Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                                    context.startActivity(new Intent(context, LoginActivity.class));
                                    return;
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        error.printStackTrace();
                        listener.onError(error.toString());
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                String token = null;
                mSettings = context.getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                if (mSettings.contains(LoginActivity.APP_PREFERENCES_TOKEN)) {
                    token = mSettings.getString(LoginActivity.APP_PREFERENCES_TOKEN, "");
                }
                headers.put("x-access-token", token);

                return headers;
            }
        };

        // Access the RequestQueue through singleton class.
        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }
}
