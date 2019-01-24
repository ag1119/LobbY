package com.example.abhishek.lobby;

/**
 * Created by Abhishek on 4/26/2018.
 */
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;

    public MessageAdapter(List<Messages> mMessageList){

        this.mMessageList=mMessageList;
    }




    public MessageViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout,parent,false);


        return new MessageViewHolder(v);
    }




    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageTextReceived,messageTextSent;
       // public CircleImageView profileImage;


        public MessageViewHolder(View itemView) {
            super(itemView);

            messageTextReceived=(TextView) itemView.findViewById(R.id.message_text_received);
            messageTextSent=(TextView) itemView.findViewById(R.id.message_text_sent);

            //profileImage=(CircleImageView)itemView.findViewById(R.id.message_profile_pic);

        }
    }


    @Override
    public void onBindViewHolder(MessageViewHolder mholder, int i) {

        if(mMessageList.size()>0){
        if(mMessageList.get(i).getFrom().equals(ChatActivity.mCurrentusername))
        {
            mholder.messageTextReceived.setVisibility(View.VISIBLE);
            mholder.messageTextSent.setVisibility(View.INVISIBLE);
            mholder.messageTextReceived.setText(mMessageList.get(i).getMessage());
        }
        else
        {
            mholder.messageTextReceived.setVisibility(View.INVISIBLE);
            mholder.messageTextSent.setVisibility(View.VISIBLE);
            mholder.messageTextSent.setText(mMessageList.get(i).getMessage());
        }}

    }

    @Override
    public int getItemCount() {

        return mMessageList.size();
    }




}
