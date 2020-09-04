package com.mrserviceman.messenger.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mrserviceman.messenger.Activity.Message;
import com.mrserviceman.messenger.Model.ChatFragmentsModels.Chat;
import com.mrserviceman.messenger.Model.ChatFragmentsModels.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.mrserviceman.messenger.R;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> mUsers;
    private String thie_last_msg;
    private DatabaseReference reference;
    private int type;

    public UserAdapter(Context context, List<User> mUsers,int type) {
        this.context = context;
        this.mUsers = mUsers;
        this.type=type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view= LayoutInflater.from(context).inflate(R.layout.user_item,viewGroup,false);
            return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

            final User user=mUsers.get(i);
            viewHolder.name.setText(user.getUserName());
            if(!user.getImageUrl().equals("default"))
            {
                viewHolder.image.setImageURI(null);
                Picasso.get().load(user.getImageUrl()).into(viewHolder.image);
            }

            if(type==1){
                lastMessage(user.getId());
                if(thie_last_msg.equals("default")){
                    viewHolder.last_message.setVisibility(View.GONE);
                }
                else{
                    viewHolder.last_message.setText(thie_last_msg);
                }
            }
            else{
                String temp="@"+user.getName();
                viewHolder.last_message.setText(temp);
            }



    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CircleImageView image;
        private TextView name;
        private TextView last_message;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            image=(CircleImageView)itemView.findViewById(R.id.image);
            name=(TextView)itemView.findViewById(R.id.name);
            last_message=itemView.findViewById(R.id.last_message_or_username);

        }

        @Override
        public void onClick(View view) {
                int position=getAdapterPosition();
                User user=mUsers.get(position);
                Intent intent=new Intent(context, Message.class);
                intent.putExtra("UserId",user.getId());
                context.startActivity(intent);

        }
    }

    public class ViewHolder1 extends ViewHolder {
        AdView adView;
        public ViewHolder1(View inflate) {
            super(inflate);
            adView = inflate.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

        }
    }
    private void lastMessage(final String userid){
        thie_last_msg="default";
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
//        reference= FirebaseDatabase.getInstance().getReference("Chats");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    Toast.makeText(context,"Inside of Chat",Toast.LENGTH_SHORT).show();
//                    Chat chat = dataSnapshot1.getValue(Chat.class);
//                    if (chat.getSender().equals(userid) || chat.getReceiver().equals(userid)) {
//                        thie_last_msg = chat.getMessage();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        for(Chat chat:CardViewAdapter.last_messageList){
            if(userid.equals(chat.getReceiver())&&firebaseUser.getUid().equals(chat.getSender())||
                    userid.equals(chat.getSender())&&firebaseUser.getUid().equals(chat.getReceiver())
            ){
                if(chat.getSender().equals(firebaseUser.getUid())){
                    thie_last_msg="You:"+chat.getMessage();
                }
                else{
                    thie_last_msg=chat.getMessage();
                }

            }
        }


    }
}
