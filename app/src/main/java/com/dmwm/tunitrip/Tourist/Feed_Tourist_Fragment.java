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
import android.widget.SearchView;

import com.dmwm.tunitrip.Add_Post_Activity;
import com.dmwm.tunitrip.MainActivity;
import com.dmwm.tunitrip.R;
import com.dmwm.tunitrip.adapters.AdapterUsersList;
import com.dmwm.tunitrip.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Feed_Tourist_Fragment extends Fragment {
        RecyclerView recyclerView;
        AdapterUsersList adapterUsersList;
        List<ModelUser> userslist;
        FirebaseAuth firebaseAuth;

    public Feed_Tourist_Fragment() {
        // Required empty public constructor
    }


    private void searchUsers(String newText) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userslist.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    System.out.println(snapshot.getChildren() + "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
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

        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerview_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        firebaseAuth=FirebaseAuth.getInstance();

        userslist=new ArrayList<>();
        
        getAllUsers();
        return view;

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
                    System.out.println(type+"HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
                    ModelUser modelUser=ds.getValue(ModelUser.class);
                    if(!modelUser.getUid().equals(firebaseUser.getUid()) && (type.equals("Guide")))
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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
            if (id == R.id.add_post_action){
                startActivity(new Intent(getActivity(), Add_Post_Activity.class));
            }

        }
        return super.onOptionsItemSelected(item);
    }
}