package com.dmwm.tunitrip;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.dmwm.tunitrip.adapters.AdapterChat;
import com.dmwm.tunitrip.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Chat_Activity extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar toolbar;
    RecyclerView recyclerView;
    EditText editTextChat;
    TextView userChat,statusTextView;
    ImageView imageStatus,imageChatProfile;
    Button buttonSend;
    ProgressDialog pd;

    ValueEventListener seenListner;


    String hisUid;
    String myUid;
    String hisImage;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDatabaseReference;
    DatabaseReference usersDatabaseReferenceForSeen;


    List<ModelChat>chatList;
    AdapterChat adapterChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView=findViewById(R.id.recyclerViewChat);
        editTextChat=findViewById(R.id.textChatText);
        userChat=findViewById(R.id.userNameChat);
        statusTextView=findViewById(R.id.onlineTextView);
        imageStatus=findViewById(R.id.onlineImage);
        imageChatProfile=findViewById(R.id.imageUsersChat);
        buttonSend=findViewById(R.id.buttonSend);



        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        pd=new ProgressDialog(this);


        Intent intent =getIntent();
        hisUid=intent.getStringExtra("hisUid");
        hisImage=intent.getStringExtra("hisImage");


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        usersDatabaseReference=firebaseDatabase.getReference("Users");

        Query usersQuery = usersDatabaseReference.orderByChild("uid").equalTo(hisUid);
        System.out.println(usersQuery+"555555555555555555555555555555555555555555555555555555555555555555555");
        pd.setTitle("Loading...!");
        pd.show();
        usersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){
                    System.out.println(ds+"  888888888888888888888888888888888888888888888888888888888888888888888");
                    String name =""+ds.child("name").getValue();
                    System.out.println(name+"ffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
                    hisImage =""+ds.child("photoProfile").getValue();
                    String typingStatus =""+ds.child("typingTo").getValue();
                    String onlineStatus = ""+ds.child("onlineStatus").getValue();

                    if(typingStatus.equals(myUid)){
                        statusTextView.setText("typing ...");

                    }else{
                        statusTextView.setText("Last seen at : "+onlineStatus);
                    }






                    if (onlineStatus.equals("online")){
                        statusTextView.setText(onlineStatus);
                        imageStatus.setVisibility(View.VISIBLE);

                    }else{
                        statusTextView.setText("Last seen at : "+onlineStatus);
                    }
                    userChat.setText(name);
                    try {
                        Picasso.get().load(hisImage).placeholder(R.drawable.userdefault).into(imageChatProfile);
                    }catch (Exception e ){
                        Picasso.get().load(R.drawable.userdefault).into(imageChatProfile);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        editTextChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                     if (s.toString().trim().length()==0){
                         checkTypingStatus("noBody");
                     }else {
                         checkTypingStatus(hisUid);
                     }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String message = editTextChat.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(Chat_Activity.this, "Cannot send empty Message !!", Toast.LENGTH_SHORT).show();
                }else {
                     sendMessage(message);
                }


            }
        });

        pd.dismiss();

            readMessage();
            seenMessage();







    }

    @Override
    protected void onPause() {
        super.onPause();
        Date currentTime = Calendar.getInstance().getTime();
        String time=String.valueOf(currentTime);
       checkOnLineStatus(time);
       checkTypingStatus("onBody");
        usersDatabaseReferenceForSeen.removeEventListener(seenListner);
    }

    @Override
    protected void onResume() {
        checkOnLineStatus("online");
        super.onResume();
    }

    private void seenMessage() {
        usersDatabaseReferenceForSeen=FirebaseDatabase.getInstance().getReference("Chats");
        seenListner=usersDatabaseReferenceForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);
                    System.out.println(chat.getMessage() +"   the messsage is heeeeeeeeeeeeeeeeeeeeeeeeeeeere");
                    System.out.println(chat.isSeen()+"   the Seen is heeeeeeeeeeeeeeeeeeeeeeeeeeeere");
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String,Object> hashMapIsSeen=new HashMap<>();
                        hashMapIsSeen.put("isSeen","true");
                        ds.getRef().updateChildren(hashMapIsSeen);
                    }
            }
              //  System.out.println(chat.isSeen()+"the moderChat is heeeeeeeeeeeeeeeeeeeeeeeeeeeere");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void readMessage() {
        chatList=new ArrayList<>();
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               chatList.clear();
               for (DataSnapshot ds : snapshot.getChildren()){
                   String isSeen=""+ds.child("isSeen").getValue();
                   String message=""+ds.child("message").getValue();
                   String receiver=""+ds.child("receiver").getValue();
                   String sender=""+ds.child("sender").getValue();
                   String time=""+ds.child("time").getValue();
                  ModelChat chat=new ModelChat(isSeen,message,receiver,sender,time);
                   if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)||
                   chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){

                    chatList.add(chat);


                   }
                   System.out.println(isSeen+"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");


                   adapterChat = new AdapterChat(chatList,getApplicationContext(),hisImage);
                   adapterChat.notifyDataSetChanged();
                   recyclerView.setAdapter(adapterChat);

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

       // String time = String.valueOf(System.currentTimeMillis());
        Date currentTime = Calendar.getInstance().getTime();
        String time=String.valueOf(currentTime);
        String newTime=time.substring(11,16);
       // String newTime3=time.substring(11,5);


        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        hashMap.put("time",newTime);
        hashMap.put("isSeen","false");
        databaseReference.child("Chats").push().setValue(hashMap);



        editTextChat.setText("");




    }




    private  void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            // textViewProfile.setText(user.getEmail());
            myUid=user.getUid();

        }else
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

    }



    private  void checkOnLineStatus(String status){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus",status);
        db.updateChildren(hashMap);
    }


    private  void checkTypingStatus(String isTyping){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("typingTo",isTyping);
        db.updateChildren(hashMap);
    }





    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnLineStatus("online");
        super.onStart();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
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
}