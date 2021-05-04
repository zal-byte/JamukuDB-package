package com.qiva.jamuku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import AdapterClass.AuthFragAdapter;
import RequestHandler.RequestHandler;
import SharedPref.AuthSession;
import URLs.Server;

public class AuthActivity extends AppCompatActivity {
    TabLayout auth_tabLayout;
    ViewPager auth_viewPager;
    AuthFragAdapter authFragAdapter;
    RequestHandler requestHandler;
    Server server;
    AuthSession authSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        requestHandler = new RequestHandler();
        server = new Server();
        authSession = new AuthSession(AuthActivity.this);

        init();
        logic();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }
    public void init(){
        auth_tabLayout = (TabLayout) findViewById(R.id.auth_tab);
        auth_viewPager = (ViewPager) findViewById(R.id.auth_viewpager);
    }
    public void logic(){
        authFragAdapter = new AuthFragAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, AuthActivity.this);
        auth_viewPager.setAdapter(authFragAdapter);
        auth_tabLayout.setupWithViewPager(auth_viewPager);

        auth_tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                auth_viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    public void submitLoginData(final HashMap<String, String> param){
        @SuppressLint("StaticFieldLeak")
        class SLD extends AsyncTask<Void, Void, String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                loading = ProgressDialog.show(AuthActivity.this, "Login","Sedang login", false, false);
            }
            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    parselogin(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(Void... voids) {
               String res = null;
                try {
                    res = requestHandler.POST(server.sign_ui,param);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        SLD sld = new SLD();
        sld.execute();
    }
    public void submitSignupData(final HashMap<String, String> param){
        @SuppressLint("StaticFieldLeak")
        class SGD extends AsyncTask<Void, Void, String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                loading = ProgressDialog.show(AuthActivity.this, "Mendaftar","Sedang mendaftar.", false, false);
            }
            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                loading.dismiss();
                try{
                    parseSignup(s);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try{
                    res = requestHandler.POST(server.sign_ui, param);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return res;
            }
        }
        SGD sgd = new SGD();
        sgd.execute();
    }
    public void parselogin(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("login");
        for(int b= 0; b < jsonArray.length(); b++){
            JSONObject object = jsonArray.getJSONObject(b);
            if( object.getBoolean("status") == true){
                //Login Berhasil
                authSession.setAuthSession(object.getString("PUsername"),
                        object.getString("PName"),
                        object.getString("PEmail"),
                        object.getString("PPhone"),
                        object.getString("PAddress"),
                        object.getString("PProfilePicture"),
                        object.getString("PRole"));
                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                AuthActivity.this.finish();
            }else{
                Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void parseSignup(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("signup");
        for(int b = 0; b < jsonArray.length(); b++){
            JSONObject object = jsonArray.getJSONObject(b);
            if( object.getBoolean("status") == true){
                Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Fragment from
    public void register(){
        auth_viewPager.setCurrentItem(1);
    }
    public void login(){
        auth_viewPager.setCurrentItem(0);
    }
}