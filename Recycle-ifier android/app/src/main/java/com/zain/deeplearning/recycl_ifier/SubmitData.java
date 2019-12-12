package com.zain.deeplearning.recycl_ifier;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

public class SubmitData extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 101;
    Button Img_choose,Img_upload ;
    ImageView Image_View;
    StorageReference storage_ref;
    private StorageTask uploadTask;
    private Uri image_uri;
    String image_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
////        getSupportActionBar().hide();
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.mipmap.app_logo_front_foreground);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_submit_data);

        storage_ref= FirebaseStorage.getInstance().getReference("Waste Images");
        Img_choose = findViewById(R.id.chse);
        Img_upload = findViewById(R.id.upld);

        Image_View = findViewById(R.id.imgv);
        Img_choose.setOnClickListener(view -> {
            browse_upload_image();
            Img_upload.setVisibility(View.VISIBLE);
        });

        Img_upload.setOnClickListener(v -> {
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(SubmitData.this, "Image upload in process!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(SubmitData.this, "Image is being Uploaded!", Toast.LENGTH_LONG).show();
                upload_image();
            }
        });

    }
    private String getExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    private void upload_image(){
        StorageReference Ref = storage_ref.child(System.currentTimeMillis()+"."+getExtension(image_uri));
        uploadTask=Ref.putFile(image_uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get a URL to the uploaded content
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(SubmitData.this, "Image Uploaded Successfully!", Toast.LENGTH_LONG).show();
                    recreate();
                })
                .addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                    Toast.makeText(SubmitData.this, "Failed to Upload!...Please Try again!"+ exception.getMessage(), Toast.LENGTH_LONG).show();
                    // TODO
                    // please add : after specific number of button click restart activity
                });
    }
    private void browse_upload_image(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,CAMERA_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            image_uri = data.getData();
            Image_View.setImageURI(image_uri);
        }
    }
}
