package com.example.baat.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baat.R;

public class UsersAdapter {
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView userName,lastMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.profile_image);
            userName=itemView.findViewById(R.id.userName);

        }
    }
}
