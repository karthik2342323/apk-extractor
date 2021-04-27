package com.example.apk_extractor.Utilities;

import android.content.Context;

import com.example.apk_extractor.MainActivity;

public class utility_message
{
    static String message;
    public static void success(Context context)
    {
        message=" Successfull extracted under Dir : "+utility.app_dir_default(context);
        MainActivity.Message_1(message,context);
       // return " Successfull extracted under Dir : "+utility.app_dir_default();
    }
    public static void failure(Context context)
    {
        message=" Fail to copy an file Under dir : "+utility.app_dir_default(context);
        MainActivity.Message_1(message,context);
    }
}
