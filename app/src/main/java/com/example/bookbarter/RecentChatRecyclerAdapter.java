package com.example.bookbarter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {
    Context context;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }
    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        fStore=FirebaseFirestore.getInstance();
        List<String> userIds=model.getUserIds();
        //Toast.makeText(context, ""+userIds.size(), Toast.LENGTH_SHORT).show();
        //holder.profilePic.setImageBitmap(null);
        if(userIds.get(0).equals(user.getUid())){
            FirebaseFirestore.getInstance().collection("users").document(userIds.get(1)).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(user.getUid());


                    UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                    String urlpic= otherUserModel.getProfilepic();
                    if(!urlpic.isEmpty())
                    {
                        Glide.with(context).load(otherUserModel.getProfilepic()).circleCrop().into(holder.profilePic);
                    }
                    else {
                       // Toast.makeText(context, ""+otherUserModel.getName(), Toast.LENGTH_SHORT).show();
                        Glide.with(context).load(R.drawable.baseline_person_outline_24).circleCrop().into(holder.profilePic);
                    }
                    holder.usernameText.setText(otherUserModel.getName());
                    if(lastMessageSentByMe)
                        holder.lastMessageText.setText("You : "+model.getLastMessage());
                    else
                        holder.lastMessageText.setText(model.getLastMessage());
                    holder.lastMessageTime.setText(new SimpleDateFormat("HH:MM").format(model.getLastMessageTimestamp().toDate()));

                    holder.itemView.setOnClickListener(v -> {
                        //navigate to chat activity
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("name",otherUserModel.getName());
                        intent.putExtra("phonenumber",otherUserModel.getPhonenumber());
                        intent.putExtra("id",otherUserModel.getUserid());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });

                }
            });
        }else{
            FirebaseFirestore.getInstance().collection("users").document(userIds.get(0)).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(user.getUid());


                    UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                    String urlpic= otherUserModel.getProfilepic();
                    if(!urlpic.isEmpty())
                    {
                        Glide.with(context).load(otherUserModel.getProfilepic()).circleCrop().into(holder.profilePic);
                    }
                    else {
                        // Toast.makeText(context, ""+otherUserModel.getName(), Toast.LENGTH_SHORT).show();
                        Glide.with(context).load(R.drawable.baseline_person_outline_24).circleCrop().into(holder.profilePic);
                    }

                    holder.usernameText.setText(otherUserModel.getName());
                    if(lastMessageSentByMe)
                        holder.lastMessageText.setText("You : "+model.getLastMessage());
                    else
                        holder.lastMessageText.setText(model.getLastMessage());
                    holder.lastMessageTime.setText(new SimpleDateFormat("HH:MM").format(model.getLastMessageTimestamp().toDate()));

                    holder.itemView.setOnClickListener(v -> {
                        //navigate to chat activity
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("name",otherUserModel.getName());
                        intent.putExtra("phonenumber",otherUserModel.getPhonenumber());
                        intent.putExtra("id",otherUserModel.getUserid());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });

                }
            });
        }

    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_view,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
