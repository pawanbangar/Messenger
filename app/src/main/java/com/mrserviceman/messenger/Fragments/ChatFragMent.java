package com.mrserviceman.messenger.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mrserviceman.messenger.Activity.SettingsActivity;
import com.mrserviceman.messenger.Notification.Token;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mrserviceman.messenger.Activity.SearchActivity;
import com.mrserviceman.messenger.Activity.WebActivity;
import com.mrserviceman.messenger.Adapter.CardViewAdapter;
import com.mrserviceman.messenger.Model.ChatFragmentsModels.Chat;
import com.mrserviceman.messenger.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChatFragMent extends Fragment{
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE =100 ;
    private FloatingActionButton Fab;
    private RecyclerView recyclerView;
    public  CardViewAdapter cardViewAdapter;
    private SearchView googleSearch;
    private ImageView facebook;
    private ImageView search_user;
    private ImageView no_internet;
    private List<Integer> list;
    private FirebaseUser firebaseUser;
    private Set<String> userset;
    private DatabaseReference chat_ref;
    private ProgressBar circularProgressBar;
    private static final long UPDATE_TIME_IN_MILLIS = 2000;
    private ColorStateList[] colorsList;
    private Handler updateProgressColorHandler;
    private ValueEventListener valueEventListener;
    private int position = 0;
    private Runnable updateProgressColorRunnable;
    String mUid;
    public ChatFragMent()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat, container, false);
        Fab=view.findViewById(R.id.fab);


        list=new ArrayList<>();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        mUid=firebaseUser.getUid();
        SharedPreferences sp=getContext().getSharedPreferences("SP_USER",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("CURRENT_USER",mUid);
        editor.apply();

        recyclerView=view.findViewById(R.id.recycler);

        //ColorStateList list=new ColorStateList(Color.parseColor("#3DADF2"),Color.parseColor("#F2A30F"));
        circularProgressBar=view.findViewById(R.id.progress_bar);
        no_internet=view.findViewById(R.id.no_internet);

        no_internet.setVisibility(View.GONE);


        showAndUpdateProgressColor();



        if(isInternetOn()){
            checkChats();
        }
        else{
            circularProgressBar.setVisibility(View.GONE);
            no_internet.setVisibility(View.VISIBLE);
        }



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


        facebook=view.findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), WebActivity.class);
                intent.putExtra("URL","https://www.facebook.com/");
                startActivity(intent);
            }
        });

        googleSearch=view.findViewById(R.id.google_search);
        googleSearch.setIconified(false);
        googleSearch.clearFocus();

        googleSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                Intent intent=new Intent(getContext(),WebActivity.class);
                intent.putExtra("URL", "https://www.google.com/search?q="+s.replace(" ","+"));
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        search_user=view.findViewById(R.id.search_user);
        search_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        Fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),SearchActivity.class));
            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());



        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


  return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    private void updateToken(String token) {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        ref.child(firebaseUser.getUid()).setValue(token1);
    }

    private void checkChats() {
        chat_ref = FirebaseDatabase.getInstance().getReference("Chats");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userset = new HashSet<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid())) {
                        userset.add(chat.getSender());
                    }
                    if (chat.getSender().equals(firebaseUser.getUid())) {
                        userset.add(chat.getReceiver());
                    }

                }


                if (userset.size() >= 5) {
                    CardViewAdapter.CONV_INFLATE = 1;
                    CardViewAdapter.NEW_FR = 0;
                    list.add(0);

                }
                if (userset.size() == 0) {
                    CardViewAdapter.NEW_FR = 1;
                    CardViewAdapter.CONV_INFLATE = 0;
                    list.add(1);
                }
                if (userset.size() < 5 && userset.size() > 0) {
                    list.add(0);
                    list.add(1);
                    CardViewAdapter.CONV_INFLATE = 1;
                    CardViewAdapter.NEW_FR = 1;
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                cardViewAdapter = new CardViewAdapter(getContext(), list);
                circularProgressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(cardViewAdapter);
                chat_ref.removeEventListener(valueEventListener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        chat_ref.addValueEventListener(valueEventListener);


    }

    @Override
    public void onStart() {
        super.onStart();
    }
    public final boolean isInternetOn() {
            ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            return cm.getActiveNetworkInfo() != null;
    }
    private void showAndUpdateProgressColor() {
        // Initialize the color list
        colorsList = new ColorStateList[]{
                ColorStateList.valueOf(Color.BLUE), ColorStateList.valueOf(Color.RED),
                ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorPrimary)),
                        ColorStateList.valueOf(Color.GREEN),
                        ColorStateList.valueOf(Color.MAGENTA), ColorStateList.valueOf(Color.CYAN)};
        // Initialize Runnable
        updateProgressColorRunnable = new Runnable() {
            @Override
            public void run() {

                if (position >= colorsList.length) {
                    // There are no more elements in the array
                    // Reset the position
                    position = 0;
                }
                // Change the progress bar color
                circularProgressBar.setIndeterminateTintList(colorsList[position]);
                // Increment the position
                position++;
                // Call the runnable again with a delay
                updateProgressColorHandler.postDelayed(updateProgressColorRunnable, UPDATE_TIME_IN_MILLIS);
            }
        };

        // Initialize Handler
        updateProgressColorHandler = new Handler();
        // Use the handler to add the runnable to the message queue
        updateProgressColorHandler.post(updateProgressColorRunnable);
    }

    }