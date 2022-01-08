package www.tolstonozhenko.password;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import www.tolstonozhenko.password.classes.Password;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {
    enum Action {UPDATE, GET, CREATE, NONE}

    Password pPassword;
    public Action pAction;
    Passwords pPasswords1;
    SharedPreferences mSettings;

    public PasswordAdapter(Password password, Action action, Passwords passwords1) {
        pPassword = password;
        pAction = action;
        pPasswords1 = passwords1;
        mSettings = pPasswords1.getSharedPreferences(LoginActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public PasswordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.password_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HideAll(holder);
        Refresh(holder);
        holder.BCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pAction = Action.NONE;
                HideAll(holder);
            }
        });
        holder.ETUnicPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) pPasswords1.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", holder.ETUnicPassword.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(pPasswords1.getApplicationContext(), "Copied", Toast.LENGTH_SHORT).show();
            }
        });
        switch (pAction) {
            case NONE: {
                HideAll(holder);
            }
            break;
            case CREATE: {
                holder.LNamePassword.setVisibility(View.VISIBLE);
                holder.LNewPassword.setVisibility(View.VISIBLE);
                holder.LVerNamePassword.setVisibility(View.VISIBLE);
                holder.LActionPassword.setVisibility(View.VISIBLE);
                holder.BAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestQueue queue = Volley.newRequestQueue(pPasswords1);
                        String namePass = holder.ETNamePassword.getText().toString();
                        String newPass = holder.ETNewPassword.getText().toString();
                        String verPass = holder.ETVerNamePassword.getText().toString();
                        String err = "";
                        if (namePass.isEmpty()) {
                            err += "Enter name password. ";
                        }
                        if (newPass.isEmpty()) {
                            err += "Enter new password. ";
                        }
                        if (verPass.isEmpty()) {
                            err += "Enter ver password. ";
                        }
                        if (!newPass.equals(verPass)) {
                            err += "New pass not ver pass. ";
                        }
                        if (!err.isEmpty()) {
                            Toast.makeText(pPasswords1.getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                        } else {
                            JSONObject jsonBody = new JSONObject();
                            try {
                                jsonBody.put("name", namePass);
                                jsonBody.put("password", newPass);
                                final StringRequest request = new StringRequest(Request.Method.POST, //POST - API-запрос для получение данных
                                        "http://localhost:8000/api/user/savepassword", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        String newPass = response;
                                        holder.LUnicPassword.setVisibility(View.VISIBLE);
                                        holder.ETUnicPassword.setText(newPass);
                                        holder.ETUnicPassword.setFocusable(false);
                                        pPasswords1.setAllPaswords();
                                    }
                                }, new Response.ErrorListener() { // в случае возникновеня ошибки
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        JSONObject body;
                                        if (error.networkResponse != null && error.networkResponse.data != null) {
                                            try {
                                                body = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                                                Toast.makeText(pPasswords1.getApplicationContext(), body.getString("message"), Toast.LENGTH_SHORT).show();
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
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
                                        if (mSettings.contains(LoginActivity.APP_PREFERENCES_TOKEN)) {
                                            token = mSettings.getString(LoginActivity.APP_PREFERENCES_TOKEN, "");
                                        }
                                        headers.put("x-access-token", token);

                                        return headers;
                                    }
                                };
                                ;
                                queue.add(request);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            }
            break;
            case GET: {
                holder.LNamePassword.setVisibility(View.VISIBLE);
                holder.LPassword.setVisibility(View.VISIBLE);
                holder.LActionPassword.setVisibility(View.VISIBLE);

                holder.ETNamePassword.setText(pPassword.namePassword);
                holder.ETNamePassword.setFocusable(false);

                holder.BAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestQueue queue = Volley.newRequestQueue(pPasswords1);
                        String namePass = pPassword.namePassword;
                        String password = holder.ETPassword.getText().toString();
                        String err = "";
                        if (namePass.isEmpty()) {
                            err += "Enter name password. ";
                        }
                        if (password.isEmpty()) {
                            err += "Enter password. ";
                        }
                        if (!err.isEmpty()) {
                            Toast.makeText(pPasswords1.getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                        } else {
                            JSONObject jsonBody = new JSONObject();
                            try {
                                jsonBody.put("passwordId", pPassword.id);
                                jsonBody.put("password", password);
                                final StringRequest request = new StringRequest(Request.Method.POST, //POST - API-запрос для получение данных
                                        "http://localhost:8000/api/user/getpassword", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        String newPass = response;
                                        holder.LUnicPassword.setVisibility(View.VISIBLE);
                                        holder.ETUnicPassword.setText(newPass);
                                        holder.ETUnicPassword.setFocusable(false);
                                    }
                                }, new Response.ErrorListener() { // в случае возникновеня ошибки
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        JSONObject body;
                                        if (error.networkResponse != null && error.networkResponse.data != null) {
                                            try {
                                                body = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                                                Toast.makeText(pPasswords1.getApplicationContext(), body.getString("message"), Toast.LENGTH_SHORT).show();
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
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
                                        if (mSettings.contains(LoginActivity.APP_PREFERENCES_TOKEN)) {
                                            token = mSettings.getString(LoginActivity.APP_PREFERENCES_TOKEN, "");
                                        }
                                        headers.put("x-access-token", token);

                                        return headers;
                                    }
                                };
                                ;
                                queue.add(request);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
            break;
            case UPDATE: {
                holder.LNamePassword.setVisibility(View.VISIBLE);
                holder.LPassword.setVisibility(View.VISIBLE);
                holder.LNewPassword.setVisibility(View.VISIBLE);
                holder.LVerNamePassword.setVisibility(View.VISIBLE);
                holder.LActionPassword.setVisibility(View.VISIBLE);

                holder.ETNamePassword.setText(pPassword.namePassword);

                holder.BAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestQueue queue = Volley.newRequestQueue(pPasswords1);
                        String namePass = holder.ETNamePassword.getText().toString();
                        String password = holder.ETPassword.getText().toString();
                        String newPassword = holder.ETNewPassword.getText().toString();
                        String verPassword = holder.ETVerNamePassword.getText().toString();
                        boolean changepassword = false;
                        String err = "";
                        if (namePass.isEmpty()) {
                            err += "Enter name password. ";
                        }
                        if (!password.isEmpty()) {
                            if (verPassword.isEmpty() || newPassword.isEmpty()) {
                                err += "Enter new or ver password. If you want change password. ";
                            } else if (!verPassword.equals(newPassword)) {
                                err += "New pass not ver pass. ";
                            }
                            changepassword = true;
                        }
                        if (!err.isEmpty()) {
                            Toast.makeText(pPasswords1.getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonBody = new JSONObject();
                                jsonBody.put("passwordId", pPassword.id);
                                jsonBody.put("passwordName", namePass);

                                if (!changepassword) {
                                    final StringRequest request = new StringRequest(Request.Method.POST, //POST - API-запрос для получение данных
                                            "http://localhost:8000/api/user/resetpassword", new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            String result = response;
                                            Toast.makeText(pPasswords1.getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                            pPasswords1.setAllPaswords();
                                            HideAll(holder);
                                        }
                                    }, new Response.ErrorListener() { // в случае возникновеня ошибки
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            JSONObject body;
                                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                                try {
                                                    body = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                                                    Toast.makeText(pPasswords1.getApplicationContext(), body.getString("message"), Toast.LENGTH_SHORT).show();
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
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
                                            if (mSettings.contains(LoginActivity.APP_PREFERENCES_TOKEN)) {
                                                token = mSettings.getString(LoginActivity.APP_PREFERENCES_TOKEN, "");
                                            }
                                            headers.put("x-access-token", token);

                                            return headers;
                                        }
                                    };
                                    ;
                                    queue.add(request);
                                } else {
                                    jsonBody.put("passwordId", pPassword.id);
                                    jsonBody.put("passwordName", namePass);
                                    jsonBody.put("password", password);
                                    jsonBody.put("passwordNew", newPassword);
                                    final StringRequest request = new StringRequest(Request.Method.POST, //POST - API-запрос для получение данных
                                            "http://localhost:8000/api/user/resetpassword", new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            String newPass = response;
                                            holder.LUnicPassword.setVisibility(View.VISIBLE);
                                            holder.ETUnicPassword.setText(newPass);
                                            holder.ETUnicPassword.setFocusable(false);
                                            pPasswords1.setAllPaswords();
                                        }
                                    }, new Response.ErrorListener() { // в случае возникновеня ошибки
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            JSONObject body;
                                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                                try {
                                                    body = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                                                    Toast.makeText(pPasswords1.getApplicationContext(), body.getString("message"), Toast.LENGTH_SHORT).show();
                                                    pPasswords1.setAllPaswords();
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
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
                                            if (mSettings.contains(LoginActivity.APP_PREFERENCES_TOKEN)) {
                                                token = mSettings.getString(LoginActivity.APP_PREFERENCES_TOKEN, "");
                                            }
                                            headers.put("x-access-token", token);

                                            return headers;
                                        }
                                    };
                                    queue.add(request);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
            break;
        }
    }

    private void Refresh(ViewHolder holder) {
        holder.ETNamePassword.setFocusable(true);
        holder.ETUnicPassword.setFocusable(true);
    }

    public void HideAll(ViewHolder holder) {
        holder.LNamePassword.setVisibility(View.GONE);
        holder.LPassword.setVisibility(View.GONE);
        holder.LNewPassword.setVisibility(View.GONE);
        holder.LVerNamePassword.setVisibility(View.GONE);
        holder.LActionPassword.setVisibility(View.GONE);
        holder.LUnicPassword.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout LNamePassword;
        public TextView TVNamePassword;
        public EditText ETNamePassword;

        public LinearLayout LPassword;
        public TextView TVPassword;
        public EditText ETPassword;

        public LinearLayout LNewPassword;
        public TextView TVNewPassword;
        public EditText ETNewPassword;

        public LinearLayout LVerNamePassword;
        public TextView TVVerNamePassword;
        public EditText ETVerNamePassword;

        public LinearLayout LUnicPassword;
        public TextView TVUnicPassword;
        public EditText ETUnicPassword;

        public LinearLayout LActionPassword;
        public Button BAdd;
        public Button BCansel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            LNamePassword = itemView.findViewById(R.id.l_name_password);
            TVNamePassword = itemView.findViewById(R.id.tv_name_password);
            ETNamePassword = itemView.findViewById(R.id.et_name_password);

            LPassword = itemView.findViewById(R.id.l_password);
            TVPassword = itemView.findViewById(R.id.tv_password);
            ETPassword = itemView.findViewById(R.id.et_password);

            LNewPassword = itemView.findViewById(R.id.l_new_password);
            TVNewPassword = itemView.findViewById(R.id.tv_new_password);
            ETNewPassword = itemView.findViewById(R.id.et_new_password);

            LVerNamePassword = itemView.findViewById(R.id.l_ver_new_password);
            TVVerNamePassword = itemView.findViewById(R.id.tv_ver_new_password);
            ETVerNamePassword = itemView.findViewById(R.id.et_ver_new_password);

            LUnicPassword = itemView.findViewById(R.id.l_unic_password);
            TVUnicPassword = itemView.findViewById(R.id.tv_unic_password);
            ETUnicPassword = itemView.findViewById(R.id.et_unic_password);

            LActionPassword = itemView.findViewById(R.id.l_action_password);
            BAdd = itemView.findViewById(R.id.bAdd);
            BCansel = itemView.findViewById(R.id.bCansel);
        }
    }
}
