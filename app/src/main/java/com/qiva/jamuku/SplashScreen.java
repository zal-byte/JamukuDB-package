package com.qiva.jamuku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import SharedPref.AuthSession;

public class SplashScreen extends AppCompatActivity {
    ConnectivityManager connectivityManager;
    AlertDialog.Builder builder;
    AuthSession authSession;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        builder = new AlertDialog.Builder(SplashScreen.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        authSession = new AuthSession(SplashScreen.this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if( authSession.is_Login() == true ){
                    intent = new Intent(SplashScreen.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    SplashScreen.this.finish();
                }else if( authSession.is_Login() == false ){
                    intent = new Intent(SplashScreen.this, AuthActivity.class );
                    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity( intent );
                    SplashScreen.this.finish();
                }
            }
        }, 3000);
    }
}