package com.example.gossip.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gossip.MessageActivity;
import com.example.gossip.Model.Chat;
import com.example.gossip.Model.User;
import com.example.gossip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {//approx same code as User

    //Adapters provide a binding from an app-specific data set to views that are displayed within a RecyclerView
    //RecyclerView.Adapter is used to handle the data collection and bind it to the view
    //and for this two methods are overrided one to inflate the view onCreateViewHolder and one to bind data to view onBindViewHolder

    public static final int MSG_TYPE_LEFT=0;//for message from receiver that is shown on left side
    public static final int MSG_TYPE_RIGHT=1;//for message from sender that is shown on right side

    FirebaseUser fuser;

    private Context mContext;
    private List<Chat> mChat;//Chat is created in Model
    private String imageurl;

    public MessageAdapter(Context mContext,List<Chat> mChat,String imageurl){
        this.mContext=mContext;
        this.mChat=mChat;
        this.imageurl=imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==MSG_TYPE_RIGHT) {//if sender side message
            // LayoutInflater class is used to instantiate the contents of layout XML files into their corresponding View objects.

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);//MessageAdapter is this class and we are inflating the layout( user_item) in this activity
        }else {//if user side message
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat=mChat.get(position);
        holder.show_message.setText(chat.getMessage());

        if(imageurl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        if(position == mChat.size()-1){
            if(chat.isIsseen()){
                holder.txt_seen.setText("seen");
            }else {
                holder.txt_seen.setText("Delivered");
            }
        }else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //RecyclerView.ViewHolder describes an item view and metadata about its place within the recycler view
        //ViewHolder holds the reference to the id of the view resource and calls to the resource will not be required. Thus performance of the application increases.

        public TextView show_message;
        public ImageView profile_image;

        public TextView txt_seen;//for showing text is seen or not

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message=itemView.findViewById(R.id.show_message);
            profile_image=itemView.findViewById(R.id.profile_image);
            txt_seen=itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //it is used when there are different view in different rows like for senders message its textview and for receivers message it is textview with receivers image
        //so it will get user from firebase and then return accordingly

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
