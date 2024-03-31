package com.example.bookbarter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Post_Item> postItemsList;
    MyAdapterPost myAdapterPost;
    FirebaseFirestore db;
    SearchView searchView;
    FirebaseAuth fAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recyclerViewPost);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        searchView=findViewById(R.id.searchView);
        searchView.clearFocus();
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
        db=FirebaseFirestore.getInstance();
        postItemsList=new ArrayList<Post_Item>();
        myAdapterPost=new MyAdapterPost(MainActivity.this,postItemsList);
        recyclerView.setAdapter(myAdapterPost);
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.home)
                {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if(item.getItemId()==R.id.post)
                {
                    startActivity(new Intent(getApplicationContext(),post.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if(item.getItemId()==R.id.chat)
                {
                    startActivity(new Intent(getApplicationContext(),MainChat.class));
                    overridePendingTransition(0,0);
                    return true;
                }
                else if(item.getItemId()==R.id.profile)
                {
                    startActivity(new Intent(getApplicationContext(),profile.class));
                    overridePendingTransition(0,0);
                    return true;
                }

                return false;
            }
        });
        EventChangeListner();
        getFCMToken();
    }


    private void filterList(String text) {
        ArrayList<Post_Item> listsearch=new ArrayList<Post_Item>();
        HashSet<Post_Item> hashSet = new HashSet<>();
        for (Post_Item item:postItemsList)
        {
            if(item.getBookname().toLowerCase().contains(text.toLowerCase())){
                hashSet.add(item);
            }
            if(item.getAuthorname().toLowerCase().contains(text.toLowerCase())){
                hashSet.add(item);
            }
            if(item.getCity().toLowerCase().contains(text.toLowerCase())){
                hashSet.add(item);
            }
            if(item.getGenre().toLowerCase().contains(text.toLowerCase())){
                hashSet.add(item);
            }
        }
        listsearch.addAll(hashSet);
        if(hashSet.isEmpty())
        {
            Toast.makeText(this, "Search result not found", Toast.LENGTH_SHORT).show();
        }
        else {
            myAdapterPost.setFilteredList(listsearch);
        }
    }

    private void EventChangeListner() {
        db.collection("books").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                 if(value != null){
                for(DocumentChange dc:value.getDocumentChanges())
                {
                    if(dc.getType()==DocumentChange.Type.ADDED)
                    {
                        postItemsList.add(dc.getDocument().toObject(Post_Item.class));
                    }
                    myAdapterPost.notifyDataSetChanged();
                }}
            }
        });
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                db.collection("users").document(user.getUid()).update("fcmtoken",token);
            }
        });
    }
}