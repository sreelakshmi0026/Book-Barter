package com.example.bookbarter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewRequests extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<ModelViewRequests> requestorsArray;
    MyAdapterViewRequests myAdapterViewRequests;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String id;
    static public List<String> arr;
    static public List<String> arr1;
    TextView norequests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            id = bundle.getString("id");
        }
        recyclerView=findViewById(R.id.viewRequestsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        fStore=FirebaseFirestore.getInstance();
        requestorsArray=new ArrayList<ModelViewRequests>();
        norequests=findViewById(R.id.norequests);
        arr1=new ArrayList<String>();
        getListPosts();
        myAdapterViewRequests= new MyAdapterViewRequests(ViewRequests.this,requestorsArray,id);
        recyclerView.setAdapter(myAdapterViewRequests);
    }

    private void getListPosts() {
        fStore.collection("books").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document=task.getResult();
                arr= (List<String>) document.get("interestedpeople");
                if(arr==null)
                {
                    norequests.setVisibility(View.VISIBLE);
                    return;
                }
                ModelViewRequests.ArrCheck.clear();
                for(int j=0;j<arr.size();j++)
                {
                    if(arr.get(j).length()>28)
                    {
                        arr1.add(arr.get(j).substring(0,28));
                        ModelViewRequests.ArrCheck.add(arr.get(j).substring(0,28));
                        //ModelViewRequests.idArr.add(arr.get(j).substring(0,28));
                    }
                    else{
                        arr1.add(arr.get(j));
                        //ModelViewRequests.idArr.add(arr.get(j));
                    }
                }
                //Toast.makeText(ViewRequests.this, ""+arr1, Toast.LENGTH_SHORT).show();

                /*for(String temp:arr1)
                {
                    DocumentSnapshot documentSnapshot= fStore.collection("users").document(temp).get().getResult();
                    requestorsArray.add(documentSnapshot.toObject(ModelViewRequests.class));
                    myAdapterViewRequests.notifyDataSetChanged();
                }*/
                fStore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value != null) {
                            for (DocumentChange dc : value.getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    if (arr1.contains(dc.getDocument().getId())) {
                                        ModelViewRequests.idArr.add(dc.getDocument().getId());
                                        requestorsArray.add(dc.getDocument().toObject(ModelViewRequests.class));
                                    }
                                }
                                myAdapterViewRequests.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }
        });
    }
}