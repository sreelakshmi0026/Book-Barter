package com.example.bookbarter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchUserActivity extends AppCompatActivity {
    ImageButton backButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;
    SearchView searchView;
    ArrayList<UserModel> userList;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        backButton = findViewById(R.id.back_btn);
        searchView=findViewById(R.id.searchViewChat);
        recyclerView = findViewById(R.id.search_user_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return true;
            }
        });
        db=FirebaseFirestore.getInstance();
        userList=new ArrayList<UserModel>();
        adapter=new SearchUserRecyclerAdapter(SearchUserActivity.this,userList);
        recyclerView.setAdapter(adapter);
        EventChangeListner();



        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    private void filterList(String text) {
        ArrayList<UserModel> listsearch=new ArrayList<UserModel>();
        for (UserModel item:userList)
        {
            if(item.getName().toLowerCase().contains(text.toLowerCase())){
                listsearch.add(item);
            }
        }
        if(listsearch.isEmpty())
        {
            Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
        }
        else {
            adapter.setFilteredList(listsearch);
        }
    }

    private void EventChangeListner() {
        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            userList.add(dc.getDocument().toObject(UserModel.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    /*private void setupSearchRecyclerView(String searchTerm) {
        Query query=FirebaseFirestore.getInstance().collection("users").whereGreaterThanOrEqualTo("name",searchTerm);
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();
        adapter = new SearchUserRecyclerAdapter(options,getApplicationContext());
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
            adapter.startListening();
    }*/
}