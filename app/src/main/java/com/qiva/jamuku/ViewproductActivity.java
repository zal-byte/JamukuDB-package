package com.qiva.jamuku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import AdapterClass.CommentProductAdapter;
import ModelView.CommentProductModel;
import ModelView.MainProductModel;
import ModelView.ProductViewModel;
import RequestHandler.RequestHandler;
import SharedPref.AuthSession;
import URLs.Server;

public class ViewproductActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView ProdImage;

    RecyclerView comment_Recyclerview;
    RequestHandler requestHandler;
    Server server;
    AuthSession authSession;

    ArrayList<ProductViewModel> productViewModels = new ArrayList<>();
    ArrayList<CommentProductModel> commentProductModels = new ArrayList<>();


    TextView ProdName, ProdDesc, ProdPrice, ProdType, ProdWeight, ProdQuantity, ProdComm_views, ProdLove_views;

    String ProdID_string;
    int comment_page = 1, comment_limit = 10;

    EditText input_comment;
    ImageButton btn_send_comment;
    HashMap<String, String> param = new HashMap<>();
    CommentProductAdapter commentProductAdapter;
    CommentProductModel commentProductModel;

    FloatingActionButton ButtonLike;
    HashMap<String, String> doLikeParam = new HashMap<>();


    //Buy activity
    Button btn_buy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewproduct);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ProdID_string = getIntent().getStringExtra("ProdID");
        init();
        logic();
    }

    ImageButton btn_add_to_cart;

    ///////////////////////BAGIAN PEMBELIAN PRODUK////////////////////////////////////////////////
    //Dibikin dialog dan memasukan jumlah barang yang akan dipesan
    //Default alamat nya bawaan dari profile
    //Sistem check out ? di cek sama petugas

    //Here you go !
    //Layout dialog_beli.xml [ RelativeLayout ]
    Dialog dialog_beli;
    int finalQuantity = 0;
    String final_price;

    void beli_bang() {
        TextInputEditText beli_ProdQuantity_view, beli_Address, beli_ProdQuantity, beli_ProdPrice_view, beli_ProdPrice;
        ImageView beli_ProdImage;
        TextView beli_ProdName;
        FloatingActionButton beli_btn;

        dialog_beli = new Dialog(ViewproductActivity.this, R.style.Theme_MaterialComponents_DayNight_Dialog);
        dialog_beli.setContentView(R.layout.dialog_beli);

        beli_ProdQuantity_view = (TextInputEditText) dialog_beli.findViewById(R.id.beli_ProdQuantity_view);
        beli_ProdQuantity = (TextInputEditText) dialog_beli.findViewById(R.id.beli_ProdQuantity);
        beli_Address = (TextInputEditText) dialog_beli.findViewById(R.id.beli_Address);
        beli_ProdImage = (ImageView) dialog_beli.findViewById(R.id.beli_ProdImage);
        beli_ProdName = (TextView) dialog_beli.findViewById(R.id.beli_ProdName);
        beli_ProdPrice_view = (TextInputEditText) dialog_beli.findViewById(R.id.beli_ProdPrice_view);
        beli_ProdPrice = (TextInputEditText) dialog_beli.findViewById(R.id.beli_ProdPrice);
        beli_btn = (FloatingActionButton) dialog_beli.findViewById(R.id.beli_btn);

        //Tampilkan data

        if (productViewModels != null) {
            beli_ProdQuantity_view.setText(productViewModels.get(0).ProdQuantity);
            beli_Address.setText(authSession.sharedPreferences.getString(authSession.address, ""));
            beli_ProdPrice_view.setText(productViewModels.get(0).ProdPrice);
            beli_ProdName.setText(productViewModels.get(0).ProdName);
            //This is a glide not a glitch !
            Glide.with(ViewproductActivity.this).load(server.img_product + productViewModels.get(0).ProdPict).placeholder(R.drawable.ic_launcher_foreground).skipMemoryCache(true).into(beli_ProdImage);
            // end of [ This is a glide not a glitch ! ]
            beli_ProdQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count <= 0) {
                        beli_ProdPrice.setText(productViewModels.get(0).ProdPrice);
                    } else {
                        if (Integer.parseInt(String.valueOf(s)) > Integer.parseInt(productViewModels.get(0).ProdQuantity)) {
                            //Out of stock
                            Snackbar.make(getWindow().getDecorView().getRootView(), "Out of stock", Snackbar.LENGTH_LONG).show();
                            beli_ProdQuantity_view.setText(productViewModels.get(0).ProdQuantity);
                        } else {
                            finalQuantity = Integer.parseInt(productViewModels.get(0).ProdQuantity);
                            final_price = String.valueOf(Integer.parseInt(productViewModels.get(0).ProdPrice) * finalQuantity);
                            beli_ProdPrice.setText(String.valueOf(final_price));
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            beli_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (beli_ProdQuantity.getText().toString().isEmpty()) {
                        Toast.makeText(ViewproductActivity.this, "Silahkan masukan jumlah barang yang akan dipesan", Toast.LENGTH_SHORT).show();
                    } else {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("Request", "buyProduct");
                        params.put("PUsername", authSession.sharedPreferences.getString(authSession.username, ""));
                        params.put("ProdID", productViewModels.get(0).ProdID);
                        params.put("ProdImage", productViewModels.get(0).ProdPict);
                        params.put("Quantity", beli_ProdQuantity.getText().toString());
                        params.put("Address", authSession.sharedPreferences.getString(authSession.address, ""));
                        new AsyncTask<Void, Void, String>() {
                            ProgressDialog loading;

                            @SuppressLint("StaticFieldLeak")
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                loading = ProgressDialog.show(ViewproductActivity.this, "Memesan", "Memesan produk", false, false);
                            }

                            @Override
                            protected void onPostExecute(String response) {
                                super.onPostExecute(response);
                                parseResponse(response);
                                loading.dismiss();
                            }

                            @Override
                            protected String doInBackground(Void... voids) {
                                String res = null;
                                try {
                                    RequestHandler rh = new RequestHandler();
                                    res = rh.POST(server.product_request, params);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return res;
                            }
                        }.execute();
                    }
                }
            });
        }

        //end of Tampilkan data
        //Tampilkan
        if (dialog_beli != null) {
            dialog_beli.show();
        }

    }
    public Dialog ccd;
    public void as(){
        ccd = new Dialog(ViewproductActivity.this);
        ccd.setCancelable(false);
        ccd.setCanceledOnTouchOutside(false);

    }

    public void parseResponse(String json) {
        if (json == null) {
            Toast.makeText(this, "Null Response", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("buyProduct");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Snackbar.make(getWindow().getDecorView().getRootView(), object.getString("message"), Snackbar.LENGTH_LONG).show();
                    if (object.getBoolean("status") == true) {
                        dialog_beli.dismiss();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //////////////////////END OF BAGIAN PEMBELIAN PRODUK//////////////////////////////////////////
    public void init() {
        requestHandler = new RequestHandler();
        server = new Server();
        authSession = new AuthSession(ViewproductActivity.this);

        toolbar = (Toolbar) findViewById(R.id.view_toolbar);
        ProdImage = (ImageView) findViewById(R.id.ProdImage);
        ProdName = (TextView) findViewById(R.id.ProdName);
        ProdDesc = (TextView) findViewById(R.id.ProdDesc);
        ProdQuantity = (TextView) findViewById(R.id.ProdQuantity);
        ProdWeight = (TextView) findViewById(R.id.ProdWeight);
        ProdType = (TextView) findViewById(R.id.ProdType);
        ProdPrice = (TextView) findViewById(R.id.ProdPrice);
        ProdComm_views = (TextView) findViewById(R.id.ProdComm_views);
        ProdLove_views = (TextView) findViewById(R.id.ProdLove_views);

        input_comment = (EditText) findViewById(R.id.input_comment);
        btn_send_comment = (ImageButton) findViewById(R.id.btn_send_comment);

        comment_Recyclerview = (RecyclerView) findViewById(R.id.comment_Recyclerview);

        ButtonLike = (FloatingActionButton) findViewById(R.id.ButtonLike);
        btn_add_to_cart = (ImageButton) findViewById(R.id.btn_add_to_cart);

        //Buy activity
        btn_buy = (Button) findViewById(R.id.btn_buy);
    }

    public void logic() {
        fetchProductData();
        fetchProductComment();
        checkingLike();
        checkingMyCart();
        btn_send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_comment.getText().toString().length() <= 0) {
                    Toast.makeText(ViewproductActivity.this, "Komentar tidak bisa diisi kosong.", Toast.LENGTH_SHORT).show();
                } else {
                    sendComment(input_comment.getText().toString());
                    input_comment.setText("");
                }
            }
        });
        ButtonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLike();
            }
        });
        btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToMyCart(ProdID_string, authSession.sharedPreferences.getString(authSession.username, ""));
            }
        });

        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ViewproductActivity.this.startActivity(new Intent(ViewproductActivity.this, BuyActivity.class).putExtra("ProdID", ProdID_string));
                beli_bang();
            }
        });
    }

    void checkingMyCart() {
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
                    res = requestHandler.GET(server.product_request + "?Request=checkingMyCart&ProdID=" + ProdID_string + "&PUsername=" + authSession.sharedPreferences.getString(authSession.username, ""));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        CMC cmc = new CMC();
        cmc.execute();
    }

    void parseCheckingMyCart(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("checkingMyCart");
        for (int a = 0; a < jsonArray.length(); a++) {
            JSONObject obj = jsonArray.getJSONObject(a);
            if (obj.getBoolean("status") == true) {
                //
                if (obj.getString("message").equals("produk_sudah_dikeranjang")) {
                    btn_add_to_cart.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_cart));
                } else {
                    Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } else {
                //
                if (obj.getString("message").equals("produk_belum_dikeranjang")) {
                    btn_add_to_cart.setImageDrawable(getResources().getDrawable(R.drawable.ic_tambah_keranjang));
                } else {
                    Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void addToMyCart(String ProdID, String PUsername) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("Request", "addToMyCart");
        params.put("ProdID", ProdID);
        params.put("PUsername", PUsername);

        @SuppressLint("StaticFieldLeak")
        class ATMC extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewproductActivity.this, "Menambahkan", null, false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    parseAddToMyCart(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try {
                    RequestHandler RH = new RequestHandler();
                    Server server = new Server();
                    res = RH.POST(server.product_request, params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        ATMC atmc = new ATMC();
        atmc.execute();
    }

    public void parseAddToMyCart(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("addToMyCart");
        for (int b = 0; b < jsonArray.length(); b++) {
            JSONObject obj = jsonArray.getJSONObject(b);
            if (obj.getBoolean("status") == true) {
                Snackbar.make(ViewproductActivity.this.getWindow().getDecorView().getRootView(), obj.getString("message"), Snackbar.LENGTH_LONG).show();
                checkingMyCart();
            } else {
                checkingMyCart();
                Snackbar.make(ViewproductActivity.this.getWindow().getDecorView().getRootView(), obj.getString("message"), Snackbar.LENGTH_LONG).show();
            }
        }
    }


    public void doLike() {
        //doLike ini bersamaan dengan doUnlike jika postingan ini sudah disukai.
        doLikeParam.put("PUsername", authSession.sharedPreferences.getString(authSession.username, ""));
        doLikeParam.put("ProdID", ProdID_string);
        doLikeParam.put("Request", "postLike");
        @SuppressLint("StaticFieldLeak")
        class CIL extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    parseDoLike(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try {
                    res = requestHandler.POST(server.product_request, doLikeParam);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        CIL cil = new CIL();
        cil.execute();
    }

    public void parseDoLike(String json) throws JSONException {

        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("doLike");
        if (jsonArray.length() <= 0) {
            Toast.makeText(this, "Respon kosong", Toast.LENGTH_SHORT).show();
        } else {
            for (int b = 0; b < jsonArray.length(); b++) {
                JSONObject object = jsonArray.getJSONObject(b);
                if (object.getBoolean("status") == true) {
                    checkingLike();
                    Snackbar.make(getWindow().getDecorView().getRootView(), object.getString("message"), Snackbar.LENGTH_LONG).show();
                } else {
                    checkingLike();
                    Snackbar.make(getWindow().getDecorView().getRootView(), object.getString("message"), Snackbar.LENGTH_LONG).show();
                }
            }
        }

    }

    public void sendComment(String value) {
        param.put("PUsername", authSession.sharedPreferences.getString(authSession.username, ""));
        param.put("PName", authSession.sharedPreferences.getString(authSession.name, ""));
        param.put("KMessage", value);
        param.put("PProfilePicture", authSession.sharedPreferences.getString(authSession.profile_picture, ""));
        param.put("ProdID", ProdID_string);
        Date date = new Date();
        String real_date = String.valueOf(android.text.format.DateFormat.format("yy-MM-dd", date));
        param.put("KDate", real_date);
        param.put("Request", "sendComment");

        @SuppressLint("StaticFieldLeak")
        class SD extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    parseCommentResponse(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try {
                    res = requestHandler.POST(server.product_request, param);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        SD sd = new SD();
        sd.execute();
    }

    public void parseCommentResponse(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("sendComment");
        if (jsonArray.length() <= 0) {
            Toast.makeText(this, "Tidak ada response.", Toast.LENGTH_SHORT).show();
        } else {
            for (int b = 0; b < jsonArray.length(); b++) {
                JSONObject object = jsonArray.getJSONObject(b);
                if (object.getBoolean("status") == true) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), object.getString("message"), Snackbar.LENGTH_LONG).show();
//                    Date date = new Date();
//                    String real_date = android.text.format.DateFormat.format("yy-MM-dd", date).toString();
//                    commentProductModel.setKDate(real_date);
//                    commentProductModel.setKMessage(param.get("KMessage"));
//                    commentProductModel.setPUsername(authSession.sharedPreferences.getString(authSession.username, ""));
//                    commentProductModel.setPName(authSession.sharedPreferences.getString(authSession.name, ""));
//                    commentProductModel.setPProfilePicture(authSession.sharedPreferences.getString(authSession.profile_picture, ""));
//                    commentProductModel.setProdID(ProdID_string);
//
//                    commentProductModels.add(commentProductModel);
//
//                    commentProductAdapter.addNewComment(commentProductModels);
                    commentProductModels.clear();
                    comment_page = 1;
                    fetchProductComment();
                } else {
                    Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void showProfPict(String url) {

        Dialog dialog = new Dialog(this, R.style.Theme_MaterialComponents_DayNight_Dialog);
        dialog.setContentView(R.layout.prod_pict_viewer);
        ImageView pict = (ImageView) dialog.findViewById(R.id.ProdPict_viewer);
        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(pict);
        dialog.show();
    }

    public void fetchProductComment() {
        @SuppressLint("StaticFieldLeak")
        class FPC extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    parseProductComment(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try {
                    res = requestHandler.GET(server.product_request + "?Request=getComment&ProdID=" + ProdID_string + "&page=" + comment_page + "&limit=" + comment_limit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        FPC fpc = new FPC();
        fpc.execute();
    }

    public void parseProductComment(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("getComment");
        if (jsonArray.length() <= 0) {
            //Coba dibikin layout "Belum ada komentar"
        } else {
            comment_page++;
            for (int a = 0; a < jsonArray.length(); a++) {
                JSONObject object = jsonArray.getJSONObject(a);
                if (object.getBoolean("status") == true) {
                    commentProductModel = new CommentProductModel();
                    commentProductModel.setProdID(object.getString("ProdID"));
                    commentProductModel.setPProfilePicture(object.getString("PProfilePicture"));
                    commentProductModel.setPName(object.getString("PName"));
                    commentProductModel.setKID(object.getString("KID"));
                    commentProductModel.setKMessage(object.getString("KMessage"));
                    commentProductModel.setKDate(object.getString("KDate"));
                    commentProductModel.setPUsername(object.getString("PUsername"));
                    commentProductModels.add(commentProductModel);
                } else {
                    Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        }
        setCommentData();
    }

    public void checkingLike() {
        @SuppressLint("StaticFieldLeak")
        class CL extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    parseCheckingLike(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try {
                    res = requestHandler.GET(server.product_request + "?Request=checkingLike&ProdID=" + ProdID_string + "&PUsername=" + authSession.sharedPreferences.getString(authSession.username, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        CL cl = new CL();
        cl.execute();
    }

    public void parseCheckingLike(String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        JSONArray jsonArray = jsonObject.getJSONArray("checkingLike");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            if (object.getBoolean("status") == true) {
                ButtonLike.setIconDrawable(getResources().getDrawable(R.drawable.ic_after_love));
            } else {
                ButtonLike.setIconDrawable(getResources().getDrawable(R.drawable.ic_before_love));
            }
        }
    }

    public void setCommentData() {
        commentProductAdapter = new CommentProductAdapter(ViewproductActivity.this, commentProductModels);
        comment_Recyclerview.setAdapter(commentProductAdapter);
        comment_Recyclerview.setLayoutManager(new LinearLayoutManager(ViewproductActivity.this));
    }

    public void fetchProductData() {
        @SuppressLint("StaticFieldLeak")
        class FPD extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewproductActivity.this, "Memuat data", null, false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    parseProductData(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try {
                    res = requestHandler.GET(server.product_request + "?Request=fetchProductData_view&ProdID=" + ProdID_string);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        FPD fpd = new FPD();
        fpd.execute();
    }

    public void parseProductData(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("productView");
        if (jsonArray.length() <= 0) {
            Toast.makeText(this, "Tidak dapat mengambil data.", Toast.LENGTH_SHORT).show();
        } else {
            for (int a = 0; a < jsonArray.length(); a++) {
                JSONObject object = jsonArray.getJSONObject(a);
                if (object.getBoolean("status") == true) {
                    ProductViewModel productViewModel = new ProductViewModel();
                    productViewModel.setProdID(object.getString("ProdID"));
                    productViewModel.setProdName(object.getString("ProdName"));
                    productViewModel.setProdDesc(object.getString("ProdDesc"));
                    productViewModel.setProdPict(object.getString("ProdImage"));
//                    productViewModel.setProdType(object.getString("ProdType"));
                    productViewModel.setProdWeight(object.getString("ProdWeight"));
                    productViewModel.setProdDate(object.getString("ProdDate"));
                    productViewModel.setProdPrice(object.getString("ProdPrice"));
                    productViewModel.setProdQuantity(object.getString("ProdQuantity"));
                    productViewModel.setProdLove(object.getString("ProdLove"));
                    productViewModel.setProdComm(object.getString("ProdComm"));

                    productViewModels.add(productViewModel);

                } else {
                    Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
            setData();
        }
    }

    public void setData() {
        Glide.with(ViewproductActivity.this).load(server.img_product + productViewModels.get(0).ProdPict).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(ProdImage);
        ProdName.setText(productViewModels.get(0).ProdName);
        ProdDesc.setText(productViewModels.get(0).ProdDesc);
        ProdPrice.setText(productViewModels.get(0).ProdPrice);
//        ProdType.setText(productViewModels.get(0).ProdType);
        ProdWeight.setText(productViewModels.get(0).ProdWeight);
        ProdQuantity.setText(productViewModels.get(0).ProdQuantity);
        ProdComm_views.setText(productViewModels.get(0).ProdComm);
        ProdLove_views.setText(productViewModels.get(0).ProdLove);
    }
}