package www.tolstonozhenko.password;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import www.tolstonozhenko.password.classes.Password;

public class PasswordsAdapter extends RecyclerView.Adapter<PasswordsAdapter.ViewHolder> {

    ArrayList<Password> pPasswords;
    Passwords pPasswords1;

    public PasswordsAdapter(ArrayList<Password> passwords, Passwords passwords1) {
        pPasswords = passwords;
        pPasswords1 = passwords1;
    }

    @NonNull
    @Override
    public PasswordsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.pName.setText(pPasswords.get(position).namePassword);
        holder.bGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pPasswords1.GetPasswordById(pPasswords.get(position).id, PasswordAdapter.Action.GET);
            }
        });
        holder.bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pPasswords1.GetPasswordById(pPasswords.get(position).id, PasswordAdapter.Action.UPDATE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pPasswords.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView pName;
        public Button bGet;
        public Button bUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pName = itemView.findViewById(R.id.password_name);
            bGet = itemView.findViewById(R.id.b_get_password);
            bUpdate = itemView.findViewById(R.id.b_update_password);
        }
    }
}
