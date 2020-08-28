package com.chatapp.messenger.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.messenger.Model.ChatFragmentsModels.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.chatapp.messenger.Adapter.UserAdapter;
import com.chatapp.messenger.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    public List<User> usersList;
    private Button prev_button;
    private RecyclerView search_user;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView=findViewById(R.id.user_search);
        prev_button=findViewById(R.id.prev_button);
        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                    searchUsers(s);

                return false;
            }
        });
        AppCompatImageView search=findViewById(R.id.searchView);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!searchView.getQuery().equals("")){
                    searchUsers(searchView.getQuery().toString());
                }

            }
        });
        search_user=(RecyclerView)findViewById(R.id.recycler_search);
        search_user.setHasFixedSize(true);
        search_user.setLayoutManager(new LinearLayoutManager(this));
    }
    private void searchUsers(final String s){

        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        Query query= FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("name")
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList=new ArrayList<>();
                usersList.clear();
                if(!s.equals("")){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                        User user1=snapshot.getValue(User.class);

                        if(!user1.getId().equals(user.getUid())){
                            usersList.add(user1);
                            if(usersList.size()>=5)
                            {
                                break;
                            }
                        }

                    }
                    userAdapter=new UserAdapter(SearchActivity.this,usersList,0);
                    search_user.setAdapter(userAdapter);
            }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
