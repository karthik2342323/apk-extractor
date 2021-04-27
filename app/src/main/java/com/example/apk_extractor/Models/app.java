package com.example.apk_extractor.Models;

import android.graphics.drawable.Drawable;

// basic structure of app
public class app
{
   public String src,name,version,id;
   public Drawable icon;
   public boolean system_app;
    public app(String name,String id,String version,String src,Drawable icon,boolean system_app)
    {
        this.src=src;
        this.name=name;
        this.version=version;
        this.id=id;
        this.icon=icon;
        this.system_app=system_app;
    }
}
