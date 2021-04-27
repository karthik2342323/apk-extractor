package com.example.apk_extractor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ChangedPackages;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.VersionedPackage;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.widget.LinearLayout;
//import android.widget.SearchView;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;

import java.lang.reflect.Array;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.example.apk_extractor.Models.app;
import com.example.apk_extractor.Adaptor.app_adaptor;
import com.example.apk_extractor.Utilities.utility;
import com.example.apk_extractor.Status.status;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


// This one is an very first Activity gonna launch i.e List of Apps
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public Toolbar toolbar;
    public Context context;
    public Activity activity;
    public RecyclerView recyclerView;
    public boolean click=false;
    static List<app> installed=new ArrayList<>();
   static List<app> system=new ArrayList<>();
    List<app> favourite=new ArrayList<>();
    MenuItem menuItem;
    SearchView searchView;
    public Drawer drawer;


    // for temp
    app_adaptor adaptor=new app_adaptor(installed,this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        activity = this;

        // set Toolbar
        setToolbar();
        recyclerView = findViewById(R.id.appList);
        recyclerView.setHasFixedSize(true);

        // set orientation of recycle view
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);

        // request permission Because It require separate execution
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            listOfPermission(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"});
            return;
        }
       // quit();

        // add drawner
       // drawner();

        // app mining
        new appMining().execute();


    }

    void setToolbar() {
         toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" Apk Extractor");
        }

        // THis one gonna add color to top system bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

    }


    public void listOfPermission(String[] permission)
    {
        List<String> denied=new ArrayList<>();
        for(String x:permission)
        {
            if(PermissionChecker.checkSelfPermission(context,x)!=PermissionChecker.PERMISSION_GRANTED)
            {
                denied.add(x);
            }
        }
        // If None of Permission are denied and launch the app
        if(denied.size()==0)
        {
            new appMining().execute();
            return;
        }
        String arr[]=Converter(denied.toArray());

        // for debugging
        /*
        for(int i=0;i<arr.length;i++)
        {
            Message_1(" see This "+i+" : "+arr[i],context);
        }

         */
        // for debugging
        activity.requestPermissions(arr,1);

    }

    public String[] Converter(Object[] x) {
        String z[] = new String[x.length];
        for(int i=0;i<x.length;i++)
        {
            z[i]=x[i].toString();
        }
        return z;
    }



    class appMining extends AsyncTask<Void, String, Void> {

        // Now If we are calling this meaning It will get loaded again
        // so reset the data
        appMining()
        {
            installed=new ArrayList<>();
            system=new ArrayList<>();
        }
        // what task to do
        @Override
        protected Void doInBackground(Void... voids) {


            PackageManager packageManager=getPackageManager();
            List<PackageInfo> packages=packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

            /* Now This will constain mixture of system/installed apps Till Now no implemention
            of Favourite apps
             */

            Collections.sort(packages, new Comparator<PackageInfo>() {
                @Override
                public int compare(PackageInfo o1, PackageInfo o2) {
                    String a=packageManager.getApplicationLabel(o1.applicationInfo).toString().toLowerCase();
                    String b=packageManager.getApplicationLabel(o2.applicationInfo).toString().toLowerCase();
                    return a.compareTo(b);
                }
            });

            for(PackageInfo x:packages)
            {
                // This means name is not " " and ID is not ""
                if(!packageManager.getApplicationLabel(x.applicationInfo).equals("") && !x.packageName.equals(""))
                {
                    // do It for installed apps
                    if(  (x.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0  ) {
                        Log.e(" App  ", " name : " + packageManager.getApplicationLabel(x.applicationInfo)
                                + " ID : " + x.packageName);
                        app y = new app(packageManager.getApplicationLabel(x.applicationInfo).toString(), x.packageName, x.versionName
                                , x.applicationInfo.sourceDir, packageManager.getApplicationIcon(x.applicationInfo), false);
                        installed.add(y);
                    }
                    // else Its system apps
                    else
                    {
                        app y = new app(packageManager.getApplicationLabel(x.applicationInfo).toString(), x.packageName, x.versionName
                                , x.applicationInfo.sourceDir, packageManager.getApplicationIcon(x.applicationInfo), true);
                        system.add(y);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // add drawner
            drawner();

            // launch the default
            adaptor=new app_adaptor(installed,context);
            recyclerView.setAdapter(adaptor);


            // for debugging
            status.what=status.installed;
            // for debugging
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // for debugging
        //Message_1(" See This Working Label 1",context);
        // for debugging


        // If any permission is denied then stop working
        for(int x:grantResults)
        {
            if(x==PackageManager.PERMISSION_DENIED)
            {
                Message_1(" Permission Require for working of certain Function So accept the permission ",context);
                listOfPermission(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"});
                return;
            }
        }
        // else start launching
       // Message_1(" See This Working label 2",context);
        new appMining().execute();



        // for debugging
        /*
        int p=1;
        for(int x:grantResults)
        {
        }

         */
        // for debugging
    }


    public void MkDirFolder()
    {
       utility.app_dir_default(context);
    }


    public void drawner()
    {
        String installed_count="-1",system_count="-1";
        int header=R.drawable.header;
        // meaning atleast one element is present
        if(installed.size()>=1)
        {
            installed_count=Integer.toString(installed.size());
        }
        if(system.size()>=1)
        {
            system_count=Integer.toString(system.size());
        }
        AccountHeader head=new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(header)
                .build();
        DrawerBuilder draw=new DrawerBuilder();
        draw.withActivity(activity);
        draw.withToolbar(toolbar);
        draw.withAccountHeader(head);

        /*
            Label

           1 -> Installed apps
           2 -> System apps
         */

        PrimaryDrawerItem installed=new PrimaryDrawerItem()
                .withName(" Installed Apps")
                .withIcon(GoogleMaterial.Icon.gmd_phone_android)
                .withBadge(installed_count)
                .withIdentifier(1);
        PrimaryDrawerItem system=new PrimaryDrawerItem()
                .withName(" System Apps")
                .withIcon(GoogleMaterial.Icon.gmd_android)
                .withBadge(system_count)
                .withIdentifier(2);

        draw.addDrawerItems(installed,system);
        draw.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
               onDrawerPerformAction(drawerItem.getIdentifier());
                return false;
            }
        });
        drawer=draw.build();


    }

    void onDrawerPerformAction(int x) {

        // installed apps
        if (x == 1)
        {
            adaptor=new app_adaptor(installed,context);
            recyclerView.setAdapter(adaptor);

            // for debugging
            status.what=status.installed;
            // for debugging
        }
        // system apps
        else if(x==2)
        {
            adaptor=new app_adaptor(system,context);
            recyclerView.setAdapter(adaptor);

            // for debugging
            status.what=status.system;
            // for debugging
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {

        if(drawer.isDrawerOpen())
        {
            drawer.closeDrawer();
           // return;
        }
        else if(menuItem.isVisible() && !searchView.isIconified())
        {
            searchView.onActionViewCollapsed();
           // return;
        }
        else
        {
            onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater= getMenuInflater();
        // Menu launching
        inflater.inflate(R.menu.menu_main,menu);
        // get that ID By menu.findItem not findbyView Otherwise It gonna create Null Pointer reference
        menuItem=menu.findItem(R.id.action_search);
        // convert to search view
        searchView= (SearchView) MenuItemCompat.getActionView(menuItem);
        // set onTextQueryListener
        searchView.setOnQueryTextListener(this);
        SearchManager searchManager= (SearchManager) getSystemService(getApplication().SEARCH_SERVICE);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    /*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

     */

    /*
     This one is not require Untill U consider submit
     But for fast job done we ain't gonna press submit button
     for everytime so onQueryTextChange is the best option
     */
    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String x=newText.trim().toLowerCase();
        if(x.equals(""))
        {
            if(status.what.equals(status.installed))
            {
                adaptor=new app_adaptor(installed,context);
            }
            else
            {
                adaptor=new app_adaptor(system,context);
            }
            recyclerView.setAdapter(adaptor);
        }
        // means Its Not plain string
        else
        {
            List<app> first=new ArrayList<>();
            // for installed
            if(status.what.equals(status.installed))
            {
                for(app y:installed)
                {
                    String z=y.name.trim().toLowerCase();
                    if(z.contains(x))
                    {
                        first.add(y);
                    }
                }
            }
            // for system apps
            else
            {
                for(app y:system)
                {
                    String z=y.name.trim().toLowerCase();
                    if(z.contains(x))
                    {
                        first.add(y);
                    }
                }
            }
            adaptor=new app_adaptor(first,context);
            recyclerView.setAdapter(adaptor);
        }

        return false;
    }

    // for debugging
    public static void Message_1(String m,Context context)
    {
        int duration= Toast.LENGTH_LONG;
        Toast t=Toast.makeText(context,m,duration);
        t.show();
    }
    // for debugging
}