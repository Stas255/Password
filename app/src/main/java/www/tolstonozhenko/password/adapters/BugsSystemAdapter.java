package www.tolstonozhenko.password.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import www.tolstonozhenko.password.AdminBugReportSystemActivity;
import www.tolstonozhenko.password.R;
import www.tolstonozhenko.password.classes.UserBlock;
import www.tolstonozhenko.password.classes.UserBug;


public class BugsSystemAdapter extends RecyclerView.Adapter<BugsSystemAdapter.ViewHolder>{
    public enum Report {USER_BUG, SYSTEM_BUG, USER_BLOCK}

    AdminBugReportSystemActivity system;
    ArrayList<String> filesNameBugs;

    ArrayList<UserBug> userBugs;
    ArrayList<UserBlock> userBloks;
    Report r;

    public BugsSystemAdapter(AdminBugReportSystemActivity system, ArrayList<String> filesNameBugs) {
        this.system = system;
        this.filesNameBugs = filesNameBugs;
        this.userBugs = null;
        this.userBloks = null;
        this.r = Report.SYSTEM_BUG;
    }

    public BugsSystemAdapter(AdminBugReportSystemActivity system, ArrayList<UserBug> userBugs, String text) {
        this.system = system;
        this.userBugs = userBugs;
        this.filesNameBugs = null;
        this.userBloks = null;
        this.r = Report.USER_BUG;
    }

    public BugsSystemAdapter(ArrayList<UserBlock> userBloks , AdminBugReportSystemActivity system) {
        this.system = system;
        this.userBloks = userBloks;
        this.filesNameBugs = null;
        this.userBugs = null;
        this.r = Report.USER_BLOCK;
    }

    @NonNull
    @Override
    public BugsSystemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bugs_system, parent, false);
        return new BugsSystemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BugsSystemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(this.r == Report.SYSTEM_BUG && AdminBugReportSystemActivity.report == this.r && filesNameBugs != null) {
            holder.twBugFileName.setText(filesNameBugs.get(position));
            holder.bBugGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    system.GetDeteilSystemBugByName(filesNameBugs.get(position));
                }
            });
            holder.bBugDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    system.DeleteSystemBugReportByName(filesNameBugs.get(position));
                }
            });
        }else if(this.r == Report.USER_BUG && AdminBugReportSystemActivity.report == this.r && userBugs != null){
            holder.twBugFileName.setText(userBugs.get(position).name + "\n" + userBugs.get(position).userName);
            holder.bBugGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    system.SetDeteilUserBug(userBugs.get(position).description, userBugs.get(position).id);
                }
            });
            holder.bBugDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    system.DeleteUserBugReportById(userBugs.get(position).id);
                }
            });
        }else if(this.r == Report.USER_BLOCK && AdminBugReportSystemActivity.report == this.r && userBloks != null){
            holder.twBugFileName.setText(userBloks.get(position).userName);
            holder.bBugGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    system.SetDeteilUserBlock(userBloks.get(position).description, userBloks.get(position).id);
                }
            });
            holder.bBugDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    system.DeleteUserBlockReportById(userBloks.get(position).id);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(this.r == Report.SYSTEM_BUG && AdminBugReportSystemActivity.report == this.r && filesNameBugs != null) {
            return filesNameBugs.size();
        }else if(this.r == Report.USER_BUG && AdminBugReportSystemActivity.report == this.r && userBugs != null){
            return userBugs.size();
        }else if(this.r == Report.USER_BLOCK && AdminBugReportSystemActivity.report == this.r && userBloks != null){
            return userBloks.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView twBugFileName;
        Button bBugGet;
        Button bBugDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.twBugFileName = itemView.findViewById(R.id.bug_file_name);
            this.bBugGet = itemView.findViewById(R.id.b_get_detail);
            this.bBugDelete = itemView.findViewById(R.id.b_delete);
        }
    }

}

