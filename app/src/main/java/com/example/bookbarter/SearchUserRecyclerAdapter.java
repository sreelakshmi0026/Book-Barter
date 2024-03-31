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

import java.util.ArrayList;

public class SearchUserRecyclerAdapter extends RecyclerView.Adapter<SearchUserRecyclerAdapter.MyViewHolder> {
    Context context;
    FirebaseAuth fAuth;
    ArrayList<UserModel> usersList;
    FirebaseFirestore fStore;
    FirebaseUser user;
    public void setFilteredList(ArrayList<UserModel> array)
    {
        this.usersList=array;
        notifyDataSetChanged();
    }
    public SearchUserRecyclerAdapter(Context context, ArrayList<UserModel> array) {
        super();
        this.context = context;
        this.usersList=array;
    }

    @NonNull
    @Override
    public SearchUserRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserRecyclerAdapter.MyViewHolder holder, int position) {
        UserModel item=usersList.get(position);
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        fAuth= FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        holder.profilePic.setImageBitmap(null);
        holder.usernameText.setText(item.name);
        StringBuilder myName = new StringBuilder(item.phonenumber);
        myName.setCharAt(3, '*');
        myName.setCharAt(7, '*');
        myName.setCharAt(6, '*');
        holder.phoneText.setText(myName);
        //Toast.makeText(context, ""+item.userid, Toast.LENGTH_SHORT).show();
        if(item.userid.equals(fAuth.getCurrentUser().getUid())){
            holder.usernameText.setText(item.name+" (Me)");
        }
        if(!item.profilepic.isEmpty())
        {
            Glide.with(context)
                    .load(item.profilepic)
                    .circleCrop()
                    .into(holder.profilePic);
        }
        else {
            Glide.with(context)
                    .load(R.drawable.baseline_person_outline_24)
                    .circleCrop()
                    .into(holder.profilePic);
        }


        holder.itemView.setOnClickListener(v -> {
            //navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("id",item.userid);
            intent.putExtra("name",item.name);
            intent.putExtra("phonenumber",item.phonenumber);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView phoneText;
        ImageView profilePic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
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
