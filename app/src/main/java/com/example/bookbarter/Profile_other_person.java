package com.example.bookbarter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class Profile_other_person extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    ImageView profilepic_other;
    TextView name_other,email_other,address_other,phonenumber_other,communityrating_other,no_of_users_rated_other;
    String id;
    RatingBar ratingBar_other;
    Button submit_rating;
    float rating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_other_person);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            id = bundle.getString("id");
        }
        //Toast.makeText(this, ""+id, Toast.LENGTH_SHORT).show();
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        fStore=FirebaseFirestore.getInstance();
        profilepic_other=findViewById(R.id.profile_other_pic);
        name_other=findViewById(R.id.profile_other_name);
        email_other=findViewById(R.id.profile_other_email);
        phonenumber_other=findViewById(R.id.profile_other_phonenumber);
        ratingBar_other=findViewById(R.id.rating_other);
        submit_rating=findViewById(R.id.submit_other);
        communityrating_other=findViewById(R.id.community_rating_other);
        no_of_users_rated_other=findViewById(R.id.users_rated_count_other);
        address_other=findViewById(R.id.profile_other_address);
        if(id.equals(user.getUid()))
        {
            ratingBar_other.setVisibility(View.INVISIBLE);
            submit_rating.setVisibility(View.INVISIBLE);
        }
        fStore.collection("users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.get("usersrated")==null)
                    {
                        ratingSubmit();
                    }
                    else {
                        List<String> lst=(List<String>) documentSnapshot.get("usersrated");
                        if(lst.contains(user.getUid()))
                        {
                            ratingBar_other.setVisibility(View.INVISIBLE);
                            submit_rating.setVisibility(View.INVISIBLE);
                        }
                        else {
                            ratingSubmit();
                        }
                    }
                }
            }
        });
        fStore.collection("users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot=task.getResult();
                    name_other.setText(documentSnapshot.getString("name"));
                    email_other.setText(documentSnapshot.getString("email"));
                    StringBuilder myName = new StringBuilder(documentSnapshot.getString("phonenumber"));
                    myName.setCharAt(3, '*');
                    myName.setCharAt(7, '*');
                    myName.setCharAt(6, '*');
                    //Toast.makeText(Profile_other_person.this, ""+myName, Toast.LENGTH_SHORT).show();
                    phonenumber_other.setText(myName);
                    String com_rating=documentSnapshot.getString("communityrating");
                    String temp=com_rating.substring(0,3)+"/5";
                    communityrating_other.setText(temp);
                    String count=documentSnapshot.getString("noofusersrated");
                    String temp1="("+count+") ratings";
                    no_of_users_rated_other.setText(temp1);
                    address_other.setText(documentSnapshot.getString("city"));
                    if(documentSnapshot.getString("profilepic").length()>0)
                    {
                        Glide.with(getApplicationContext()).load(documentSnapshot.getString("profilepic")).circleCrop().into(profilepic_other);
                    }
                }
            }
        });
    }

    private void ratingSubmit() {

                    submit_rating.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fStore.collection("users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        rating = (float) ratingBar_other.getRating();
                                        String rateS = documentSnapshot.getString("communityrating");
                                        //Toast.makeText(Profile_other_person.this, "" + rateS, Toast.LENGTH_SHORT).show();
                                        float rate = Float.parseFloat(rateS);
                                        String no_of_pplS = documentSnapshot.getString("noofusersrated");
                                        int no_of_ppl = Integer.parseInt(no_of_pplS);
                                        float rate_updated = (float) ((rate + rating) / (no_of_ppl + 1));

                                        fStore.collection("users").document(id).update("noofusersrated", Integer.toString(no_of_ppl + 1));
                                        fStore.collection("users").document(id).update("communityrating", Float.toString(rate_updated));
                                        fStore.collection("users").document(id).update("usersrated", FieldValue.arrayUnion(user.getUid()));
                                        submit_rating.setVisibility(View.INVISIBLE);
                                        ratingBar_other.setVisibility(View.INVISIBLE);
                                        Toast.makeText(Profile_other_person.this, "Rating Submitted", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
            }
        });
    }
}