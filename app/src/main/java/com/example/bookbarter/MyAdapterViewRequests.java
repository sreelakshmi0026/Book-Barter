package com.example.bookbarter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyAdapterViewRequests extends RecyclerView.Adapter<MyAdapterViewRequests.MyViewHolder>{
    Context context;
    ArrayList<ModelViewRequests> requestArray;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String id;
    public MyAdapterViewRequests(Context context, ArrayList<ModelViewRequests> requestArray,String id) {
        this.context = context;
        this.requestArray = requestArray;
        this.id=id;
    }
    @NonNull
    @Override
    public MyAdapterViewRequests.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.viewrequests_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterViewRequests.MyViewHolder holder, int position) {
        //holder.setIsRecyclable(false);
        ModelViewRequests item=requestArray.get(position);
        holder.requester_name.setText(item.name);
        if(ModelViewRequests.ArrCheck.contains(ModelViewRequests.idArr.get(position)))
        {
            holder.request_type.setText("Request Type : "+"Postal/Fed-Ex");
            holder.address.setText("Address : "+item.address);
        }
        else{
            holder.request_type.setText("Request Type : "+"In-Person");
            holder.address.setVisibility(View.INVISIBLE);
        }
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        fAuth= FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        fStore.collection("books").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    final Boolean[] availability1 = new Boolean[1];
                    DocumentSnapshot documentSnapshot=task.getResult();
                    availability1[0] =(Boolean) documentSnapshot.getBoolean("availability");
                    if(!availability1[0])
                    {
                        String givento=documentSnapshot.getString("bookgivento");
                        if(ModelViewRequests.idArr.get(position).equals(givento))
                        {
                            holder.accept.setText("Accepted");
                            holder.accept.setBackgroundColor(Color.GREEN);
                        }
                        else {
                            holder.accept.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                fStore.collection("users").document(ModelViewRequests.idArr.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot documentSnapshot= task.getResult();
                            String name=documentSnapshot.getString("name");
                            String msg="\nThis book is given to "+name;
                                    builder.setTitle("Are you sure?")
                                            .setMessage(msg+"\nNote: This action is not reversible\nPlease click Cancel if not sure")
                                            .setView(layout)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    bookAccept(holder,position);
                                                }
                                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).show();
                        }
                    }
                });
            }
        });


    }

    private void bookAccept(MyAdapterViewRequests.MyViewHolder holder,int position) {
        final Boolean[] availability = new Boolean[1];
        fStore.collection("books").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot=task.getResult();
                availability[0] =(Boolean) snapshot.getBoolean("availability");
                if(availability[0])
                {
                    fStore.collection("books").document(id).update("availability",false);
                    fStore.collection("books").document(id).update("bookgivento",ModelViewRequests.idArr.get(position));
                    holder.accept.setText("Accepted");
                    ModelViewRequests.userid=ModelViewRequests.idArr.get(position);
                    holder.accept.setBackgroundColor(Color.GREEN);
                    holder.accept.setVisibility(View.VISIBLE);
                    sendNotification(ModelViewRequests.idArr.get(position),id);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Accepted and Notified Successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Book is Already Given to a User", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestArray.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView requester_name,request_type,address;
        Button accept,decline;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            requester_name=itemView.findViewById(R.id.requester_name);
            request_type=itemView.findViewById(R.id.request_type);
            address=itemView.findViewById(R.id.requester_address);
            accept=itemView.findViewById(R.id.accept);
            //decline=itemView.findViewById(R.id.decline);
        }
    }
    void sendNotification(String user_id,String book_id){
        fStore.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot=task.getResult();
                    String barter_name=documentSnapshot.getString("name");
                    fStore.collection("books").document(book_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                DocumentSnapshot documentSnapshot1=task.getResult();
                                String book_name=documentSnapshot1.getString("bookname");
                                fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful())
                                        {
                                            DocumentSnapshot documentSnapshot2=task.getResult();
                                            String token=documentSnapshot2.getString("fcmtoken");
                                            try{
                                                //Toast.makeText(this, "hiiii", Toast.LENGTH_SHORT).show();
                                                JSONObject jsonObject  = new JSONObject();

                                                JSONObject notificationObj = new JSONObject();
                                                notificationObj.put("title",barter_name+" Accepted your Request");
                                                notificationObj.put("body","You can avail "+book_name+" book now");

                                                jsonObject.put("notification",notificationObj);
                                                jsonObject.put("to",token);
                                                callApi(jsonObject);


                                            }catch (Exception e){

                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
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
        //Toast.makeText(context, "Ha ha", Toast.LENGTH_SHORT).show();
    }
}
