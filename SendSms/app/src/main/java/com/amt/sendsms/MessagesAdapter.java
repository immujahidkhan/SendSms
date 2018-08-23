package com.amt.sendsms;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    List<SMSModelClass> mMessagelist;
    Context context;

    public MessagesAdapter(List<SMSModelClass> mMessagelist, Context context) {
        this.mMessagelist = mMessagelist;
        this.context = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false));
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        SMSModelClass data = mMessagelist.get(position);

        String from_user = data.getType();
        if (data.getType().equals("2")) {
            holder.MyMessage.setVisibility(View.VISIBLE);
            holder.MyDate.setVisibility(View.VISIBLE);
            holder.MyDate.setText(data.getDate());
            holder.MyMessage.setText(data.getBody());
        } else {
            holder.TheirMessage.setVisibility(View.VISIBLE);
            holder.TheirDate.setVisibility(View.VISIBLE);
            holder.TheirDate.setText(data.getDate());
            holder.TheirMessage.setText(data.getBody());
        }
    }

    @Override
    public int getItemCount() {
        return mMessagelist.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView MyMessage, TheirMessage, MyDate, TheirDate;

        public MessageViewHolder(View itemView) {
            super(itemView);
            MyMessage = itemView.findViewById(R.id.MyMessage);
            TheirMessage = itemView.findViewById(R.id.TheirMessage);
            MyDate = itemView.findViewById(R.id.Mydate);
            TheirDate = itemView.findViewById(R.id.Theirdate);
            MyMessage.setVisibility(View.GONE);
            TheirMessage.setVisibility(View.GONE);
            MyDate.setVisibility(View.GONE);
            TheirDate.setVisibility(View.GONE);
        }
    }
}