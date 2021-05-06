package com.qiva.jamuku;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import FragmentClass.ProfileFrag;
import RequestHandler.RequestHandler;
import SharedPref.AuthSession;
import URLs.Server;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    public static CircleImageView PProfilePicture;
    TextView PName;
    TextView PEmail;
    TextView PPhone;
    TextView PAddress;
    RequestHandler requestHandler;
    Server server;
    AuthSession authSession;

    ImageButton PEmail_edit, PPhone_edit, PAddress_edit;


    Dialog dialog;
    private SharedPreferences sharedPreferences;
    String profile_pref = "profile_pref";
    String session = "session";
    private SharedPreferences.Editor editor;
    public HashMap<String, String> param = new HashMap<>();
    Button profile_btn_save;
    EditText profile_edit_value;
    String m_name;
    String m_email;
    String m_phone;
    String m_address;
    String m_profile;

    String views = "";
    String PUsername_view = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        views = getIntent().getStringExtra("view");

        authSession = new AuthSession(this);
        requestHandler = new RequestHandler();
        server = new Server();
        sharedPreferences = getSharedPreferences(profile_pref, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        init();
        logic();
    }


    public void init() {
        PProfilePicture = (CircleImageView) findViewById(R.id.PProfilePicture);
        PName = (TextView) findViewById(R.id.PName);
        PEmail = (TextView) findViewById(R.id.PEmail);
        PAddress = (TextView) findViewById(R.id.PAddress);
        PPhone = (TextView) findViewById(R.id.PPhone);

        PEmail_edit = (ImageButton) findViewById(R.id.PEmail_edit);
        PPhone_edit = (ImageButton) findViewById(R.id.PPhone_edit);
        PAddress_edit = (ImageButton) findViewById(R.id.PAddress_edit);
        if(views.equals("View_As_Person")){
            dialog = new Dialog(ProfileActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
            dialog.setContentView(R.layout.profile_edit_layout);
            PUsername_view = authSession.sharedPreferences.getString(authSession.username, "");
        }else if(views.equals("View_As_Visitor")){
            PEmail_edit.setVisibility(View.GONE);
            PPhone_edit.setVisibility(View.GONE);
            PAddress_edit.setVisibility(View.GONE);
            PUsername_view = getIntent().getStringExtra("PUsername");
        }
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

        if(views.equals("View_As_Person")){
            profile_edit_value = (EditText) dialog.findViewById(R.id.profile_edit_value);
            profile_btn_save = (Button) dialog.findViewById(R.id.profile_btn_save);
            profile_btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getSession()) {
                        case "PAddress_edit":
                            param.put("Name", "PAddress");
                            param.put("Value", profile_edit_value.getText().toString());
                            dialog.dismiss();
                            saveProfileData();
                            break;
                        case "PEmail_edit":
                            param.put("Name", "PEmail");
                            param.put("Value", profile_edit_value.getText().toString());
                            dialog.dismiss();
                            saveProfileData();
                            break;
                        case "PPhone_edit":
                            param.put("Name", "PPhone");
                            param.put("Value", profile_edit_value.getText().toString());
                            dialog.dismiss();
                            saveProfileData();
                            break;
                    }
                    profile_edit_value.setText("");
                }
            });

        }
        PProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                List<String> list = new ArrayList<>();
                list.add("View");
                if(views.equals("View_As_Person")){
                    list.add("Upload New Image");
                }
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, list);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showProfPict(server.img_profile + m_profile);
                                break;
                            case 1:
                                if(views.equals("View_As_Person")){
                                    setSession("PProfilePicture_edit");
                                    CropImage.activity().start(ProfileActivity.this);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
                builder.show();
            }
        });



    }

    public void showProfPict(String url) {

        Dialog dialog = new Dialog(this, R.style.Theme_MaterialComponents_DayNight_Dialog);
        dialog.setContentView(R.layout.prod_pict_viewer);
        ImageView pict = (ImageView) dialog.findViewById(R.id.ProdPict_viewer);
        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(pict);
        dialog.show();
    }

    private String getSession() {
        return sharedPreferences.getString(session, "");
    }

    private void setSession(String s) {
        editor.putString(session, s);
        editor.apply();
    }

    private void delSession() {
        editor.clear();
        editor.commit();
    }


    public void saveProfileData() {
        param.put("Request", "updateProfileData");
        param.put("PUsername", PUsername_view);
        @SuppressLint("StaticFieldLeak")
        class SPD extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ProfileActivity.this, "Uploading profile", null, false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    parseUpdatedProfileData(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try {
                    res = requestHandler.POST(server.profile_request, param);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        SPD spd = new SPD();
        spd.execute();
    }

    private void parseUpdatedProfileData(String json) throws JSONException {
//        Toast.makeText(this, json, Toast.LENGTH_SHORT).show();
        Log.d("UploadImage", json);
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("updateProfileData");
        if (jsonArray.length() <= 0) {
            Toast.makeText(this, "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show();
        } else {
            for (int b = 0; b < jsonArray.length(); b++) {
                JSONObject object = jsonArray.getJSONObject(b);
                if (object.getBoolean("status") == true) {
                    fetchProfileData();
                } else {
                    Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void fetchProfileData() {
        @SuppressLint("StaticFieldLeak")
        class FPD extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ProfileActivity.this, "Sedang memuat data", null, false, false);
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
                    res = requestHandler.GET(server.profile_request + "?Request=fetchProfileData&PUsername=" + PUsername_view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        FPD fpd = new FPD();
        fpd.execute();
    }

    public void parseProfileData(String json) throws JSONException {
        System.out.println("[ERROR] : "+json);
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("fetchProfileData");
        if (jsonArray.length() <= 0) {
            //Return false
            Toast.makeText(this, "Tidak dapat mengambil data", Toast.LENGTH_SHORT).show();
        } else {
            for (int a = 0; a < jsonArray.length(); a++) {
                JSONObject object = jsonArray.getJSONObject(a);
                if (object.getBoolean("status") == true) {
                    setData(object);
                } else {
                    Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void setData(JSONObject object) throws JSONException {
        authSession.change("profile_picture", object.getString("PProfilePicture"));
        PName.setText(object.getString("PName"));
        PEmail.setText(object.getString("PEmail"));
        PPhone.setText(object.getString("PPhone"));
        PAddress.setText(object.getString("PAddress"));
        m_name = object.getString("PName");
        m_email = object.getString("PEmail");
        m_phone = object.getString("PPhone");
        m_address = object.getString("PAddress");
        m_profile = object.getString("PProfilePicture");
        Picasso.get().load(server.img_profile + object.getString("PProfilePicture")).into(PProfilePicture);
//        Glide.with(ProfileActivity.this).load(server.img_profile + object.getString("PProfilePicture")).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(R.drawable.ic_broken_image).into(PProfilePicture);
        if(views.equals("View_As_Person")){
            //Update sharedpreferences user loged in
            authSession.change("name", object.getString("PName"));
            authSession.change("address", object.getString("PAddress"));
            authSession.change("phone", object.getString("PPhone"));
            authSession.change("email", object.getString("PEmail"));
            //end of [ Update sharedpreferences user loged in ]
            MainActivity.fetchProfileData();
        }
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
                    PProfilePicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_broken_image));
                    param.put("Name","ImageBase64");
                    param.put("Value",BitmapToBase64(bitmap));

                    saveProfileData();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public String BitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] res = baos.toByteArray();

        return String.valueOf(Base64.encodeToString(res, Base64.DEFAULT));
    }
}