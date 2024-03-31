package com.example.bookbarter;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Login extends AppCompatActivity {
    EditText mMail,mPassword;
    Button mLogin;
    TextView mRegister,mPasswordForget;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mMail=findViewById(R.id.email);
        mPassword=findViewById(R.id.enter_password);
        mLogin=findViewById(R.id.LoginButton);
        mRegister=findViewById(R.id.textRegister);
        mPasswordForget=findViewById(R.id.password_forget);
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        fStore=FirebaseFirestore.getInstance();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mMail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();
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
                if (password.length()<6)
                {
                    mPassword.setError("Password must contain at least 6 Characters");
                    return;
                }
                //authenticate the user
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            user=fAuth.getCurrentUser();
                            if (!user.isEmailVerified()){
                                Toast.makeText(Login.this,"Please verify email to continue!",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(Login.this,"Logged In Successfully",Toast.LENGTH_SHORT).show();
                            fStore.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot documentSnapshot=task.getResult();
                                        Boolean val=documentSnapshot.getBoolean("firsttimelogin");
                                        if(val)
                                        {
                                            startActivity(new Intent(getApplicationContext(), WishListSelection.class));
                                        }
                                        else {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        }
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(Login.this,"Entered Email or Password doesn't match or exist in system",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        mPasswordForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),PasswordReset.class));
            }
        });
    }
}