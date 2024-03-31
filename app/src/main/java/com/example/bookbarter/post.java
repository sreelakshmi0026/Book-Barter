package com.example.bookbarter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class post extends AppCompatActivity {
    Spinner spinner;
    EditText bookname,authorname,description;
    Button selectpic,submit;
    Uri ImageUri;
    ImageView coverpic;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String book_id;

    RatingBar ratingBar;
    String city;
    double lattitude,longitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    public final static int REQUEST_CODE=100;
    FirebaseUser user;
    String genre;
    String bookName;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        spinner=findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.Genre, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        bookname=findViewById(R.id.bookname);
        authorname=findViewById(R.id.authorname);
        description=findViewById(R.id.description);
        selectpic=findViewById(R.id.selectpicbutton);
        submit=findViewById(R.id.submitpost);
        coverpic=findViewById(R.id.coverpic);
        fAuth= FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        ratingBar=findViewById(R.id.ratingBar);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        //progressBar=findViewById(R.id.progressBar);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.post);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.home)
                {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    return true;
                }
                else if(item.getItemId()==R.id.post)
                {
                    startActivity(new Intent(getApplicationContext(),post.class));
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


        selectpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });
    }

    private boolean getLastLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null)
                    {
                        Geocoder geocoder=new Geocoder(post.this,Locale.getDefault());
                        try {
                            //progressBar.setVisibility(View.VISIBLE);
                            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            lattitude=addresses.get(0).getLatitude();
                            longitude=addresses.get(0).getLongitude();
                            city=addresses.get(0).getLocality();
                            bookName=bookname.getText().toString().trim();
                            String authorName=authorname.getText().toString().trim();
                            String des=description.getText().toString().trim();
                            genre=spinner.getSelectedItem().toString();
                            float rating= (float) ratingBar.getRating();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
                            Date now = new Date();
                            book_id = formatter.format(now);
                            DocumentReference documentReference=fStore.collection("books").document(book_id);
                            Map<String,Object> book_item=new HashMap<>();
                            if (TextUtils.isEmpty(bookName))
                            {
                                bookname.setError("Book Name is Required");
                                return;
                            }
                            if (TextUtils.isEmpty(authorName))
                            {
                                authorname.setError("Author Name is Required");
                                return;
                            }
                            if (TextUtils.isEmpty(des))
                            {
                                description.setError("description is Required");
                                return;
                            }
                            if(genre.equals("Select Genre") || TextUtils.isEmpty(genre))
                            {
                                Toast.makeText(post.this, "Please select genre", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            book_item.put("bookname",bookName);
                            book_item.put("authorname",authorName);
                            book_item.put("description",des);
                            book_item.put("rating",rating);
                            book_item.put("genre",genre);
                            book_item.put("city",city);
                            book_item.put("longitude",longitude);
                            book_item.put("latitude",lattitude);
                            book_item.put("id",book_id);
                            book_item.put("availability",true);
                            if(ImageUri==null)
                            {
                                Toast.makeText(post.this, "Please select Image", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(rating==0)
                            {
                                Toast.makeText(post.this, "Please provide rating", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            documentReference.set(book_item).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(post.this,"Something Went Wrong!",Toast.LENGTH_SHORT).show();
                                }
                            });
                            fStore.collection("users").document(user.getUid()).update("booksposted", FieldValue.arrayUnion(book_id));
                            fStore.collection("books").document(book_id).update("postedby",user.getUid());
                            uploadImage();
                            sendNotification();
                            clearForm();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            });
        }
        else{
            askPermission();
        }
        return true;
    }

    private void clearForm() {
        bookname.getText().clear();
        authorname.getText().clear();
        spinner.setSelection(0);
        description.getText().clear();
        ratingBar.setRating(0);
        coverpic.setImageResource(R.drawable.baseline_crop_original_24);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //progressBar.setVisibility(View.INVISIBLE);
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
                        fStore.collection("books").document(book_id).update("imageuri",uri);
                    }
                });
                Toast.makeText(post.this,"Uploaded Successfully",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(post.this,"Something Went Wrong!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && data!=null && data.getData()!=null)
        {
            ImageUri=data.getData();
            coverpic.setImageURI(ImageUri);
        }
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
    void sendNotification(){

                try{

                    JSONObject jsonObject  = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title",genre);
                    notificationObj.put("body",bookName+" book is available -> grab it Quickly!");

                    jsonObject.put("notification",notificationObj);
                    String link="/topics/"+genre;
                    jsonObject.put("to",link);

                    callApi(jsonObject);


                }catch (Exception e){

                }


    }

    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAA2BIVOIU:APA91bFwjBRhDJGdg9ZiSkV9hskwdCACwr3naEE1L7JXaCo1LplXhOaCvocp2Q8JkpKInF1T_sgOTh5s0wmEWizg6bTb3lPjYiuep_LNfQEc_Nb8w8V9cBaZq_szP3kTGMzcy5NRQEKZ")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
        //startActivity(new Intent(post.this,MainActivity.class));
    }
}