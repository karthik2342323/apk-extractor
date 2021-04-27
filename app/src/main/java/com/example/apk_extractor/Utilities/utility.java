package com.example.apk_extractor.Utilities;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.apk_extractor.MainActivity;

import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.example.apk_extractor.Status.status;

public class utility
{
    public static boolean extract_apk(String src, Context context,String name)
    {
        File dest=app_dir_default(context);
        dest=new File(dest.getPath(),name+".apk");
        boolean success=false;
        File file_src=new File(src);

        // for debugging
        if(dest.exists())
        {
           for(int i=1;i>0;i++)
           {
               dest=new File(app_dir_default(context),name+"_"+i+".apk");
               if(!dest.exists())
               {
                   break;
               }
           }
        }
        // for debugging

        File file_dest=new File(dest.getPath());
        try {
            FileUtils.copyFile(file_src,file_dest);
            success=true;
            // for debugging
            status.temp=file_dest;
            // for debugging
        } catch (IOException e) {
            Log.e(" Error : "," Unable to copy to "+file_src.getPath());
            e.printStackTrace();
        }
        return success;
    }
    public static File  app_dir_default(Context context)
    {
       //File x= new File(Environment.getExternalStorageDirectory() + "/MLManager");
        File dir=new File(Environment.getExternalStorageDirectory()+"/Apk Extractor");

        if(!dir.exists())
        {
           Boolean x=dir.mkdir();
           MainActivity.Message_1(" Status "+x+" Path : "+dir.getPath(),context);
        }
        return dir;
    }



}
