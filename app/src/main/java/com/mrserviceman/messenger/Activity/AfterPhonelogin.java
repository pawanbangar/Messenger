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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mrserviceman.messenger.Model.ChatFragmentsModels.User;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mrserviceman.messenger.R;
import com.mrserviceman.messenger.Utils.ImageStorage;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AfterPhonelogin extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private Button done, upload;
    private CircleImageView profile_img;
    private EditText name, username;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private static final int IMG_REQUEST = 1;
    private Uri imageUrl;
    private String num;
    private String mUrl="default";
    private User current_user;
    private Boolean cont=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_phonelogin);
        upload = findViewById(R.id.upload_button);
        done = findViewById(R.id.done_button);
        profile_img = findViewById(R.id.profile_pic);
        name = findViewById(R.id.enter_name_input);
        username = findViewById(R.id.input_for_username);



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    if(user.getId().equals(firebaseUser.getUid()))
                    {
                        current_user=user;
                        name.setText(current_user.getUserName());
                        username.setText(current_user.getName());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> map = new HashMap<>();
                map.put("ImageUrl", mUrl);
                map.put("id", current_user.getId());
                map.put("email",current_user.getEmail());
                map.put("about",current_user.getAbout());
                map.put("name",username.getText().toString());
                map.put("num",num);
                map.put("userName",name.getText().toString());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            User user=dataSnapshot1.getValue(User.class);
                            if(user.getName().equals(username.getText().toString())){
                                if(!user.getId().equals(current_user.getId())){
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

                if(!name.getText().equals("")&&!username.getText().equals("")){

                    if(cont){
                        SharedPreferences sharedPreferences=getSharedPreferences("com.messenger.profile_name",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("com.messenger.profile_name",name.getText().toString());
                        editor.apply();
                        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(current_user.getId());
                        databaseReference.setValue(map);
                        Intent intent=new Intent(new Intent(AfterPhonelogin.this,MainActivity.class));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),"Please Provide Valid Username and Name",Toast.LENGTH_SHORT).show();
                }

            }
        });
        storageReference = FirebaseStorage.getInstance().getReference("uploads");




        num = getIntent().getStringExtra("num");
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImge();
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
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        if (imageUrl != null) {
            storageReference=FirebaseStorage.getInstance().getReference();
            final StorageReference ref = storageReference.child(System.currentTimeMillis()+"."+getFilExtension(imageUrl));
            uploadTask = ref.putFile(imageUrl);

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
                String savedImage= ImageStorage.saveToSdCard(bitmap,"profile_pic_new");
                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("com.messenger.profile_pic_uri", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("com.messenger.profile_pic_uri", savedImage);
                editor.apply();

                profile_img.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
