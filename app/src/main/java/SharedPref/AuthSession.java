package SharedPref;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

public class AuthSession {

    public Activity activity;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public String pref_name = "pref_name";
    public String username = "username";
    public String name = "name";
    public String email = "email";
    public String phone = "phone";
    public String address = "address";
    public String profile_picture = "profile_picture";
    public String role = "role";
    public String is_login = "is_login";
    public AuthSession(@NotNull Activity activity){
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public void setAuthSession(String username, String name, String email, String phone, String address, String profile_picture,  String role){
        editor.putString(this.username, username);
        editor.putString(this.name, name);
        editor.putString(this.email, email);
        editor.putString(this.phone, phone);
        editor.putString(this.address, address);
        editor.putString(this.profile_picture, profile_picture);
        editor.putString(this.role, role);
        editor.putBoolean(this.is_login, true);
        editor.apply();
    }
    public void delAuthSession(){
        editor.putString(this.username,"");
        editor.putString(this.name, "");
        editor.putString(this.email, "");
        editor.putString(this.phone, "");
        editor.putString(this.address, "");
        editor.putString(this.profile_picture, "");
        editor.putString(this.role, "");
        editor.putBoolean(this.is_login, false);
        editor.apply();
        editor.commit();
    }

    public void change(String name, String value){
        editor.putString(name, value);
        editor.apply();
    }
    public boolean is_Login(){
        return sharedPreferences.getBoolean(is_login, false);
    }


}
