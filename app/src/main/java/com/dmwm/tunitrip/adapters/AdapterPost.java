package com.dmwm.tunitrip.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dmwm.tunitrip.R;
import com.dmwm.tunitrip.models.ModelPost;

import java.util.List;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.HolderPosts> {
    List<ModelPost> arrayListUserPost;
    private Context context;

    public AdapterPost(List<ModelPost> arrayListUserPost, Context context) {
        this.arrayListUserPost = arrayListUserPost;
        this.context = context;

    }
    public class HolderPosts extends RecyclerView.ViewHolder {


        ImageView uPictureIv,pImageIv;
        TextView uNameTv,pTimeTv,pTitleTv,pDescriptionTv,pLikesTv;
        Button likeBtn,commentBtn,shareBtn;
        ImageButton moreBtn;


        public HolderPosts(@NonNull View itemView) {
            super(itemView);

            pImageIv=itemView.findViewById(R.id.imagePostItem);
            uNameTv=itemView.findViewById(R.id.userNamePItem);
            pDescriptionTv=itemView.findViewById(R.id.description_item);
            likeBtn=itemView.findViewById(R.id.likeBtnItem);
            commentBtn=itemView.findViewById(R.id.commentBtnitem);
            shareBtn=itemView.findViewById(R.id.shareBtnitem);
        }



    }
    @NonNull
    @Override
    public HolderPosts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent, false);

        return new HolderPosts(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderPosts holder, int position) {


        String uid=arrayListUserPost.get(position).getUid();
        String uName=arrayListUserPost.get(position).getuName();
        String pTitle=arrayListUserPost.get(position).getpTitle();
        String pDescription=arrayListUserPost.get(position).getpDescr();
        String pImage=arrayListUserPost.get(position).getpImage();
        String pTimeStamp=arrayListUserPost.get(position).getpTime();
        holder.uNameTv.setText(uName);
        holder.pDescriptionTv.setText(pDescription);

        holder.likeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                Toast.makeText(context,"LIKE",Toast.LENGTH_SHORT).show();
            }


        });
     /*   holder.commentBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                Toast.makeText(context,"COMMENT",Toast.LENGTH_SHORT).show();
            }


        });
        holder.moreBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                Toast.makeText(context,"More",Toast.LENGTH_SHORT).show();
            }


        });*/
    }

    @Override
    public int getItemCount() {
        return arrayListUserPost.size();
    }



}