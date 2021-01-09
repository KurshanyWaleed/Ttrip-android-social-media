package com.dmwm.tunitrip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dmwm.tunitrip.Tourist.TouristMainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Add_Post_Activity extends AppCompatActivity {
                ActionBar actionBar;
                FirebaseAuth firebaseAuth;
                DatabaseReference userDbRef;

                EditText titleET,descriptionET;
                ImageView imageView;
                Button uploadBtn;

                Uri image_uriP;

                ProgressDialog progressDialog;

               String name,email,uid,dp;


    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;


    String cameraPermission[];
    String storagePermission[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("Add new Post");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog= new ProgressDialog(this);
        actionBar.setSubtitle(email);



        firebaseAuth=FirebaseAuth.getInstance();
        userDbRef=FirebaseDatabase.getInstance().getReference("Users");
        checkUserStatus();

        titleET=findViewById(R.id.pTitleEt);
        descriptionET=findViewById(R.id.pDescriptionEt);
        imageView=findViewById(R.id.pImageIV);
        uploadBtn=findViewById(R.id.pUploadBtn);

        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot ds :snapshot.getChildren()){
                   name =""+ds.child("name").getValue();
                   email =""+ds.child("email").getValue();
                   dp =""+ds.child("image").getValue();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showImagePickDIalog();
            }
        });



        uploadBtn.setOnClickListener(v -> {

                String title=titleET.getText().toString().trim();
                String description =descriptionET.getText().toString().trim();
                if(TextUtils.isEmpty(title)){
                    Toast.makeText(this, "Enter title ! ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(description)){
                    Toast.makeText(this, "Enter description !", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (image_uriP==null){
                    String imagevaleur="noImage";
                           uploadData(title,description,imagevaleur);
                           startActivity(new Intent(getApplicationContext(), TouristMainActivity.class));
                    finish();
                }else{
                    uploadData(title,description,String.valueOf(image_uriP));
                    startActivity(new Intent(getApplicationContext(), TouristMainActivity.class));
                    finish();
                }



        });


    }

    private void uploadData(String title, String description, String uri) {
        progressDialog.setTitle("Publishing post ...");
        progressDialog.show();
        Date currentTime = Calendar.getInstance().getTime();
        String time=String.valueOf(currentTime);

        String filePathAndName = "Post/"+"post_"+time;
        if(!uri.equals("noImage")){
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri)).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
               String downloadUri = uriTask.getResult().toString();

               if(uriTask.isSuccessful()){
                   HashMap<Object,String> hashMap= new HashMap<>();
                   hashMap.put("uid",uid);
                   hashMap.put("uName",name);
                   hashMap.put("uEmail",email);
                   hashMap.put("pId",time);
                   hashMap.put("pTitle",title);
                   hashMap.put("pDescr",description);
                   hashMap.put("pImage",downloadUri);
                   hashMap.put("pTime",time);
                   DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
                   System.out.println(reference+"RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
                   reference.child(time).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                              progressDialog.dismiss();
                              Toast.makeText(Add_Post_Activity.this, " Post published ! ", Toast.LENGTH_SHORT).show();
                           titleET.setText("");
                           descriptionET.setText("");
                           imageView.setImageURI(null);
                           image_uriP=null;
                       }
                   }).addOnFailureListener(e -> {
                       progressDialog.dismiss();
                       Toast.makeText(Add_Post_Activity.this," something was wrong  ! "+ e.getMessage(), Toast.LENGTH_SHORT).show();

                   });



               }


            }).addOnFailureListener(e -> {
                 progressDialog.dismiss();
                Toast.makeText(Add_Post_Activity.this,e.getMessage()+"here is the error", Toast.LENGTH_SHORT).show();
            });
        }else{
            HashMap<Object,String> hashMap= new HashMap<>();
            hashMap.put("uid",uid);
            hashMap.put("uName",name);
            hashMap.put("uEmail",email);
            hashMap.put("pId",time);
            hashMap.put("pTitle",title);
            hashMap.put("pDescr",description);
            hashMap.put("pImage","noImage");
            hashMap.put("pTime",time);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            ref.child(time).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(Add_Post_Activity.this, " Post published ! ", Toast.LENGTH_SHORT).show();
                      titleET.setText("");
                      descriptionET.setText("");
                      imageView.setImageURI(null);
                      image_uriP=null;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Add_Post_Activity.this," something was wrong  ! "+ e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }


    }

    // Methode of Storage Permission verifications
    private  boolean checkStoragePermission(){
        boolean resultat= ContextCompat.checkSelfPermission(getApplicationContext()
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_DENIED);
        return resultat;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    // Methode of Camera Permission verifications
    private  boolean checkCameraPermission(){

        boolean resultat= ContextCompat.checkSelfPermission(getApplicationContext()
                ,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);

        boolean resultat2= ContextCompat.checkSelfPermission(getApplicationContext()
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return resultat2 && resultat;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermission,CAMERA_REQUEST_CODE);
    }

    private void showImagePickDIalog() {
         {
            String option[]={"Camera","Gallery"};
            AlertDialog.Builder builderDialog = new AlertDialog.Builder(this);
            builderDialog.setTitle("Pick image from");
            builderDialog.setItems(option, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which==0){
                        if(!checkCameraPermission()){
                            requestCameraPermission();
                        }else{
                            PickFromCamera();


                        }
                    }
                    else if (which==1){
                        if(!checkStoragePermission()){
                            requestStoragePermission();
                        }else{
                            PickFromGallery();
                        }
                    }


                }
            });
            builderDialog.create().show();

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("request code \n :"+requestCode+"\n result code \n"+resultCode+"\n heeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeere");
        if(resultCode == RESULT_OK){
            if (requestCode==IMAGE_PICK_GALLERY_CODE){
                //image picked from camera and getting from it
                image_uriP=data.getData();
                System.out.println(image_uriP+"     d5alt fi gallery ");
                // Picasso.get().load(image_uri).into(imageViewProfile);
                imageView.setImageURI(image_uriP);
                //UploadProfileCoverPhoto(image_uriP);


            }
            else if (requestCode==IMAGE_PICK_CAMERA_CODE){
                //image picked from gallery and getting from it
                System.out.println(image_uriP+"    d5alt fi camera HAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAAHAHAHAHAH ");

                //Uri image_urio=data.getData();
                imageView.setImageURI(image_uriP);
               // UploadProfileCoverPhoto(image_uriP);
            }
            else {
                System.out.println(image_uriP);
                Toast.makeText(this, "error here", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private void PickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Pic Description");
        //putting image uri
        image_uriP=this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ,values);
        // intent to start
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        System.out.println(image_uriP+"//////////");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uriP);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }

    private void PickFromGallery()  {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }


    private  void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            // textViewProfile.setText(user.getEmail());
            email=user.getEmail();
            uid=user.getUid();

        }else
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        menu.findItem(R.id.add_post_action).setVisible(false);
        menu.findItem(R.id.search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         switch (requestCode){
             case CAMERA_REQUEST_CODE:{
                 if (grantResults.length>0){
                     boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                     boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                     if (cameraAccepted && storageAccepted){
                            PickFromCamera();
                     }else{
                         Toast.makeText(this, " Permissions are necessary ! ", Toast.LENGTH_SHORT).show();
                     }

                 }else{

                 }
             }break;
             case STORAGE_REQUEST_CODE:{
                 if (grantResults.length>0){
                     boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                     if (storageAccepted){
                         PickFromGallery();
                     }
                 }else{
                     Toast.makeText(this, " Permissions are necessary ! ", Toast.LENGTH_SHORT).show();

                 }
             }break;
         }

    }
}