package com.example.apk_extractor.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.apk_extractor.MainActivity;
import com.example.apk_extractor.Models.app;
import com.example.apk_extractor.R;
import com.gc.materialdesign.views.ButtonFlat;
import com.example.apk_extractor.Utilities.utility;
import com.example.apk_extractor.Utilities.utility_message;
import com.example.apk_extractor.Status.status;
import com.example.apk_extractor.activities.ap_detail_activity;

public class app_adaptor extends RecyclerView.Adapter<app_adaptor.holder>
{
     List<app> first=new ArrayList<>();
    Context context;
   public app_adaptor(List<app> first, Context context)
    {
        this.first=first;
        this.context=context;
    }

    // Launch of UI
    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.app_layout,null);
        return new holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position)
    {
        // get the app from list
        app x=first.get(position);

        // start filling info
        holder.name.setText(x.name);
        holder.id.setText(x.id);
        holder.img.setImageDrawable(x.icon);

        // onclick Listener on extract and share
        holder.extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start extracting

                // for debugging
                utility.app_dir_default(context);
                // for debugging

                boolean result=utility.extract_apk(x.src,context,x.name);





                if(result)
                {
                    utility_message.success(context);
                }
                else
                {
                    utility_message.failure(context);

                    // for debugging
                    //MainActivity.Message_1(" src : "+);
                    // for debugging
                }



            }
        });
        share(holder.share,x);
        cardClick(holder.onclick,x);


    }
    void share(ButtonFlat btn,app x)
    {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ap_detail_activity.share_1(context,x);
            }
        });
    }
    public static void extract(Context context,app x)
    {
        // for debugging
        utility.app_dir_default(context);
        // for debugging

        boolean result=utility.extract_apk(x.src,context,x.name);

        // for debugging

          // Now This one is for share when we gonna use It We don't wanna to print the message
          // So Its an job of caller to set it true so message wont gonna print
        if(status.x)
        {
            status.x=false;
            return;
        }
        // for debugging

        if(result)
        {
            utility_message.success(context);
        }
        else
        {
            utility_message.failure(context);

            // for debugging
            //MainActivity.Message_1(" src : "+);
            // for debugging
        }
    }

    private void cardClick(CardView x,app y)
    {
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status.data=y;
                Intent intent=new Intent(context,ap_detail_activity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return first.size();
    }

    // Now this is acting as an basic information means common structure to launch the
    // list of apps
    class holder extends RecyclerView.ViewHolder
    {
        TextView name,id;
        ImageView img;
        ButtonFlat extract,share;
        // this one is for when that whole layout gets clicked
        CardView onclick;
        public holder(@NonNull View itemView) {
            // this one is needed inbuilt method
            super(itemView);

            // start allocating
            name=itemView.findViewById(R.id.txtName);
            id=itemView.findViewById(R.id.txtApk);
            img= itemView.findViewById(R.id.imgIcon);
            extract=itemView.findViewById(R.id.btnExtract);
            share=itemView.findViewById(R.id.btnShare);
            onclick=itemView.findViewById(R.id.app_card);
        }
    }



    // field computation

}
