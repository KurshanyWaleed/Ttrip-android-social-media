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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
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
    TextView userChat,statusTextView,isSeenTV;
    ImageView imageStatus;
    CircularImageView imageChatProfile;
    ImageButton buttonSend;
    ProgressDialog pd;

    ValueEventListener seenListner;


    String hisUid;
    String myUid;
    String hisImage;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
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
        isSeenTV=findViewById(R.id.MessageDelivered);


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        pd=new ProgressDialog(this);


        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");

        firebaseAuth=FirebaseAuth.getInstance();
        myUid=firebaseAuth.getUid();
        firebaseDatabase=FirebaseDatabase.getInstance();
        usersDatabaseReference=firebaseDatabase.getReference("Users");



        Query usersQuery = usersDatabaseReference.orderByChild("uid").equalTo(hisUid);
        usersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){
                    //  System.out.println(ds+"  888888888888888888888888888888888888888888888888888888888888888888888");
                    String name =""+ds.child("name").getValue();
                    hisImage =""+ds.child("photoProfile").getValue();

                    String typingStatus =""+ds.child("typingTo").getValue();
                    String onlineStatus = ""+ds.child("onlineStatus").getValue();

                    // String newOnligneStatus=onlineStatus.substring(0,12);
                    if(typingStatus.equals(myUid)){
                        statusTextView.setText("typing ...");

                    }else{

                        statusTextView.setText("Last seen at : "+onlineStatus);
                    }


                    if (onlineStatus.equals("online")){
                        statusTextView.setText(onlineStatus);
                        imageStatus.setVisibility(View.VISIBLE);

                    }else{
                      //  String onlineStatus2=onlineStatus.substring(0,3);
                        statusTextView.setText(" Last seen at : "+onlineStatus);
                        imageStatus.setVisibility(View.GONE);
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


        seenMessage();
        readMessage();



        buttonSend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String message = editTextChat.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(Chat_Activity.this, "Cannot send empty Message !!", Toast.LENGTH_SHORT).show();
                }else {
                    sendMessage(message);
                }

               // seenMessage();
                readMessage();

            }
        });


















    }
    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // String time = String.valueOf(System.currentTimeMillis());
        Date currentTime = Calendar.getInstance().getTime();
        String time=String.valueOf(currentTime);
        String newTime=time.substring(11,20);
        // String newTime3=time.substring(11,5);


        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        hashMap.put("time",newTime);
        hashMap.put("isSeen","false");
        databaseReference.child("Chats").push().setValue(hashMap);


        isSeenTV.setVisibility(View.VISIBLE);
        editTextChat.setText("");




    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    private  void checkTypingStatus(String isTyping){
        firebaseAuth=FirebaseAuth.getInstance();
        String myUid=firebaseAuth.getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("typingTo",isTyping);
        db.updateChildren(hashMap);
    }

    private void seenMessage() {
        usersDatabaseReferenceForSeen=FirebaseDatabase.getInstance().getReference("Chats");
        seenListner=usersDatabaseReferenceForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    String isSeen=""+ds.child("isSeen").getValue();
                    String message=""+ds.child("message").getValue();
                    String receiver=""+ds.child("receiver").getValue();
                    String sender=""+ds.child("sender").getValue();
                    String time=""+ds.child("time").getValue();
                    String seen=""+ds.child("isSeen").getValue();
                    System.out.println(isSeen+"       isSeen      HHHHHHHHHHHHHHHHHHHHHHHHH");
                    System.out.println(message+"          message    AA");
                    System.out.println(receiver+"        reciver   ");
                    System.out.println(sender+"           sender  ");
                    System.out.println(time+"  time           ");
                    ModelChat chat=new ModelChat(isSeen,message,receiver,sender,time);
                  /*  ModelChat chat=ds.getValue(ModelChat.class);
                    System.out.println(chat.getMessage() +"   the messsage is heeeeeeeeeeeeeeeeeeeeeeeeeeeere");
                    System.out.println(chat.isSeen()+"   the Seen is heeeeeeeeAAAAAAAAAAAAAAAAAAeeeeeeeeere");*/

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
                    System.out.println(isSeen+"       isSeen      AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAA");
                    System.out.println(message+"          message    AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAA");
                    System.out.println(receiver+"        reciver    AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAA");
                    System.out.println(sender+"           sender  AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAA");
                    System.out.println(time+"  time           AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBAAAAAAAAAAAAAAAAAA");
                    ModelChat chat=new ModelChat(isSeen,message,receiver,sender,time);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        if(isSeen.equals("true")){isSeenTV.setVisibility(View.VISIBLE); isSeenTV.setText("Seen");
                        }else{isSeenTV.setVisibility(View.VISIBLE); isSeenTV.setText("delivered");}
                        chatList.add(chat);

                    }
                    adapterChat = new AdapterChat(chatList,getBaseContext(),hisImage);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }









    @Override
    protected void onStart() {

        super.onStart();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.add_post_action).setVisible((false));
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
        }
            return super.onOptionsItemSelected(item);
    }
}