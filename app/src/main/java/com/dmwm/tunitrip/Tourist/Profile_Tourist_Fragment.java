package com.dmwm.tunitrip.Tourist;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.dmwm.tunitrip.Add_Post_Activity;
import com.dmwm.tunitrip.MainActivity;
import com.dmwm.tunitrip.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class Profile_Tourist_Fragment extends Fragment {
    TextView textViewName,textViewhome,textViewRegion,textViewbio,textViewphone,textViewfollowers,gender;
    CircularImageView imageprofile;
    ImageView imagecover;
    FloatingActionButton floatingActionButton;
    String StoregePath="User_Profile_Cover_image/";
    ProgressDialog progressDialog;

     FirebaseAuth firebaseAuth;
     FirebaseUser firebaseUser;
     FirebaseDatabase firebaseDatabase;
     DatabaseReference databaseReference;
     StorageReference storageReference;

    //Uri image Picked
    Uri image_uri;
    // for checking profile or cover photo
    String profileOrCover;


    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;


    String cameraPermission[];
    String storagePermission[];

    public Profile_Tourist_Fragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile_tourist, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
        storageReference  = FirebaseStorage.getInstance().getReference();

        floatingActionButton=(FloatingActionButton)view.findViewById(R.id.floating_btn);
        textViewbio=(TextView)view.findViewById(R.id.textViewBioT);
        textViewName=(TextView)view.findViewById(R.id.textViewUserNameT);
        textViewhome=(TextView)view.findViewById(R.id.textViewHomeT);
        textViewRegion=(TextView)view.findViewById(R.id.textViewRegionT);
        textViewphone=(TextView)view.findViewById(R.id.textViewPhoneT);
        textViewfollowers=(TextView)view.findViewById(R.id.textViewPostNumberT);
        imagecover=(ImageView)view.findViewById(R.id.imageViewCoverT);
        imageprofile=(CircularImageView) view.findViewById(R.id.imageVProfileT);

        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog=new ProgressDialog(getContext());



        floatingActionButton.setOnClickListener(v -> showEditProfileDialog());

       Query query=databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        System.out.println(query);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    System.out.println(ds + "fffffffffffffffffffffffffffffffffffffffffffffffffffh");
                    String name = "" + ds.child("name").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String home = "" + ds.child("home").getValue();
                    String region = "" + ds.child("region").getValue();
                    String PhotoProfile =""+ds.child("photoProfile").getValue();
                    String  PhotoCover =""+ds.child("photoCover").getValue();
                    System.out.println(name);

                    textViewRegion.setText(region);
                    textViewName.setText(name);
                    textViewhome.setText(home);
                    textViewphone.setText(phone);

                    try {
                        Picasso.get().load(PhotoProfile).into(imageprofile);
                       // imageprofile.setImageURI(PhotoProfile);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.userdefault).into(imageprofile);
                    }
                    try {
                        Picasso.get().load(PhotoCover).into(imagecover);
                    }catch (Exception e){
                        Toast.makeText(getContext(), "no Cover Image", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("request code \n :"+requestCode+"\n result code \n"+resultCode+"\n heeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeere");
        if(resultCode == RESULT_OK){
            if (requestCode==IMAGE_PICK_GALLERY_CODE){
                //image picked from camera and getting from it
                image_uri=data.getData();
                System.out.println(image_uri+"     d5alt fi gallery ");
                // Picasso.get().load(image_uri).into(imageViewProfile);
                //imageViewProfile.setImageURI(image_uri);
                UploadProfileCoverPhoto(image_uri);


            }
            else if (requestCode==IMAGE_PICK_CAMERA_CODE){
                //image picked from gallery and getting from it
                System.out.println(image_uri+"    d5alt fi camera HAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAAHAHAHAHAH ");

                //Uri image_urio=data.getData();
                UploadProfileCoverPhoto(image_uri);
            }
            else {
                System.out.println(image_uri);
                Toast.makeText(getActivity(), "error here", Toast.LENGTH_SHORT).show();
            }

        }
    }





    // Methode of Storage Permission verifications
    private  boolean checkStoragePermission(){
        boolean resultat= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_DENIED);
        return resultat;
    }
    private void requestStoragePermission(){
        requestPermissions(storagePermission,STORAGE_REQUEST_CODE);
    }

    // Methode of Camera Permission verifications
    private  boolean checkCameraPermission(){

        boolean resultat= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);

        boolean resultat2= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return resultat2 && resultat;
    }
    private void requestCameraPermission(){
        requestPermissions(cameraPermission,CAMERA_REQUEST_CODE);
    }








    private void UploadProfileCoverPhoto(Uri image_uri) {

        String filePathName=StoregePath+""+profileOrCover+"_"+ firebaseUser.getUid();
        StorageReference storageReference2nd=storageReference.child(filePathName);
        System.out.println(image_uri+"    HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
        System.out.println(storageReference2nd+"    AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        storageReference2nd.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask =taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            Uri downloadUri=uriTask.getResult();
            //check if image loaded
            if (uriTask.isSuccessful()){
                progressDialog.show();
                //image uploded
                HashMap<String,Object> results = new HashMap<>();
                results.put(profileOrCover,downloadUri.toString());
                databaseReference.child(firebaseUser.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),"Image Downloading ... ",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"error in Downloading ",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });

            }else
            {
                Toast.makeText(getActivity(),"somthing was wrong",Toast.LENGTH_LONG).show();
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


    private void PickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Pic Discription");
        //putting image uri
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        // intent to start
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        System.out.println(image_uri+"777777777777777777777777777777777777///////////////////////////////////////////////////////////////////////////");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }

    private void PickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }

    private void showEditProfileDialog(){
        String option[]={"1- Change your Profile Picture","2- Change your Cover Photo","3- Change your Name","4- Change your Phone Number","5- Change your Bio","6- Change your Home"
                ,"7- Change your region","8- Asking for account verification "};
        AlertDialog.Builder builderDialog = new AlertDialog.Builder(getActivity());
        builderDialog.setTitle("Choose Option");
        builderDialog.setIcon(R.drawable.ic_settings);
        builderDialog.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    progressDialog.setMessage("Updating Profile Picture...");
                    profileOrCover="photoProfile";
                    showImagepicDialog();

                }
                else if (which==1){
                    progressDialog.setMessage("Updating Cover Image...");
                    profileOrCover="photoCover";
                    showImagepicDialog();}
                else if (which==2){
                    progressDialog.setMessage("Updating Pseudo Name...");
                    showDetailsupdtingDialog("name");

                }
                else if (which==3){
                    progressDialog.setMessage("Updating Phone Number...");
                    showDetailsupdtingDialog("Phone");
                }
                else if (which==4){
                    progressDialog.setMessage("Updating Bio...");
                    showDetailsupdtingDialog("bio");
                }
                else if (which==5){
                    progressDialog.setMessage("Updating Home ...");
                    showDetailsupdtingDialog("home");
                }
                else if (which==6){
                    progressDialog.setMessage("Updating Region");
                    showDetailsupdtingDialog("region");
                }
                else if (which==7){}

            }
        });
        builderDialog.create().show();

    }

    private void showDetailsupdtingDialog(final String key) {
        AlertDialog.Builder builderDialog = new AlertDialog.Builder(getActivity());
        builderDialog.setTitle("Update"+key);
        LinearLayout linearLayout=new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(50,50,50,50);
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter a new "+key);
        linearLayout.addView(editText);
        builderDialog.setView(linearLayout);
        builderDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value =editText.getText().toString().trim();
                if(!TextUtils.isEmpty(value)){
                    progressDialog.show();
                    HashMap<String ,Object> result =new HashMap<>();
                    result.put(key,value);
                    databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(getContext(),"Updating ...",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }else
                {Toast.makeText(getContext(),"Please Enter"+key,Toast.LENGTH_LONG).show();}
            }
        });
        builderDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        builderDialog.create().show();
    }


    private void showImagepicDialog() {
        String option[]={"Camera","Gallery"};
        AlertDialog.Builder builderDialog = new AlertDialog.Builder(getActivity());
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



    private  void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            // textViewProfile.setText(user.getEmail());

        }else
        {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        inflater.inflate(R.menu.menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout){

            if

            (id == R.id.action_logout){
                Date currentTime = Calendar.getInstance().getTime();
                String time=String.valueOf(currentTime);
                checkOnLineStatus(time);
                firebaseAuth.signOut();
                checkUserStatus();
            }
            else if (id == R.id.add_post_action){
                startActivity(new Intent(getActivity(), Add_Post_Activity.class));
            }

            return super.onOptionsItemSelected(item);
        }


        if (id == R.id.add_post_action){
            startActivity(new Intent(getActivity(), Add_Post_Activity.class));
        }
        return super.onOptionsItemSelected(item);
    }





    private  void checkOnLineStatus(String status){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        String myUid=user.getUid();
        System.out.println(myUid+" my uiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiid we did itttttttttt ");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus",status);
        db.updateChildren(hashMap);
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
                        Toast.makeText(getActivity(), " Permissions are necessary ! ", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), " Permissions are necessary ! ", Toast.LENGTH_SHORT).show();

                }
            }break;
        }

    }
}
