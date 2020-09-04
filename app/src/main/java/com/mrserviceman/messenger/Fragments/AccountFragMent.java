package com.mrserviceman.messenger.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.mrserviceman.messenger.Activity.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mrserviceman.messenger.Activity.ProfieEditActivity;
import com.mrserviceman.messenger.Activity.SignUpActivity;
import com.mrserviceman.messenger.Activity.WebActivity;
import com.mrserviceman.messenger.Model.ChatFragmentsModels.User;
import com.mrserviceman.messenger.R;

public class AccountFragMent extends Fragment {
    private Button sign_in,invite_friend;
    private TextView user_name;
    private ImageView edit,profile_pic;
    private User user;

    public AccountFragMent(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view=inflater.inflate(R.layout.fragment_account,container,false);

        Toolbar myToolbar = view.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
            }
        });

        sign_in=view.findViewById(R.id.sign_in_button);
        user_name=view.findViewById(R.id.user_name);
        edit=view.findViewById(R.id.edit_info);
        profile_pic=view.findViewById(R.id.profile_pic);

        SharedPreferences sharedPref = getContext().getSharedPreferences(
                "com.messenger.profile_name", Context.MODE_PRIVATE);
        String username = sharedPref.getString("com.messenger.profile_name", "");

        user_name.setText(username);
        invite_friend=view.findViewById(R.id.invite_friends);
        invite_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Heyy Try This All in One Messaging App I have Downloaded from uptodown Store");
                startActivity(Intent.createChooser(sendIntent, "Share With"));
            }
        });
        ImageView facebook=view.findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), WebActivity.class);
                intent.putExtra("URL","https://www.facebook.com/");
                startActivity(intent);
            }
        });

        SearchView googleSearch=view.findViewById(R.id.google_search);
        googleSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                Intent intent=new Intent(getContext(), WebActivity.class);
                intent.putExtra("URL", "https://www.google.com/search?q="+s.replace(" ","+"));
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

                sharedPref = getContext().getSharedPreferences(
                    "com.messenger.profile_pic_uri", Context.MODE_PRIVATE);
                final String profile_uri = sharedPref.getString("com.messenger.profile_pic_uri", "");
                try {
                    if (!profile_uri.equals("")){
                        profile_pic.setImageURI(Uri.parse(profile_uri));
                    }
                }
                    catch (Exception ex){
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                user=dataSnapshot.getValue(User.class);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        profile_pic.setImageURI(Uri.parse(user.getImageUrl()));
                    }

                if(!profile_uri.equals("")){
                    sign_in.setVisibility(View.GONE);
                }

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!profile_uri.equals("")){

                        startActivity(new Intent(getContext(), ProfieEditActivity.class));

                    }
                    else{
                        if(isInternetOn()){
                            Intent intent=new Intent(getActivity(), SignUpActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(getContext(),"No Internet",Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            });


        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        return view;


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_chat, menu);
    }

    public final boolean isInternetOn() {
        ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }




}
