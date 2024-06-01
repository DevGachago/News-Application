package com.example.newsapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newsapplication.R;
import com.example.newsapplication.models.Users;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<Users> userList;
    private OnUserActionListener userActionListener;

    public UsersAdapter(List<Users> userList, OnUserActionListener userActionListener) {
        this.userList = userList;
        this.userActionListener = userActionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users user = userList.get(position);

        holder.textViewUserId.setText(user.getUserId());
        holder.textViewEmail.setText(user.getEmail());
        holder.textViewName.setText(user.getUsername());

        // Set verification status text based on isVerified field
        String verificationStatus = user.isVerified() ? "Verified" : "Not Verified";
        holder.textViewVerificationStatus.setText(verificationStatus);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnUserActionListener {
        void onVerifyUser(Users user);
        void onDeVerifyUser(Users user);
    }

    public void setOnUserActionListener(OnUserActionListener listener) {
        this.userActionListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewUserId;
        private TextView textViewName;
        private TextView textViewEmail;
        private TextView textViewVerificationStatus;
        private Button buttonVerify;
        private Button buttonDeVerify;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserId = itemView.findViewById(R.id.textViewUserId);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewVerificationStatus = itemView.findViewById(R.id.textViewVerificationStatus);
            buttonVerify = itemView.findViewById(R.id.buttonVerify);
            buttonDeVerify = itemView.findViewById(R.id.buttonDeVerify);

            buttonVerify.setOnClickListener(v -> {
                if (userActionListener != null) {
                    userActionListener.onVerifyUser(userList.get(getAdapterPosition()));
                }
            });

            buttonDeVerify.setOnClickListener(v -> {
                if (userActionListener != null) {
                    userActionListener.onDeVerifyUser(userList.get(getAdapterPosition()));
                }
            });
        }
    }
}
