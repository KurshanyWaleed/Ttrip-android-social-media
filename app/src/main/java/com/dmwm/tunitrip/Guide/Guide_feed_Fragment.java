package com.dmwm.tunitrip.Guide;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dmwm.tunitrip.R;
import com.dmwm.tunitrip.adapters.AdapterPost;
import com.dmwm.tunitrip.adapters.AdapterPost;
import com.dmwm.tunitrip.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Guide_feed_Fragment extends Fragment {
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;

    public Guide_feed_Fragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_guide_feed_, container, false);

        firebaseAuth=FirebaseAuth.getInstance();
        recyclerView=(RecyclerView)view.findViewById(R.id.RecyFeedPostsGuide);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        postList=new ArrayList<>();
        loadPosts();
        return view;
    }

    private void loadPosts(){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                System.out.println("ss");
                System.out.println(dataSnapshot.getChildren() + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);


                    postList.add(modelPost);
                    adapterPost=new AdapterPost(postList,getActivity());
                    recyclerView.setAdapter(adapterPost);
                    adapterPost.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}