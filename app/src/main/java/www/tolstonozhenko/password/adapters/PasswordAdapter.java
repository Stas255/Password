package www.tolstonozhenko.password.adapters;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import www.tolstonozhenko.password.LoginActivity;
import www.tolstonozhenko.password.Passwords;
import www.tolstonozhenko.password.R;
import www.tolstonozhenko.password.classes.InputCheck;
import www.tolstonozhenko.password.classes.Password;
import www.tolstonozhenko.password.configuration.URL;
import www.tolstonozhenko.password.request.VolleyStringResponseListener;
import www.tolstonozhenko.password.request.VolleyUtils;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {
    public enum Action {UPDATE, GET, CREATE, NONE}

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
                        if(!InputCheck.Check(holder.ETNamePassword, 2,null)){
                            return;
                        }
                        if(!InputCheck.Check(holder.ETNewPassword, 2,null)){
                            return;
                        }
                        if(!InputCheck.Check(holder.ETVerNamePassword, 2,null)){
                            return;
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
                                VolleyUtils.makeStringObjectRequest(Request.Method.POST,pPasswords1, URL.HTPP_URL_USER_SAVE_PASSWORD,jsonBody, new VolleyStringResponseListener() {
                                    @Override
                                    public void onError(String message) {

                                    }

                                    @Override
                                    public void onResponse(String response) {
                                        String newPass = response;
                                        holder.LUnicPassword.setVisibility(View.VISIBLE);
                                        holder.ETUnicPassword.setText(newPass);
                                        holder.ETUnicPassword.setFocusable(false);
                                        pPasswords1.setAllPaswords();
                                    }
                                });
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
                        if(!InputCheck.Check(holder.ETPassword, 2,null)){
                            return;
                        }
                        if (namePass.isEmpty()) {
                            err += "Enter name password. ";
                        }
                        if (!err.isEmpty()) {
                            Toast.makeText(pPasswords1.getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                        } else {
                            JSONObject jsonBody = new JSONObject();
                            try {
                                jsonBody.put("passwordId", pPassword.id);
                                jsonBody.put("password", password);
                                VolleyUtils.makeStringObjectRequest(Request.Method.POST,pPasswords1, URL.HTPP_URL_USER_GET_PASSWORD,jsonBody, new VolleyStringResponseListener() {
                                    @Override
                                    public void onError(String message) {

                                    }

                                    @Override
                                    public void onResponse(String response) {
                                        String newPass = response;
                                        holder.LUnicPassword.setVisibility(View.VISIBLE);
                                        holder.ETUnicPassword.setText(newPass);
                                        holder.ETUnicPassword.setFocusable(false);
                                    }
                                });
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
                        if(!InputCheck.Check(holder.ETNamePassword, 2,null)){
                            return;
                        }
                        if(!InputCheck.Check(holder.ETPassword, 2,null)){
                            return;
                        }
                        if(!InputCheck.Check(holder.ETNewPassword, 2,null)){
                            return;
                        }
                        if(!InputCheck.Check(holder.ETVerNamePassword, 2,null)){
                            return;
                        }
                        if (!verPassword.equals(newPassword)) {
                            err += "New pass not ver pass. ";
                        }
                        if (!err.isEmpty()) {
                            Toast.makeText(pPasswords1.getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonBody = new JSONObject();
                                jsonBody.put("passwordId", pPassword.id);
                                jsonBody.put("passwordName", namePass);
                                if (changepassword) {
                                    jsonBody.put("passwordId", pPassword.id);
                                    jsonBody.put("passwordName", namePass);
                                    jsonBody.put("password", password);
                                    jsonBody.put("passwordNew", newPassword);
                                }
                                boolean finalChangepassword = changepassword;
                                VolleyUtils.makeStringObjectRequest(Request.Method.POST,pPasswords1, URL.HTPP_URL_USER_RESET_PASSWORD,jsonBody, new VolleyStringResponseListener() {
                                    @Override
                                    public void onError(String message) {

                                    }

                                    @Override
                                    public void onResponse(String response) {
                                        if(!finalChangepassword){
                                            String result = response;
                                            Toast.makeText(pPasswords1.getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                            pPasswords1.setAllPaswords();
                                            HideAll(holder);
                                        }else{
                                            String newPass = response;
                                            holder.LUnicPassword.setVisibility(View.VISIBLE);
                                            holder.ETUnicPassword.setText(newPass);
                                            holder.ETUnicPassword.setFocusable(false);
                                            pPasswords1.setAllPaswords();
                                        }
                                    }
                                });
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
