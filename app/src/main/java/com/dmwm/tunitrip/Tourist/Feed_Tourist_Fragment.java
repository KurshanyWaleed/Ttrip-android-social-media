package com.dmwm.tunitrip.Tourist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.dmwm.tunitrip.Add_Post_Activity;
import com.dmwm.tunitrip.MainActivity;
import com.dmwm.tunitrip.R;
import com.dmwm.tunitrip.adapters.AdapterPost;
import com.dmwm.tunitrip.adapters.AdapterUsersList;
import com.dmwm.tunitrip.models.ModelPost;
import com.dmwm.tunitrip.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class Feed_Tourist_Fragment extends Fragment {
        RecyclerView recyclerView,recyclerViewP;
        AdapterUsersList adapterUsersList;
        List<ModelUser> userslist;
        List<ModelPost> postList;
        AdapterPost adapterPost;
        FirebaseAuth firebaseAuth;
        Button btnShowGuides,btnHideGuides;
        LinearLayout linearLayout;

    public Feed_Tourist_Fragment() {
        // Required empty public constructor
    }


    private void searchUsers(String newText) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReferenceUsers =FirebaseDatabase.getInstance().getReference("Users");
        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userslist.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    System.out.println(snapshot+"jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                    System.out.println(ds + "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!modelUser.getUid().equals(firebaseUser.getUid())) {
                        if(modelUser.getName().toLowerCase().contains(newText.toLowerCase())||
                                (modelUser.getEmail().toLowerCase().contains(newText.toLowerCase()))){

                            userslist.add(modelUser);
                        }
                    }
                    adapterUsersList = new AdapterUsersList(userslist, getActivity());

                    recyclerView.setAdapter(adapterUsersList);
                    adapterUsersList.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_feed_tourist_, container, false);

        linearLayout=view.findViewById(R.id.layout);

        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerview_users);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        btnShowGuides=view.findViewById(R.id.btnShowGuides);

        btnHideGuides=new Button(getActivity());
        btnHideGuides.setWidth(30);
        btnHideGuides.setWidth(10);
        btnHideGuides.setText("Hide Guides List");
        btnHideGuides.setBackgroundColor(getResources().getColor(R.color.red));


        btnShowGuides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                btnShowGuides.setVisibility(View.GONE);
                linearLayout.addView(btnHideGuides);
            }
        });

        btnHideGuides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
               linearLayout.removeView(btnHideGuides);
                btnShowGuides.setVisibility(View.VISIBLE);
            }
        });


        recyclerViewP=(RecyclerView)view.findViewById(R.id.recyclerview_usersPosts);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        recyclerViewP.setLayoutManager(layoutManager);

        firebaseAuth=FirebaseAuth.getInstance();

        userslist=new ArrayList<>();
        postList=new ArrayList<>();
        loadPosts();
        getAllUsers();
        return view;

    }

    private void loadPosts(){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot);
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                    adapterPost=new AdapterPost(postList,getActivity());
                    recyclerViewP.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                userslist.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    String type=ds.child("type").getValue().toString();
                    ModelUser modelUser=ds.getValue(ModelUser.class);
                    if(!modelUser.getUid().equals(firebaseUser.getUid()))
                    {
                       userslist.add(modelUser);
                    }
                    adapterUsersList=new AdapterUsersList(userslist,getActivity());
                    recyclerView.setAdapter(adapterUsersList);
                    adapterUsersList.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

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
        menu.findItem(R.id.add_post_action).setVisible(false);
        //SearchView
        MenuItem item =menu.findItem(R.id.search);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(item);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim()))
                {
                    searchUsers(query);
                }else
                {
                    getAllUsers();
                }



                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim()))
                {
                    searchUsers(newText);
                }else
                {
                    getAllUsers();
                }
                return false;
            }
        });










        super.onCreateOptionsMenu(menu,inflater);


    }
    private  void checkOnLineStatus(String status){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus",status);
        db.updateChildren(hashMap);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout){
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
}