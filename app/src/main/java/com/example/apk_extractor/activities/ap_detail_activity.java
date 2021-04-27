package com.example.apk_extractor.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.apk_extractor.MainActivity;
import com.example.apk_extractor.R;
import com.example.apk_extractor.Status.status;
import com.example.apk_extractor.Adaptor.app_adaptor;
import com.example.apk_extractor.Models.app;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;

public class ap_detail_activity extends AppCompatActivity
{
    Context context;
    Activity activity;
    ImageView icon;
    TextView name;
    TextView version;
    TextView package_1;
    FloatingActionButton share;
    FloatingActionsMenu menu;

    CardView open,extract,uninstall,google_play;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        activity=this;
        // set the UI
        setContentView(R.layout.activity_app);
        // assign the UI Variables
        allocateResource();

        // Toolbar
        setToolbar();

        // allocate data
        setdata();

        // Set OnClick listener
        setOnClickListener();

    }
    void setToolbar()
    {
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    protected void allocateResource()
    {
        icon=findViewById(R.id.app_icon);
        name=findViewById(R.id.app_name);
        version=findViewById(R.id.app_version);
        package_1=findViewById(R.id.app_apk);
        google_play=findViewById(R.id.id_card);
        open=findViewById(R.id.start_card);
        extract=findViewById(R.id.extract_card);
        uninstall=findViewById(R.id.uninstall_card);
        share=findViewById(R.id.fab_a);
        menu=findViewById(R.id.fab);

    }
    private void setdata()
    {
        icon.setImageDrawable(status.data.icon);
        name.setText(status.data.name);
        version.setText(status.data.version);
        package_1.setText(status.data.id);
    }

    void setOnClickListener()
    {
        googlePlay();
        open();
        extract();
        uninstall();
        share();
    }
    void googlePlay()
    {
        google_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.data.system_app)
                {
                    MainActivity.Message_1(" U cannot Find System Apps on Google play ",context);
                }
                else
                {
                    // First Try with URI i.e  try to request the play store
                    Intent intent;
                    try {
                        intent=new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+status.data.id));
                        context.startActivity(intent);
                    }
                    // Now Any Error occur then use URL i.e web request via any browser
                    catch (Exception e)
                    {
                        intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+status.data.id));
                        context.startActivity(intent);
                    }
                }
            }
        });
        google_play.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData clipData;
                ClipboardManager clipboardManager= (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipData=ClipData.newPlainText("text",status.data.id);
                clipboardManager.setPrimaryClip(clipData);
                MainActivity.Message_1(" ID has been copied",context);
                return false;
            }
        });
    }

    void open()
    {
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.data.system_app)
                {
                    MainActivity.Message_1(" U cannot Open System Apps",context);
                    return;
                }
                Intent intent= getPackageManager().getLaunchIntentForPackage(status.data.id);
                context.startActivity(intent);
            }
        });
    }
    void extract()
    {
        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app_adaptor.extract(context,status.data);
            }
        });
    }
    void uninstall()
    {
       if(status.data.system_app)
       {

           // for debugging
           uninstall.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   MainActivity.Message_1(" System apps cannot be Uninstall ",context);
               }
           });
           // for debugging
       }
       else
       {
           uninstall.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent=new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                   intent.setData(Uri.parse("package:"+status.data.id));
                   intent.putExtra(Intent.EXTRA_RETURN_RESULT,true);
                   // always remember 0 -> for creation , 1-> for execution
                   startActivityForResult(intent,1);
               }
           });
       }
    }

    void share()
    {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_1(context,status.data);
            }
        });
    }
    public static void share_1(Context context,app x)
    {
        // for debugging
        status.x=true;
        // for debugging

        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_SEND);
        app_adaptor.extract(context,x);
        intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(status.temp));
        intent.setType("application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent," Share Apk"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // for our uninstalling code
        if(requestCode==1)
        {
            // THis means App Uninstalled successfull  but Our fragment
            // is holding an app as an cache so what we gonna do is load Main Activity
            if(resultCode==RESULT_OK)
            {
                Intent intent=new Intent(this,MainActivity.class);
                // Now this flag means we are clearing previously loaded activity and launching
                // fresh new Activity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                finish();
                status.quit=true;
            }
        }
    }

    @Override
    public void onBackPressed() {
        menu.collapse();
        super.onBackPressed();
    }
}
