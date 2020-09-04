package com.mrserviceman.messenger.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mrserviceman.messenger.Model.ChatFragmentsModels.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mrserviceman.messenger.R;
import com.mrserviceman.messenger.Utils.ImageStorage;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfieEditActivity extends AppCompatActivity {

    private static final int IMG_REQUEST = 100;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String mUrl;
    private StorageReference storageReference;
    private Uri imageUrl;
    private CircleImageView profile_pic;
    public static String path;
    private TextView about_me;
    private User user;
    private User current_user;
    private boolean cont=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profie_edit);

        ImageButton back=findViewById(R.id.back);
        TextView save=findViewById(R.id.save);
         profile_pic=findViewById(R.id.profile_pic);
        TextView change_photo=findViewById(R.id.change_photo);
        final TextView name=findViewById(R.id.user_id);
        about_me=findViewById(R.id.about_me);
        final EditText username_edit=findViewById(R.id.username_edit);
        final TextView number=findViewById(R.id.number);
        final EditText email=findViewById(R.id.email_edit);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-6605349658509803~8536929057");

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(user.getId().equals(firebaseUser.getUid()));
                {
                    current_user=user;
                    mUrl=user.getImageUrl();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
            SharedPreferences sharedPref = getSharedPreferences(
                    "com.messenger.profile_pic_uri", Context.MODE_PRIVATE);
            try{
                profile_pic.setImageURI(Uri.parse(sharedPref.getString("com.messenger.profile_pic_uri", "")));
            }
            catch (Exception ex){
                profile_pic.setImageURI(Uri.parse(current_user.getImageUrl()));
            }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, String> map = new HashMap<>();

                databaseReference=FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            User user=dataSnapshot1.getValue(User.class);
                            if(user.getName().equals(username_edit.getText().toString())){
                                if(user.getId().equals(current_user.getId())){
                                    cont=false;
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                if(mUrl.equals("")){
                    map.put("ImageUrl", "default");
                }
                else{
                    map.put("ImageUrl", mUrl);
                }
                map.put("id", firebaseUser.getUid());
                if(email.getText().toString().equals("")){
                    map.put("email", "default");
                }
                else{
                    map.put("email", email.getText().toString());
                }
                if(about_me.getText().toString().equals("")){
                    map.put("about", "default");
                }
                else{
                    map.put("about", about_me.getText().toString());
                }
                if(!username_edit.getText().toString().equals("")){
                    map.put("userName", username_edit.getText().toString());
                    map.put("name",username_edit.getText().toString().replace(" ","_").toLowerCase());
                }
                map.put("num",number.getText().toString());

                if(cont){
                    databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    SharedPreferences sharedPreferences=getSharedPreferences("com.messenger.profile_name",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("com.messenger.profile_name",username_edit.getText().toString());
                    editor.apply();
                    databaseReference.setValue(map);
                    Intent intent=new Intent(new Intent(ProfieEditActivity.this,MainActivity.class));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            }
        });
        change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImge();
            }
        });
        databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    user=dataSnapshot1.getValue(User.class);
                    if(user.getId().equals(firebaseUser.getUid())){
                        name.setText(user.getName());
                        if(!user.getAbout().equals("default")){
                            about_me.setText(user.getAbout());
                        }
                        username_edit.setText(user.getUserName());
                        if(!user.getNum().equals("default")){
                            number.setText(user.getNum());
                        }
                        if(!user.getEmail().equals("default")){
                            email.setText(user.getEmail());
                        }
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void openImge() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }



    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        if (imageUrl!= null) {
            storageReference= FirebaseStorage.getInstance().getReference();
            final StorageReference ref = storageReference.child(System.currentTimeMillis()+"."+getFilExtension(imageUrl));
            UploadTask uploadTask = ref.putFile(imageUrl);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        mUrl = downloadUri.toString();
                        progressDialog.dismiss();
                    } else {
                        // Handle failures
                        progressDialog.dismiss();
                        // ...
                    }
                }
            });


        } else {
            Toast.makeText(getApplicationContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private String getFilExtension(Uri imageUrl) {
        ContentResolver contentResolver=getContentResolver();

        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUrl));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUrl = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUrl);
                String savedImage=ImageStorage.saveToSdCard(bitmap,"profile_pic_new");
                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("com.messenger.profile_pic_uri",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("com.messenger.profile_pic_uri", savedImage);
                editor.apply();
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
