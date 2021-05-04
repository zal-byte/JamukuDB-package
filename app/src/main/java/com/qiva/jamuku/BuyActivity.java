package com.qiva.jamuku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import RequestHandler.RequestHandler;
import Services.BuyProductServices;
import URLs.Server;

public class BuyActivity extends AppCompatActivity {
    String ProdID;
    TextView ProdName, ProdPrice_buy, ProdQuantity_buys;
    TextInputEditText ProdQuantity_buy;
    String ProdName_var, ProdImage_var, ProdPrice_var, ProdQuantity_var;
    String final_price;
    Toolbar buy_toolbar;
    ImageView ProdPict_buy;
    int finalQuantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setSupportActionBar(buy_toolbar);
        Toast.makeText(this, getIntent().getStringExtra("ProdID"), Toast.LENGTH_SHORT).show();
//        String reso = getIntent().getStringExtra("ProdID").isEmpty() ? getIntent().getStringExtra("ProdID") : errors("ProdID Tidak ada");
        ProdID = getIntentData("ProdID");
        Toast.makeText(this, ProdID, Toast.LENGTH_SHORT).show();
        init();
        logic();
//        logic_edt();
    }
    String getIntentData(String name){
        if( getIntent().getStringExtra(name) != null){
            return getIntent().getStringExtra(name);
        }
        return "";
    }
    String errors(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        this.finish();
        return null;
    }

    void logic_edt() {
        ProdQuantity_buy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count <= 0) {
                    ProdPrice_buy.setText(ProdPrice_var);
                } else {
                    if (Integer.parseInt(String.valueOf(s)) > Integer.parseInt(ProdQuantity_var)) {
                        //return error because out of stock
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Tidak dapat memesan barang lebih dari stok yang ditentukan", Snackbar.LENGTH_LONG).show();
                        ProdQuantity_buy.setText(String.valueOf(ProdQuantity_var));
                    } else {
                        finalQuantity = Integer.parseInt(String.valueOf(s));
                        final_price = String.valueOf(Integer.parseInt(ProdPrice_var) * finalQuantity);
                        ProdPrice_buy.setText(final_price);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void init() {
        buy_toolbar = (Toolbar) findViewById(R.id.buy_toolbar);
        ProdName = (TextView) findViewById(R.id.ProdName_buy);
        ProdPict_buy = (ImageView) findViewById(R.id.ProdPict_buy);
        ProdPrice_buy = (TextView) findViewById(R.id.ProdPrice_buy);
        ProdQuantity_buy = (TextInputEditText) findViewById(R.id.ProdQuantity_buy);
        ProdQuantity_buys = (TextView) findViewById(R.id.ProdQuantity_buys);
        //    input_price = (EditText) findViewById(R.id.input_price);
        //  ProdPict_buy = (ImageView) findViewById(R.id.ProdPict_buy);

    }

    void logic() {
        LDPL();
    }

    void startBuyService() {
        if (BuyProductServices.isRunning == true) {
            Intent intent = new Intent(BuyActivity.this, BuyProductServices.class);
            stopService(intent);
            System.out.println("[]byte{BuyProductServices : disabled");
        } else {
            Intent intent = new Intent(BuyActivity.this, BuyProductServices.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("ProdID", ProdID);
            startService(intent);
            System.out.println("[]byte{BuyProductServices : enabled");
        }
    }

    //Load data produk lagi
    public void LDPL() {
        @SuppressLint("StaticFieldLeak")
        class ll extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(BuyActivity.this, "Mengambil produk", "Silahkan tunggu", false, false);
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                loading.dismiss();
                try {
                    System.out.println("[]byte{BUY} : " + response);
                    logic_edt();
                    parseResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                Server server = new Server();
                String res = null;
                RequestHandler RH = new RequestHandler();
                try {
                    res = RH.GET(server.product_request + "?Request=fetchProductData_view&ProdID=" + ProdID);
                } catch (Exception e) {
                    System.out.println("[null]byte{Exception} : " + e.toString());
                }
                return res;
            }
        }
        ll LL = new ll();
        LL.execute();
    }

    void parseResponse(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("productView");
        if (jsonArray.length() <= 0) {
            Snackbar.make(getWindow().getDecorView().getRootView(), "Tidak dapat mengambil data produk", Snackbar.LENGTH_LONG).show();
        } else {
            for (int b = 0; b < jsonArray.length(); b++) {
                JSONObject object = jsonArray.getJSONObject(b);
                if (object.getBoolean("status") == true) {
                    ProdName_var = object.getString("ProdName");
                    ProdPrice_var = object.getString("ProdPrice");
                    ProdImage_var = object.getString("ProdImage");
                    ProdQuantity_var = object.getString("ProdQuantity");
                    setData();
                } else {
                    System.out.println("[]byte{parseResponse : " + object.getString("message") + "}");
                }
            }
        }

    }

    void setData() {
        Server server = new Server();
        ProdName.setText(ProdName_var);
        getSupportActionBar().setTitle(ProdName_var);
        ProdPrice_buy.setText(ProdPrice_var);
        ProdQuantity_buys.setText(ProdQuantity_var);
        Glide.with(BuyActivity.this).load(server.img_product + ProdImage_var).placeholder(R.drawable.ic_launcher_foreground).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(ProdPict_buy);
    }

}