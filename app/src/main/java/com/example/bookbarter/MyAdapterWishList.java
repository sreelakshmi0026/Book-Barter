package com.example.bookbarter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class MyAdapterWishList extends RecyclerView.Adapter<MyAdapterWishList.MyViewHolder> {
    Context context;

    List<ModelWishList> modelWishLists;
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    FirebaseUser user=fAuth.getCurrentUser();
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    String userID=user.getUid();
    DocumentReference documentReference=fStore.collection("users").document(userID);
    public MyAdapterWishList(Context context,List<ModelWishList> modelWishLists)
    {
        this.context=context;
        this.modelWishLists=modelWishLists;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.wishlist_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelWishList model=modelWishLists.get(position);
        holder.mTextView.setText(model.getTitle());
        holder.mImageView.setImageResource(model.getImage());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.get("wishlist")!=null)
                    {
                        List<String> lst= (List<String>) documentSnapshot.get("wishlist");
                        if(lst.contains(model.getTitle()))
                        {
                            ModelWishList.count++;
                            //Toast.makeText(context, ""+ModelWishList.count, Toast.LENGTH_SHORT).show();
                            holder.relativeLayout.setBackgroundColor(Color.GREEN);
                            model.setSelected(!model.isSelected);
                        }
                    }
                }
            }
        });
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setSelected(!model.isSelected);
                if(model.isSelected) {
                    documentReference.update("wishlist", FieldValue.arrayUnion(model.getTitle()));
                    ModelWishList.count++;
                    subscribeToGenre(model.getTitle());
                    //subscribeToGenreCollection(model.getTitle());
                }
                else {
                    documentReference.update("wishlist",FieldValue.arrayRemove(model.getTitle()));
                    ModelWishList.count--;
                    unsubscribeToGenre(model.getTitle());
                }
                holder.relativeLayout.setBackgroundColor(model.isSelected?Color.GREEN:Color.WHITE);
            }
        });
    }

    private void unsubscribeToGenre(String genre) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(genre).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context.getApplicationContext(), "UnSubscribed "+genre, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subscribeToGenre(String genre) {
            FirebaseMessaging.getInstance().subscribeToTopic(genre).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context.getApplicationContext(), "Subscribed "+ genre, Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public int getItemCount() {
        return modelWishLists.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView mImageView;
        TextView mTextView;
        RelativeLayout relativeLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView=itemView.findViewById(R.id.imageView);
            mTextView= (TextView) itemView.findViewById(R.id.titletextview);
            relativeLayout= itemView.findViewById(R.id.itemLayout);
        }
    }

}
