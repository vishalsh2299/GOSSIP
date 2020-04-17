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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    //Adapters provide a binding from an app-specific data set to views that are displayed within a RecyclerView
    //RecyclerView.Adapter is used to handle the data collection and bind it to the view
    //and for this two methods are overrided one to inflate the view onCreateViewHolder and one to bind data to view onBindViewHolder
    //object of this class is used in UsersFragment.java file and set to the Adapter

    private Context mContext;
    private List<User> mUsers;//Users is created in Model
    private boolean ischat;//for showing status online or offline

    String theLastMessage;

    public UserAdapter(Context mContext,List<User> mUsers,boolean ischat){
        this.mContext=mContext;
        this.mUsers=mUsers;
        this.ischat=ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       // LayoutInflater class is used to instantiate the contents of layout XML files into their corresponding View objects.

        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);//UserAdapter is this class and we are inflating the layout( user_item) in this activity
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        //in this function appropriate Username and image is set for each row
        final User user=mUsers.get(position);
        holder.username.setText(user.getUsername());//as we have inflated the layout user_item so we can use the id's username and profile_image
        if(user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
            //Glide image lib is used to fetch image from internet. Volley image lib can also be used
            //it has many advantages-check internet for more
        }

        //for last message
        if(ischat){
            lastMessage(user.getId(),holder.last_msg);
        }else {
            holder.last_msg.setVisibility(View.GONE);
        }


        //for status
        if(ischat){
            if(user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        //After creating activity_message.xml
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //RecyclerView.ViewHolder describes an item view and metadata about its place within the recycler view
        //ViewHolder holds the reference to the id of the view resource and calls to the resource will not be required. Thus performance of the application increases.

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;//for on and off status
        private ImageView img_off;
        private TextView last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.username);
            profile_image=itemView.findViewById(R.id.profile_image);
            img_on=itemView.findViewById(R.id.img_on);
            img_off=itemView.findViewById(R.id.img_off);
            last_msg=itemView.findViewById(R.id.last_msg);
        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        if(firebaseUser!=null)
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        theLastMessage = chat.getMessage();
                    }
                }

                switch (theLastMessage){
                    case "default":
                        last_msg.setText("No Message");
                        break;
                        default:
                            last_msg.setText(theLastMessage);
                            break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
