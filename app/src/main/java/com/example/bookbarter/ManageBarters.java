package com.example.bookbarter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageBarters extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<ModelManageBarter> postItemsArray;
    MyAdapterManageBarters myAdapterManageBarters;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser user;
    static public List<String> posts;
    TextView noPosts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_barters);
        recyclerView=findViewById(R.id.managebarters_recyclerView);
        noPosts=findViewById(R.id.no_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        fStore=FirebaseFirestore.getInstance();
        posts=new ArrayList<String>();
        postItemsArray=new ArrayList<ModelManageBarter>();
        getListPosts();
        myAdapterManageBarters= new MyAdapterManageBarters(ManageBarters.this,postItemsArray);
        recyclerView.setAdapter(myAdapterManageBarters);
    }

    private void getListPosts() {
        fStore.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document=task.getResult();
                posts= (List<String>) document.get("booksposted");
                if(posts==null)
                {
                    noPosts.setVisibility(View.VISIBLE);
                    //Toast.makeText(ManageBarters.this, "Empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    fStore.collection("books").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value != null) {
                                for (DocumentChange dc : value.getDocumentChanges()) {
                                    if (dc.getType() == DocumentChange.Type.ADDED) {
                                        if (posts.contains(dc.getDocument().getId())) {
                                            postItemsArray.add(dc.getDocument().toObject(ModelManageBarter.class));
                                        }
                                    }
                                    myAdapterManageBarters.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
            }
        });

    }
}