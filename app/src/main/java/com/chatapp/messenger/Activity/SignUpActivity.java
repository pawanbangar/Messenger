package com.chatapp.messenger.Activity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.chatapp.messenger.R;

import java.util.HashMap;
import java.util.Locale;


public class SignUpActivity extends AppCompatActivity {
    public static int APP_REQUEST_CODE = 99;
    private DatabaseReference reference;
    private FirebaseUser user;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        Button sign_up_button = findViewById(R.id.sign_up_with_phone);
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneLogin(view);
            }
        });
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        // If you are using in a fragment, call loginButton.setFragment(this);

        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                mAuth.getCurrentUser().linkWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                              if(task.isSuccessful()){
                                  user=task.getResult().getUser();
                                  Toast.makeText(getApplicationContext(),user.getDisplayName(),Toast.LENGTH_SHORT).show();


                              }
                            }
                        });

            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(getApplicationContext(),"cancelled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getApplicationContext(),"Error occured",Toast.LENGTH_SHORT).show();
            }
        });
                

    }
    public void phoneLogin(View view) {
        final Intent intent = new Intent(SignUpActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);

        // ... perform additional configuration ...
      //  configurationBuilder.setReadPhoneStateEnabled(true);
        //     intent.putExtra(
        //           AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
        //            configurationBuilder..setInitialPhoneNumber(phoneNumber)
        //                   build());
        Toast.makeText(getApplicationContext(),"reached",Toast.LENGTH_SHORT).show();
    }
    private String formatPhoneNumber(String phoneNumber) {
        // helper method to format the phone number for display
        try {
            PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber pn = pnu.parse(phoneNumber, Locale.getDefault().getCountry());
            phoneNumber = pnu.format(pn, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        }
        catch (NumberParseException e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }
    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                Toast.makeText(getApplicationContext(),toastMessage,Toast.LENGTH_SHORT).show();
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
                Toast.makeText(getApplicationContext(),toastMessage,Toast.LENGTH_SHORT).show();

            } else {

                final HashMap<String, String> user_data = new HashMap<>();




                if (loginResult.getAccessToken() != null) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                    Toast.makeText(getApplicationContext(),toastMessage,Toast.LENGTH_SHORT).show();
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {

                            PhoneNumber phoneNumber = account.getPhoneNumber();
                            if (account.getPhoneNumber() != null) {
                                // if the phone number is available, display it
//                                user_data.put("id", user.getUid());
////                                user_data.put("userName", USER_OWN.getUserName());
////                                user_data.put("name", USER_OWN.getName());
//                                user_data.put("ImageUrl", "default");
//                                user_data.put("about", "default");
//                                user_data.put("email", "default");
                                String formattedPhoneNumber = formatPhoneNumber(phoneNumber.toString());
//                                user_data.put("num", formattedPhoneNumber);
//
//
//
//                                reference.setValue(user_data);
                                Intent intent = new Intent(SignUpActivity.this, AfterPhonelogin.class);
                                intent.putExtra("num",formattedPhoneNumber);
                                startActivity(intent);
                            }

                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {

                        }

                    });
                }

                /*    userList=new ArrayList<>();
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Toast.makeText(getApplicationContext(),"got users info",Toast.LENGTH_SHORT).show();
                                User user1 = dataSnapshot.getValue(User.class);
                                username=user1.getUserName();
                            }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    for(User user1:userList){
                        if(user1.getId().equals(user.getUid())){
                            username=user1.getName();
                         //   Toast.makeText(getApplicationContext(), "Found user", Toast.LENGTH_SHORT).show();

                        }
                    }
                    */




                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
                //   goToMyLoggedInActivity();


            }
        else
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }

        }
    }
