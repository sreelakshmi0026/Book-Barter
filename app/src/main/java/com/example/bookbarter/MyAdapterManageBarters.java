package com.example.bookbarter;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapterManageBarters extends RecyclerView.Adapter<MyAdapterManageBarters.MyViewHolder>{
    Context context;
    ArrayList<ModelManageBarter> postArray;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;

    public MyAdapterManageBarters(Context context, ArrayList<ModelManageBarter> postArray) {
        this.context = context;
        this.postArray = postArray;
    }
    @NonNull
    @Override
    public MyAdapterManageBarters.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.managebarters_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelManageBarter item=postArray.get(position);
        holder.mb_name.setText(item.authorname);
        holder.mb_name.setText(item.bookname);
        holder.mb_author.setText("by ~ "+item.authorname);
        holder.mb_genre.setText("Genre : "+item.genre);
        holder.mb_description.setText(item.description);
        holder.mb_rating.setText("Rating : "+(int) item.rating+"/5");
        //holder.book_coverPic.setImageURI(Uri.parse(post_item.imageuri));
        Glide.with(context).load(item.imageuri).into(holder.mb_coverPic);
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        fAuth= FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        holder.mb_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure?")
                        .setMessage("\nNote: This action is not reversible\nPlease click Cancel if not sure")
                        .setView(layout)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fStore.collection("users").document(user.getUid()).update("booksposted",FieldValue.arrayRemove(item.id));
                                fStore.collection("books").document(item.id).delete();
                                Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                                postArray.remove(position);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        }).show();
            }
        });
        holder.viewRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewRequests.class);
                Bundle bundle = new Bundle();

                bundle.putString("id", item.id);

                i.putExtras(bundle);

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postArray.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mb_name,mb_author,mb_genre,mb_description,mb_rating;
        ImageView mb_coverPic;
        Button mb_delete,viewRequests;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mb_name=itemView.findViewById(R.id.mb_name);
            mb_author=itemView.findViewById(R.id.mb_author);
            mb_genre=itemView.findViewById(R.id.mb_genre);
            mb_rating=itemView.findViewById(R.id.mb_rating);
            mb_description=itemView.findViewById(R.id.mb_description);
            mb_coverPic=itemView.findViewById(R.id.mb_coverpic);
            mb_delete=itemView.findViewById(R.id.mb_delete);
            viewRequests=itemView.findViewById(R.id.viewRequests);
        }
    }

}
