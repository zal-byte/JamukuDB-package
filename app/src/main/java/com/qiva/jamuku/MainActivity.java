package com.qiva.jamuku;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

import AdapterClass.MainFragAdapter;
import FragmentClass.ProfileFrag;
import RequestHandler.RequestHandler;
import SharedPref.AuthSession;
import URLs.Server;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout main_drawerlayout;
    static NavigationView main_nav_view;
    ActionBarDrawerToggle toggle;
    AuthSession authSession;
    androidx.appcompat.widget.Toolbar toolbar;
    BottomNavigationView main_bottomnavigation_view;
    ViewPager main_viewpager;

    MainFragAdapter mainFragAdapter;
    public static Activity context;
    static String m_profile;
    static String m_name;
    static Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        con = getApplicationContext();
        init();
        logic();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        fetchProfileData();
    }
    public static void setHeaderProperties(){
        ImageView header_image = (ImageView) MainActivity.main_nav_view.getHeaderView(0).findViewById(R.id.header_image);
        TextView header_titles = (TextView) MainActivity.main_nav_view.getHeaderView(0).findViewById(R.id.header_titles);

        header_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("view", "View_As_Person");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        Server server = new Server();
        Glide.with(context).load(server.img_profile+m_profile).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(R.drawable.ic_broken_image).into(header_image);
        header_titles.setText(m_name);
    }


    public static void fetchProfileData(){
        @SuppressLint("StaticFieldLeak")
        class FPD extends AsyncTask<Void, Void, String>{
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                try{
                    parseProfileData(s);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try{
                    RequestHandler requestHandler = new RequestHandler();
                    Server server = new Server();
                    AuthSession authSession = new AuthSession(context);
                    res = requestHandler.GET(server.profile_request+"?Request=fetchProfileData&PUsername="+authSession.sharedPreferences.getString(authSession.username,""));
                }catch(Exception e){
                    e.printStackTrace();
                }
                return res;
            }
        }
        FPD fpd = new FPD();
        fpd.execute();
    }
    public static void parseProfileData(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("fetchProfileData");
        if(jsonArray.length() <=0){
            Toast.makeText(context, "Navigation View Data Error", Toast.LENGTH_SHORT).show();
        }else{
            for(int b = 0; b < jsonArray.length(); b++){
                JSONObject object = jsonArray.getJSONObject(b);
                if(object.getBoolean("status") == true){
                    m_profile = object.getString("PProfilePicture");
                    m_name = object.getString("PName");
                    setHeaderProperties();
                }else{
                    Toast.makeText(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public void init(){
        authSession = new AuthSession(MainActivity.this);
        mainFragAdapter = new MainFragAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,this);
        main_drawerlayout = (DrawerLayout) findViewById(R.id.main_drawer);
        main_nav_view = (NavigationView) findViewById(R.id.main_nav_view);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        main_bottomnavigation_view = (BottomNavigationView) findViewById(R.id.main_bottomnavigation_view);
        main_viewpager = (ViewPager) findViewById(R.id.main_viewpager);
    }
    public void logic(){
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, main_drawerlayout, toolbar, R.string.open, R.string.close);
        main_drawerlayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        main_nav_view.setNavigationItemSelectedListener(this);
        main_viewpager.setAdapter(mainFragAdapter);
        main_viewpager.setCurrentItem(0); //MainFrag

        main_bottomnavigation_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_nav_home:
                        main_viewpager.setCurrentItem(0);
                        break;
                    case R.id.bottom_nav_keranjang:
                        main_viewpager.setCurrentItem(1);
                        break;
                    default:break;
                }
                return false;
            }
        });
    }
    public void showProdPict(String url){

        Dialog dialog = new Dialog(this, R.style.Theme_MaterialComponents_DayNight_Dialog);
        dialog.setContentView(R.layout.prod_pict_viewer);
        ImageView pict = (ImageView) dialog.findViewById(R.id.ProdPict_viewer);
        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(pict);
        dialog.show();
    }
    private void usr_logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        builder.setTitle("Logout?");
        builder.setMessage("Are you sure want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                authSession.delAuthSession();
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    public void checkingMyCart(final String ProdID) {
        @SuppressLint("StaticFieldLeak")
        class CMC extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    parseCheckingMyCart(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try {
                    RequestHandler requestHandler = new RequestHandler();
                    Server server = new Server();
                    res = requestHandler.GET(server.product_request+"?Request=checkingMyCart&ProdID="+ProdID+"&PUsername="+authSession.sharedPreferences.getString(authSession.username,""));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        CMC cmc = new CMC();
        cmc.execute();
    }
    boolean bool = false;

     void parseCheckingMyCart(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("checkingMyCart");
        for (int a = 0; a < jsonArray.length(); a++) {
            JSONObject obj = jsonArray.getJSONObject(a);
            bool = obj.getBoolean("status");
        }
    }
    public boolean getBool(){
        return bool;
    }
    public void addToMyCart(String ProdID, String PUsername){
        final HashMap<String, String> params = new HashMap<>();
        params.put("Request", "addToMyCart");
        params.put("ProdID", ProdID);
        params.put("PUsername", PUsername);
        @SuppressLint("StaticFieldLeak")
        class ATMC extends AsyncTask<Void, Void, String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Menambahkan", null, false, false);
            }
            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                loading.dismiss();
                try{
                    parseAddToMyCart(s);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try{
                    RequestHandler RH = new RequestHandler();
                    Server server = new Server();
                    res = RH.POST(server.product_request, params);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return res;
            }
        }
        ATMC atmc = new ATMC();
        atmc.execute();
    }
    public  void parseAddToMyCart(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("addToMyCart");
        for(int b =0; b < jsonArray.length(); b++){
            JSONObject obj = jsonArray.getJSONObject(b);
            if(obj.getBoolean("status") == true){
                Snackbar.make(MainActivity.this.getWindow().getDecorView().getRootView(), obj.getString("message"), Snackbar.LENGTH_LONG).show();
            }else{
                Snackbar.make(MainActivity.this.getWindow().getDecorView().getRootView(), obj.getString("message"), Snackbar.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.check_payment:
                Toast.makeText(MainActivity.this, "Fitur belum tersedia", Toast.LENGTH_SHORT).show();
                break;
            case R.id.myprofile:
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("view","View_As_Person");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.usr_logout:
                usr_logout();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri imgUri = result.getUri();
                try{
                    InputStream inputStream = getContentResolver().openInputStream(imgUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ProfileFrag.PProfilePicture.setImageBitmap(bitmap);
                    ProfileFrag.param.put("Name", "ImageBase64");
                    ProfileFrag.param.put("Value", BitmapToBase64(bitmap));
                    Log.d("BIT", BitmapToBase64(bitmap));
                    ProfileFrag.saveProfileData();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public String BitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] res = baos.toByteArray();

        return String.valueOf(Base64.encodeToString(res, Base64.DEFAULT));
    }
}