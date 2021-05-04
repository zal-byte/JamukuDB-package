package FragmentClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiva.jamuku.MainActivity;
import com.qiva.jamuku.R;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import RequestHandler.RequestHandler;
import SharedPref.AuthSession;
import URLs.Server;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFrag extends Fragment {
    public View view;
    static Activity activity;
    public static  CircleImageView PProfilePicture;
    static TextView PName;
    static TextView PEmail;
    static TextView PPhone;
    static TextView PAddress;
    static RequestHandler requestHandler;
    static Server server;
    static AuthSession authSession;

    ImageButton PEmail_edit, PPhone_edit, PAddress_edit;


    Dialog dialog;
    private SharedPreferences sharedPreferences;
    String profile_pref = "profile_pref";
    String session = "session";
    private SharedPreferences.Editor editor;
    public static  HashMap<String, String> param  = new HashMap<>();
    Button profile_btn_save;
    EditText profile_edit_value;
    static String m_name;
    static String m_email;
    static String m_phone;
    static String m_address;
    static String m_profile;
    public ProfileFrag(Activity activity) {
        // Required empty public constructor
        this.activity = activity;
        this.requestHandler = new RequestHandler();
        this.server = new Server();
        this.authSession = new AuthSession(activity);
        this.sharedPreferences = activity.getSharedPreferences(profile_pref, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_profile, container, false);
        init();
        logic();
        return this.view;
    }

    private String getSession(){
        return sharedPreferences.getString(session, "");
    }
    private void setSession(String s){
        editor.putString(session, s);
        editor.apply();
    }
    private void delSession(){
        editor.clear();
        editor.commit();
    }


    public void init() {
        dialog = new Dialog(activity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        dialog.setContentView(R.layout.profile_edit_layout);

        PProfilePicture = (CircleImageView) this.view.findViewById(R.id.PProfilePicture);
        PName = (TextView) this.view.findViewById(R.id.PName);
        PEmail = (TextView) this.view.findViewById(R.id.PEmail);
        PAddress = (TextView) this.view.findViewById(R.id.PAddress);
        PPhone = (TextView) this.view.findViewById(R.id.PPhone);

        PEmail_edit = (ImageButton) this.view.findViewById(R.id.PEmail_edit);
        PPhone_edit = (ImageButton) this.view.findViewById(R.id.PPhone_edit);
        PAddress_edit = (ImageButton) this.view.findViewById(R.id.PAddress_edit);

    }

    public void logic() {
        fetchProfileData();
//        if (PEmail.getText().toString().contains("UserEmailExample@gmail.com")) {
//            PEmail_edit.setVisibility(View.GONE);
//            PPhone_edit.setVisibility(View.GONE);
//            PAddress_edit.setVisibility(View.GONE);
//        } else {
            PEmail_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSession("PEmail_edit");
                    profile_edit_value.setText(m_email);
                    dialog.show();
                }
            });
            PPhone_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSession("PPhone_edit");
                    profile_edit_value.setText(m_phone);
                    dialog.show();
                }
            });
            PAddress_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSession("PAddress_edit");
                    profile_edit_value.setText(m_address);
                    dialog.show();
                }
            });
        //}


            profile_edit_value = (EditText) dialog.findViewById(R.id.profile_edit_value);
            profile_btn_save = (Button) dialog.findViewById(R.id.profile_btn_save);



            profile_btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getSession().equals("PAddress_edit")){
                        param.put("Name","PAddress");
                        param.put("Value",profile_edit_value.getText().toString());
                        dialog.dismiss();
                        saveProfileData();
                    }else if(getSession().equals("PEmail_edit")){
                        param.put("Name", "PEmail");
                        param.put("Value",profile_edit_value.getText().toString());
                        dialog.dismiss();
                        saveProfileData();
                    }else if(getSession().equals("PPhone_edit")){
                        param.put("Name", "PPhone");
                        param.put("Value", profile_edit_value.getText().toString());
                        dialog.dismiss();
                        saveProfileData();
                    }
                    profile_edit_value.setText("");
                }
            });


            PProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    List<String> list = new ArrayList<>();
                    list.add("View");
                    list.add("Upload new image");
                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, list);
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    ((MainActivity) activity).showProdPict(server.img_profile+m_profile );
                                    break;
                                case 1:
                                    setSession("PProfilePicture_edit");
                                    CropImage.activity().start(activity);
                                    break;
                                default:break;
                            }
                        }
                    });
                    builder.show();
                }
            });
    }

    public static void saveProfileData(){
        param.put("Request", "updateProfileData");
        param.put("PUsername", authSession.sharedPreferences.getString(authSession.username, ""));
        @SuppressLint("StaticFieldLeak")
        class SPD extends AsyncTask<Void, Void, String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                loading = ProgressDialog.show(activity, "Upading profile",null, false, false);
            }
            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                loading.dismiss();
                try{
                    parseUpdatedProfileData(s);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try{
                    res = requestHandler.POST(server.profile_request, param);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return res;
            }
        }
        SPD spd = new SPD();
        spd.execute();
    }
    private static void parseUpdatedProfileData(String json) throws JSONException {
        Log.d("UploadImage", json);
        JSONObject jsonObject = new JSONObject( json);
        JSONArray jsonArray = jsonObject.getJSONArray("updateProfileData");
        if(jsonArray.length() <= 0){
            Toast.makeText(activity, "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show();
        }else{
            for(int b =0; b < jsonArray.length(); b++){
                JSONObject object = jsonArray.getJSONObject(b);
                if( object.getBoolean("status") == true){
                    fetchProfileData();
                }else{
                    Toast.makeText(activity, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static void fetchProfileData() {
        @SuppressLint("StaticFieldLeak")
        class FPD extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(activity, "Sedang memuat data", null, false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    parseProfileData(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try {
                    res = requestHandler.GET(server.profile_request + "?Request=fetchProfileData&PUsername=" + authSession.sharedPreferences.getString(authSession.username, ""));
                } catch (Exception e) {
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
        if (jsonArray.length() <= 0) {
            //Return false
            Toast.makeText(activity, "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show();
        } else {
            for (int a = 0; a < jsonArray.length(); a++) {
                JSONObject object = jsonArray.getJSONObject(a);
                if (object.getBoolean("status") == true) {
                    setData(object);
                } else {
                    Toast.makeText(activity, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static void setData(JSONObject object) throws JSONException {
        PName.setText(object.getString("PName"));
        PEmail.setText(object.getString("PEmail"));
        PPhone.setText(object.getString("PPhone"));
        PAddress.setText(object.getString("PAddress"));
        m_name = object.getString("PName");
        m_email = object.getString("PEmail");
        m_phone = object.getString("PPhone");
        m_address = object.getString("PAddress");
        m_profile = object.getString("PProfilePicture");
        Glide.with(activity).load(server.img_profile + object.getString("PProfilePicture")).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(R.drawable.ic_broken_image).into(PProfilePicture);
    }


}