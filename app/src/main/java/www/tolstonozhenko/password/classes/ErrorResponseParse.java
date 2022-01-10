package www.tolstonozhenko.password.classes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import www.tolstonozhenko.password.LoginActivity;

public class ErrorResponseParse {
    VolleyError error;

    public ErrorResponseParse(VolleyError error) {
        this.error = error;
    }

    public void Parse(Context context) {
        Gson gson = new Gson();
        if(this.error instanceof TimeoutError){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Message").setMessage("Time out request. Repeat please").setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create();
            builder.show();

            return;
        }
        try {
            Type type = new TypeToken<Message>() {
            }.getType();
            String jsonString = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers));
            Message errorResponse = gson.fromJson(jsonString, type);
            if (error.networkResponse.statusCode == 401 && errorResponse.message.equals("Time working token lost!")) {
                SharedPreferences mSettings = context.getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                mSettings.edit().clear().commit();
                Toast.makeText(context.getApplicationContext(), errorResponse.message, Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, LoginActivity.class));
                return;
            }
            if(error.networkResponse.statusCode == 403 || error.networkResponse.statusCode == 404){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Message").setMessage(errorResponse.message).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create();
                builder.show();

                return;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    class Message {
        String message;
    }
}
