package com.example.bookbarter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DisplayContext;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class profile extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    ImageView profilepic;
    Uri ImageUri;
    StorageReference storageReference;
    TextView name,email,address,phonenumber,managebarters,update_wishlist,upload_image,editphonenumber,communityrating,no_of_users_rated;
    Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        fStore=FirebaseFirestore.getInstance();
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        upload_image=findViewById(R.id.profile_upload_pic);
        profilepic=findViewById(R.id.profile_pic);
        name=findViewById(R.id.profile_name);
        email=findViewById(R.id.profile_email);
        phonenumber=findViewById(R.id.profile_phonenumber);
        managebarters=findViewById(R.id.profile_manage_barters);
        update_wishlist=findViewById(R.id.profile_wishlist);
        address=findViewById(R.id.profile_address);
        editphonenumber=findViewById(R.id.profile_edit_phonenumber);
        communityrating=findViewById(R.id.community_rating);
        no_of_users_rated=findViewById(R.id.users_rated_count);
        logout=findViewById(R.id.profile_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                Intent intent=new Intent(profile.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(profile.this, "Successfully Logged out", Toast.LENGTH_SHORT).show();
            }
        });
        editphonenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText=new EditText(profile.this);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setPadding(20,0,20,0);
                editText.setHint("Please enter new Phone number");
                editText.setBackgroundResource(R.drawable.textfieldbox);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(30,0,30,0);
                editText.setLayoutParams(lp);
                AlertDialog.Builder builder = new AlertDialog.Builder(profile.this);
                builder.setTitle("Update Phone Number")
                        .setView(editText).setMessage("\n")
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String updatednumber=editText.getText().toString().trim();
                                fStore.collection("users").document(user.getUid()).update("phonenumber",updatednumber);
                                Toast.makeText(profile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
        update_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),WishListSelection.class));
            }
        });
        managebarters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ManageBarters.class));
            }
        });
        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
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
        fStore.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        DocumentSnapshot documentSnapshot=task.getResult();
                        name.setText(documentSnapshot.getString("name"));
                        email.setText(documentSnapshot.getString("email"));
                        phonenumber.setText(documentSnapshot.getString("phonenumber"));
                        address.setText(documentSnapshot.getString("address"));
                        String com_rating=documentSnapshot.getString("communityrating");
                        String temp=com_rating.substring(0,3)+"/5";
                        communityrating.setText(temp);
                        String count=documentSnapshot.getString("noofusersrated");
                        String temp1="("+count+") ratings";
                        no_of_users_rated.setText(temp1);
                        if(documentSnapshot.getString("profilepic").length()>0)
                        {
                            Glide.with(getApplicationContext()).load(documentSnapshot.getString("profilepic")).circleCrop().into(profilepic);
                        }
                    }
            }
        });
    }
    private void selectImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }
    private void uploadImage() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference= FirebaseStorage.getInstance().getReference("images/"+fileName);
        storageReference.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        fStore.collection("users").document(user.getUid()).update("profilepic",uri);
                    }
                });
                Toast.makeText(profile.this,"Uploaded Successfully",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(profile.this,"Something Went Wrong!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && data!=null && data.getData()!=null)
        {
            ImageUri=data.getData();
            profilepic.setImageURI(ImageUri);
            uploadImage();
        }
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}