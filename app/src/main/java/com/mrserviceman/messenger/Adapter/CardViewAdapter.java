package com.mrserviceman.messenger.Adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mrserviceman.messenger.Model.ChatFragmentsModels.Chat;
import com.mrserviceman.messenger.Model.ChatFragmentsModels.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mrserviceman.messenger.R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder>{
    public static final int CONV = 0;
    private static final int USER = 1;
//    public static User USER_OWN;
    public static int CONV_INFLATE;
    public static int NEW_FR;
    private DatabaseReference reference;
    private UserAdapter userAdapter;
    private List<User> users;
    private Context mContext;
    List<Integer> list;
    private List<User> userList;
    private List<String> chat_list;
    private DatabaseReference chat_ref;
    private FirebaseUser firebaseUser;
    private UserAdapter convAdapter;
    private Set<String> userset;
    public static List<Chat> last_messageList;
    public CardViewAdapter(Context context, List<Integer> list) {
        mContext = context;
        this.list = list;
    }


    @Override
    public CardViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (i == CONV&&CONV_INFLATE==1) {
            view = LayoutInflater.from(mContext).inflate(R.layout.card_item_conversation, viewGroup, false);
            return new CardViewAdapter.ViewHolder(view);

        }
        if(i==USER &&NEW_FR==1) {
                    view = LayoutInflater.from(mContext).inflate(R.layout.card_item_users, viewGroup, false);
                    return new CardViewAdapter.ViewHolder(view);
        }
       return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewAdapter.ViewHolder viewHolder, int i) {
        viewHolder.recyclerView.setHasFixedSize(true);
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        if (list.get(i) == CONV && CONV_INFLATE == 1) {

            if(NEW_FR==0){
                setupUsers(null);
            }
            setupConversation(viewHolder.recyclerView);

        } else if (list.get(i)==USER && NEW_FR == 1) {

            setupUsers(viewHolder.recyclerView);
        }
    }
    @Override
    public int getItemCount() {

        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        // CardView
       private RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.block);

        }
    }

    @Override
    public int getItemViewType(int position) {

        if (list.get(position) == 0) {
            return CONV;
        } else {
            return USER;
        }
    }

    private void setupUsers(final RecyclerView recyclerView) {

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    users = new ArrayList<>();
                    users.clear();

                    int i = 0;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        User user = dataSnapshot1.getValue(User.class);
                        if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        }
                        if(i<10){
                            users.add(user);
                        }
                        i++;
                    }
                    if((CONV_INFLATE==1&&NEW_FR==1)||NEW_FR==1&&CONV_INFLATE==0){
                        userAdapter = new UserAdapter(mContext, users,0);
                        recyclerView.setAdapter(userAdapter);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    private void setupConversation(final RecyclerView recyclerView) {
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        chat_list = new ArrayList<>();
        chat_ref = FirebaseDatabase.getInstance().getReference("Chats");
        chat_ref.addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                               chat_list.clear();
                                               userset=new HashSet<>();
                                               last_messageList=new ArrayList<>();
                                               for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                   Chat chat = snapshot.getValue(Chat.class);

                                                   if (chat.getSender().equals(firebaseUser.getUid()) || chat.getReceiver().equals(firebaseUser.getUid())) {
                                                       last_messageList.add(chat);
                                                    }

                                                   if(chat.getReceiver().equals(firebaseUser.getUid())){
                                                       userset.add(chat.getSender());
                                                   }
                                                   if(chat.getSender().equals(firebaseUser.getUid())){
                                                       userset.add(chat.getReceiver());
                                                   }

                                               }
                                                   readChats(recyclerView);


                                           }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readChats(final RecyclerView recyclerView) {
        userList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    for (String id : userset) {

                        if (user.getId().equals(id)) {

                            userList.add(user);
                        }
                    }
                }
                convAdapter = new UserAdapter(mContext, userList,1);
                recyclerView.setAdapter(convAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
