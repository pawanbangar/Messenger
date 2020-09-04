package com.mrserviceman.messenger.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.mrserviceman.messenger.Adapter.MessageAdapter;
import com.mrserviceman.messenger.Model.ChatFragmentsModels.Chat;
import com.mrserviceman.messenger.Model.ChatFragmentsModels.User;
import com.mrserviceman.messenger.Notification.ApiService;
import com.mrserviceman.messenger.Notification.Client;
import com.mrserviceman.messenger.Notification.Data;
import com.mrserviceman.messenger.Notification.Response;
import com.mrserviceman.messenger.Notification.Sender;
import com.mrserviceman.messenger.Notification.Token;
import com.mrserviceman.messenger.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class Message extends AppCompatActivity {

    //Top Content
    private CircleImageView profile_img;
    private ImageButton back_button;
    private TextView user;

    //Bottom Content
    private EditText text_to_send;
    private Button send_button;

    //FireBase Content
    private FirebaseUser fUser;
    private DatabaseReference reference;
    Intent intent;

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Chat> mChat;

    ApiService apiService;
    boolean notify=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //setting up Views
        profile_img=findViewById(R.id.profile_image);
        back_button=findViewById(R.id.back_button);
        user=(TextView)findViewById(R.id.user);
//        add=(Button)findViewById(R.id.add_button);
//        remind=(Button)findViewById(R.id.remind);
        text_to_send=(EditText)findViewById(R.id.content_to_send);
        send_button=(Button)findViewById(R.id.send_button);
        recyclerView=findViewById(R.id.message_block);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        apiService= Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
        intent=getIntent();
        final String userid=intent.getStringExtra("UserId");
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        mChat=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user1=dataSnapshot.getValue(User.class);
                user.setText(user1.getUserName());
                if(!user1.getImageUrl().equals("default"))
                {
                    Picasso.get().load(user1.getImageUrl()).into(profile_img);
                }
                readMessage(fUser.getUid(),userid,user1.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg=text_to_send.getText().toString();
                if(!msg.equals(""))
                {
                    notify=true;
                    sendMessage(fUser.getUid(),userid,msg);
                    text_to_send.setText("");
                }

            }
        });

    }
    private void sendMessage(String sender, final String receiver, final String message)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Chats");
        final HashMap<String,String> data=new HashMap<>();
        data.put("sender",sender);
        data.put("receiver",receiver);
        data.put("message",message);
        ref.push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

        final String msg=message;

            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(sender);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user=dataSnapshot.getValue(User.class);
                    if(notify){
                        sendNotification(receiver,user.getUserName(),msg);
                    }
                    notify=false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    private void sendNotification(final String receiver, final String userName, final String message) {

        DatabaseReference allTokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=allTokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Token token=ds.getValue(Token.class);
                    Data data=new Data(receiver,userName+":"+message,receiver,"New Message",R.mipmap.ic_launcher_round);
                    Sender sender=new Sender(data,token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage(final String myId, final String userid,String message)
    {
        mChat=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    Chat chat=dataSnapshot1.getValue(Chat.class);
                    try{
                        if(chat.getReceiver().equals(myId)&&chat.getSender().equals(userid)||
                                chat.getReceiver().equals(userid)&&chat.getSender().equals(myId))
                        {
                            mChat.add(chat);

                        }
                    }
                    catch (NullPointerException ex)
                    {
                        Toast.makeText(getApplicationContext(),"Error Occured",Toast.LENGTH_SHORT).show();
                    }
                    adapter=new MessageAdapter(getApplicationContext(),mChat);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
     //  startActivity(new Intent(Message.this,MainActivity.class));
    }
}
