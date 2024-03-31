package com.example.bookbarter;

import static com.example.bookbarter.post.REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText mName,mMail,mPassword,mRePassword,mPhoneNumber;
    Button mRegister;
    TextView mLoginButton;
    String userID;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ArrayList<String> arr;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mName=findViewById(R.id.name);
        mMail=findViewById(R.id.email);
        mPassword=findViewById(R.id.enter_password);
        mRePassword=findViewById(R.id.re_enter_password);

        mRegister=findViewById(R.id.registerbutton);
        mLoginButton=findViewById(R.id.LoginButton);
        mPhoneNumber=findViewById(R.id.PhoneNumber);
        fAuth= FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(Register.this);

        if (fAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });


    }
    private boolean getLastLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(Register.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            String city = addresses.get(0).getLocality();
                            String addressLine=addresses.get(0).getAddressLine(0);
                            String  country=addresses.get(0).getCountryName();
                            String postalcode=addresses.get(0).getPostalCode();
                            String Address=addressLine;
                            String email=mMail.getText().toString().trim();
                            String password=mPassword.getText().toString().trim();
                            String re_password=mRePassword.getText().toString().trim();
                            String phonenumber=mPhoneNumber.getText().toString().trim();
                            String name=mName.getText().toString().trim();
                            if (TextUtils.isEmpty(email))
                            {
                                mMail.setError("E-Mail is Required");
                                return;
                            }
                            if (TextUtils.isEmpty(password))
                            {
                                mPassword.setError("Password is Required");
                                return;
                            }
                            if (TextUtils.isEmpty(re_password))
                            {
                                mRePassword.setError("This field is Required");
                                return;
                            }
                            if (TextUtils.equals(password,re_password))
                            {
                            }
                            else {
                                mRePassword.setError("Password should match");
                            }
                            if (password.length()<6)
                            {
                                mPassword.setError("Password must contain at least 6 Characters");
                                return;
                            }
                            //register user in firebase
                            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        //send verification email
                                        user=fAuth.getCurrentUser();
                                        if(user != null) {
                                            userID = user.getUid();
                                        }
                                        DocumentReference documentReference=fStore.collection("users").document(userID);
                                        Map<String,Object> user_item=new HashMap<>();
                                        user_item.put("name",name);
                                        user_item.put("email",email);
                                        user_item.put("phonenumber",phonenumber);
                                        user_item.put("wishlist",arr);
                                        user_item.put("booksposted",arr);
                                        user_item.put("address",Address);
                                        user_item.put("profilepic","");
                                        user_item.put("city",addresses.get(0).getLocality());
                                        user_item.put("communityrating","0.0");
                                        user_item.put("noofusersrated","0");
                                        user_item.put("usersrated",arr);
                                        user_item.put("userid",user.getUid());
                                        user_item.put("firsttimelogin",true);
                                        documentReference.set(user_item).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Register.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                getFCMToken();
                                                Toast.makeText(Register.this, "Verification E-mail has been Sent", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Register.this, "OOPS! Something went wrong!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        Toast.makeText(Register.this,"Successfully Registered",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), Login.class));
                                    }
                                    else{
                                        Toast.makeText(Register.this,"Something Went Wrong! Please Enter Details Correctly",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            });
        }else{
            askPermission();
        }
        return true;
    }
    private void askPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getLastLocation();
            }
            else {
                Toast.makeText(this,"Required Permission",Toast.LENGTH_SHORT).show();
            }
        }



        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                fStore.collection("users").document(user.getUid()).update("fcmtoken",token);
            }
        });
    }
}