package com.example.bookbarter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class WishListSelection extends AppCompatActivity {
    RecyclerView mRecyclerView;
    List<String> titles;
    List<Integer> mimages;
    Button save;
    List<ModelWishList> modelWishLists;
    MyAdapterWishList myAdapterWishList;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list_selection);

        mRecyclerView=findViewById(R.id.recyclerView);
        save=findViewById(R.id.button2);
        titles= new ArrayList<>();
        mimages=new ArrayList<>();
        modelWishLists=new ArrayList<>();
        modelWishLists.add(new ModelWishList("Sci-Fi",R.drawable.scifi));
        modelWishLists.add(new ModelWishList("Comedy",R.drawable.comedy));
        modelWishLists.add(new ModelWishList("Thriller",R.drawable.thriller));
        modelWishLists.add(new ModelWishList("Detective",R.drawable.detective));
        modelWishLists.add(new ModelWishList("Romantic",R.drawable.romantic));
        modelWishLists.add(new ModelWishList("Horror",R.drawable.horror));
        modelWishLists.add(new ModelWishList("History",R.drawable.history));
        modelWishLists.add(new ModelWishList("Comic",R.drawable.comic));
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        fStore=FirebaseFirestore.getInstance();

        myAdapterWishList=new MyAdapterWishList(WishListSelection.this,modelWishLists);

        GridLayoutManager gridLayoutManager=new GridLayoutManager(WishListSelection.this, 2,GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(myAdapterWishList);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    fStore.collection("users").document(user.getUid()).update("firsttimelogin",false);
                    startActivity(new Intent(getApplicationContext(),profile.class));
            }
        });
    }
}