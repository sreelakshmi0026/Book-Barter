package com.example.bookbarter;

import static android.content.Intent.getIntent;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyAdapterPost extends RecyclerView.Adapter<MyAdapterPost.MyViewHolder> {

    Context context;
    ArrayList<Post_Item> postArrayList;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;

    public MyAdapterPost(Context context, ArrayList<Post_Item> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;
    }
    public void setFilteredList(ArrayList<Post_Item> array)
    {
        this.postArrayList=array;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyAdapterPost.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterPost.MyViewHolder holder, int position) {
        Post_Item post_item=postArrayList.get(position);
        holder.book_name.setText(post_item.bookname);
        holder.book_author.setText("by ~ " + post_item.authorname);
        holder.book_genre.setText("Genre : " + post_item.genre);
        holder.book_description.setText(post_item.description);
        holder.book_rating.setText("Rating : " + (int) post_item.rating + "/5");
        holder.city.setText(", "+post_item.city);
        Glide.with(context).load(post_item.imageuri).fitCenter().into(holder.book_coverPic);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        //String ids;
        if (post_item.getPostedby().equals(user.getUid()))
        {
            holder.interested.setVisibility(View.INVISIBLE);
        }
        else {
            fStore.collection("books").document(post_item.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        boolean match=false;
                        List<String> arr=new ArrayList<>();
                        DocumentSnapshot documentSnapshot=task.getResult();
                        arr= (List<String>) documentSnapshot.get("interestedpeople");
                        boolean avl= (boolean) documentSnapshot.get("availability");
                        if(avl) {
                            if (arr != null) {
                                for (String temp : arr) {
                                    if (temp.contains(user.getUid())) {
                                        match = true;
                                        break;
                                    }
                                }
                                if (match) {
                                    holder.interested.setText("Requested");
                                    holder.interested.setBackgroundColor(Color.GREEN);
                                    holder.interested.setClickable(false);
                                }
                            }
                        }
                        else {
                            holder.interested.setText("Not Available");
                            holder.interested.setBackgroundColor(Color.RED);
                            holder.interested.setClickable(false);
                        }
                    }
                }
            });
        }
        /*
        String text=holder.interested.getText().toString();
        if(text.equals("Requested"))
        {
            holder.interested.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fStore.collection("books").document(post_item.getId()).update("interestedpeople",FieldValue.arrayRemove(user.getUid()));
                }
            });
        }*/

        fStore.collection("users").document(post_item.postedby).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (!post_item.getPostedby().equals(user.getUid()))
                    {
                        holder.profileView.setText("by "+documentSnapshot.getString("name"));
                    }
                    else {
                        holder.profileView.setText("by me");
                    }
                }
            }
        });


        holder.interested.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Select your preferences")
                            .setMessage("\nNote: This action is not reversible\nPlease click Cancel if not sure")
                            .setView(layout)
                            .setPositiveButton("In-Person", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    fStore.collection("books").document(post_item.getId()).update("interestedpeople", FieldValue.arrayUnion(user.getUid()));
                                    holder.interested.setText("Requested");
                                    holder.interested.setBackgroundColor(Color.GREEN);
                                    holder.interested.setClickable(false);
                                    Toast.makeText(context, "Request sent Sucessfully", Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton("Postal", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String id = user.getUid();
                                    fStore.collection("users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                String address = documentSnapshot.getString("address");
                                                String res = id + '#' + address;
                                                fStore.collection("books").document(post_item.getId()).update("interestedpeople", FieldValue.arrayUnion(res));
                                                holder.interested.setText("Requested");
                                                holder.interested.setBackgroundColor(Color.GREEN);
                                                holder.interested.setClickable(false);

                                                Toast.makeText(context, "Request sent Sucessfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
        });

        holder.profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, Profile_other_person.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", post_item.postedby);
                //Toast.makeText(context, ""+post_item.postedby, Toast.LENGTH_SHORT).show();
                i.putExtras(bundle);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView book_name,book_author,book_genre,book_description,book_rating,profileView,city;
        ImageView book_coverPic;
        Button interested;
        //Button in_person,postal;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            book_name=(TextView) itemView.findViewById(R.id.book_name);
            book_author=(TextView)itemView.findViewById(R.id.book_author);
            book_genre=(TextView)itemView.findViewById(R.id.book_genre);
            book_rating=(TextView)itemView.findViewById(R.id.book_rating);
            book_description=(TextView)itemView.findViewById(R.id.book_description);
            book_coverPic=(ImageView) itemView.findViewById(R.id.book_coverpic);
            interested=(Button) itemView.findViewById(R.id.book_delete);
            profileView=(TextView)itemView.findViewById(R.id.name_posted_by);
            city=itemView.findViewById(R.id.city_posted_by);
            //in_person=itemView.findViewById(R.id.in_person);
            //postal=itemView.findViewById(R.id.postal);
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
