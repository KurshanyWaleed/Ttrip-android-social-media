package com.dmwm.tunitrip.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dmwm.tunitrip.R;
import com.dmwm.tunitrip.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.type.DateTime;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterChat  extends RecyclerView.Adapter<AdapterChat.MyHolder>

{
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_WRIGHT=1;
    List<ModelChat> chatList;
     Context context;
    String imageUri;
    FirebaseUser firebaseUser;

    public AdapterChat(List<ModelChat> chatList, Context context, String imageUri) {
        this.chatList = chatList;
        this.context = context;
        this.imageUri = imageUri;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==MSG_TYPE_WRIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
          return new MyHolder(view);
        }else {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
            return new MyHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd' T 'HH:mm:ss", Locale.US);
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd' T 'HH:mm:ss.SSSX", Locale.US);
        String message = chatList.get(position).getMessage();
        String time = chatList.get(position).getTime();
        String seen= chatList.get(position).isSeen();


       /* DateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
        String strDate = (String) dateFormat.format(time);
*/



        // Note, MM is months, not mm


       // String inputText = "2012-11-17T00:00:00.000-05:00";
        Date date = new Date();
        try {
            date = inputFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String output = outputFormat.format(date);
        System.out.println(output+"      hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");



        holder.messagesTV.setText(message);
        holder.timeTV.setText(time+" - ");

        try{
            Picasso.get().load(imageUri).into(holder.imageViewChat);
        }catch (Exception e ){
           // Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }
        System.out.println(seen +" AAAAseeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeen");
        if (position==chatList.size()-1){

            if (seen.equals("true")){
                holder.isSeenTV.setText("seen");
            }else {
                holder.isSeenTV.setText("delivered");
            }

        }else {
            holder.isSeenTV.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_WRIGHT;
    }
    else
    {
        return MSG_TYPE_LEFT;
    }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView imageViewChat;
        TextView messagesTV,timeTV,isSeenTV;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            imageViewChat=itemView.findViewById(R.id.imageV);
            messagesTV=itemView.findViewById(R.id.textmessage);
            timeTV=itemView.findViewById(R.id.dateText);
            isSeenTV=itemView.findViewById(R.id.datedelevred);

        }
    }
}
