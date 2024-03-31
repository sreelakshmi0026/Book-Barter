package com.example.bookbarter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainChat extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    ChatFragment chatFragment;
    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        chatFragment=new ChatFragment();
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        searchButton=findViewById(R.id.main_search_btn);
        searchButton.setOnClickListener((v)->{
            startActivity(new Intent(MainChat.this,SearchUserActivity.class));
        });
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        fStore=FirebaseFirestore.getInstance();

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.chat);

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
        recyclerView=findViewById(R.id.recycler_view_chat);
        setupRecyclerView();
    }
    void setupRecyclerView(){
        Query query=FirebaseFirestore.getInstance().collection("chatrooms").whereArrayContains("userIds",user.getUid())
                .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query,ChatroomModel.class).build();

        adapter = new RecentChatRecyclerAdapter(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}