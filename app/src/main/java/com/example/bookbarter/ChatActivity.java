package com.example.bookbarter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    String otheruserid,otherusername,otheruserphonenumber;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    String chatroomId;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    ImageView profilepic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        otheruserid = bundle.getString("id");
        otherusername=bundle.getString("name");
        otheruserphonenumber=bundle.getString("phonenumer");
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        fAuth= FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();
        fStore=FirebaseFirestore.getInstance();
        profilepic=findViewById(R.id.profile_pic_image_view);
        fStore.collection("users").document(otheruserid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot=task.getResult();
                    String urlpic=documentSnapshot.getString("profilepic");
                    if(!urlpic.isEmpty())
                    {
                        Glide.with(getApplicationContext()).load(documentSnapshot.getString("profilepic")).circleCrop().into(profilepic);
                    }
                }
            }
        });
        backBtn.setOnClickListener((v)->{
            onBackPressed();
        });
        otherUsername.setText(otherusername);
        chatroomId=getChatRoomId(user.getUid(),otheruserid);
        getOrCreateChatroomModel();
        setupChatRecyclerView();
        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        }));

    }

    private void setupChatRecyclerView() {
        Query query = FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId).collection("chats")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMessageToUser(String message) {
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(user.getUid());
        chatroomModel.setLastMessage(message);
        fStore.collection("chatrooms").document(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,user.getUid(),Timestamp.now());
        FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId).collection("chats").add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
                        }
                    }
                });
    }

    private String getChatRoomId(String userId1, String userId2) {
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }

    private void getOrCreateChatroomModel() {
        fStore.collection("chatrooms").document(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                chatroomModel=task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(user.getUid(),otheruserid),
                            Timestamp.now(),
                            "",
                            ""
                    );
                    fStore.collection("chatrooms").document(chatroomId).set(chatroomModel);
                }
            }
        });
    }
}