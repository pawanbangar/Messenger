package com.mrserviceman.messenger.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mrserviceman.messenger.R;
import java.util.HashMap;
import java.util.Random;

public class SignIn extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Handler handler;
    private ProgressDialog mProg;
    private DatabaseReference reference;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        mProg=new ProgressDialog(SignIn.this);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               try{
                   user = mAuth.getCurrentUser();
               }
               catch(DatabaseException ex)
               {
                   Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
               }
                if (user != null) {
                    Toast.makeText(getApplicationContext(),"User Exist",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignIn.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    mProg.setMessage("Creating New Account");
                    mProg.show();
                    Toast.makeText(getApplicationContext(),"No Account",Toast.LENGTH_SHORT).show();
                    updateui();

                }


            }
        }, 3000);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void updateui()
    {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Random rand = new Random();
                            int rand_int1 = rand.nextInt(100);

                            String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz";
                            int N=AlphaNumericString.length();
                            int rand2=rand.nextInt(N);
                            char mChar=AlphaNumericString.charAt(rand2);
                            String userName="Anonymous_"+mChar+rand_int1;

                            SharedPreferences sharedPreferences=getSharedPreferences("com.messenger.profile_name",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("com.messenger.profile_name",userName);
                            editor.apply();
                            final FirebaseUser user = mAuth.getCurrentUser();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

                            HashMap<String,String> data=new HashMap<>();
                            data.put("id",user.getUid());
                            data.put("userName",userName);
                            data.put("name",userName.toLowerCase());
                            data.put("ImageUrl","default");
                            data.put("about","default");
                            data.put("email","default");
                            data.put("num","default");
                            reference.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mProg.dismiss();
                                        startActivity(intent);

                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Failed to Create Account", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to Create Account", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}
