package com.dmwm.tunitrip.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dmwm.tunitrip.Chat_Activity;
import com.dmwm.tunitrip.R;
import com.dmwm.tunitrip.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsersList extends RecyclerView.Adapter<AdapterUsersList.MyViewHolder> {

    List<ModelUser> arrayListUser;
    private Context context;



    public AdapterUsersList(List<ModelUser> arrayList, Context context) {
        this.arrayListUser=arrayList;
        this.context=context;
    }
    public  class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        TextView textViewUsername;
        TextView textViewBio;
        TextView textViewRegion;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);


            imageView=itemView.findViewById(R.id.imageViewUserRec);
            textViewUsername=itemView.findViewById(R.id.username);
            textViewBio=itemView.findViewById(R.id.textViewBioView);
            textViewRegion=itemView.findViewById(R.id.textViewRegionV);
        }

    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_items,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String hisUid = arrayListUser.get(position).getUid();
        String photoProfile=arrayListUser.get(position).getPhotoProfile();
        String username=arrayListUser.get(position).getName();
        String bio=arrayListUser.get(position).getBio();
        String userRegion=arrayListUser.get(position).getRegion();

        holder.textViewRegion.setText(userRegion);
        holder.textViewBio.setText(bio);
        holder.textViewUsername.setText(username);
        try {
            Picasso.get().load(photoProfile).into(holder.imageView);
        }catch (Exception e){
            Picasso.get().load(R.drawable.userdefault).into(holder.imageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, Chat_Activity.class);
                i.putExtra("hisUid",hisUid);
                context.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListUser.size();
    }



}
