package com.qiva.jamuku;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import AdapterClass.PaymentAdapter;
import ModelView.PaymentModel;
import RequestHandler.RequestHandler;
import SharedPref.AuthSession;
import URLs.Server;

public class Payment extends AppCompatActivity {
    NestedScrollView payment_nestedscrollview;
    RecyclerView payment_recycler;
    AppCompatSpinner s_status;
    Server url;
    RequestHandler RH;
    AuthSession authSession;
    ArrayList<PaymentModel> paymentModels = new ArrayList<>();
    PaymentAdapter paymentAdapter;
    ArrayList<String> data_list = new ArrayList<>();
    SwipeRefreshLayout pay_ref;
    int page = 1;
    int limit = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        url = new Server();
        RH = new RequestHandler();
        authSession = new AuthSession(this);
        data_list.add("proses");
        data_list.add("selesai");
        data_list.add("cancel");
        init();
        logic();
    }

    @SuppressLint("StaticFieldLeak")
    public Payment() {
        new AsyncTask<Void, Void, String>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                parse(result);

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                String res = "";
                try {
                    res = RH.GET(url.product_request + "?Request=fetchMyPayment&PUsername=" + authSession.sharedPreferences.getString(authSession.username, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }.execute();

    }

    public void parse(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("fetchMyPayment");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.getBoolean("status")) {
                    PaymentModel paymentModel = new PaymentModel();
                    paymentModel.setProdID(obj.getString("ProdID"));
                    paymentModel.setProdComm(obj.getString("ProdComm"));
                    paymentModel.setProdLove(obj.getString("ProdLove"));
                    paymentModel.setProdDate(obj.getString("ProdDate"));
                    paymentModel.setProdDesc(obj.getString("ProdDesc"));
                    paymentModel.setProdQuantity(obj.getString("ProdQuantity"));
                    paymentModel.setProdPrice(obj.getString("ProdPrice"));
                    paymentModel.setProdPict(obj.getString("ProdPict"));
                    paymentModel.setProdName(obj.getString("ProdName"));
                    paymentModel.setBelID(obj.getString("BelID"));
                    paymentModel.setStatus(obj.getString("Status"));
                    paymentModel.setQuantity(obj.getString("Quantity"));
                    paymentModels.add(paymentModel);
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), obj.getString("message"), Snackbar.LENGTH_LONG).show();
                }
            }
            if (paymentModels != null) {
                //Setting data up
                paymentAdapter = new PaymentAdapter(Payment.this, paymentModels);
                payment_recycler.setAdapter(paymentAdapter);
                payment_recycler.setLayoutManager(new LinearLayoutManager(Payment.this));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        payment_recycler = (RecyclerView) findViewById(R.id.payment_recycler);
        payment_nestedscrollview = (NestedScrollView) findViewById(R.id.payment_nestedscrollview);
        s_status = (AppCompatSpinner) findViewById(R.id.s_status);
        pay_ref = (SwipeRefreshLayout) findViewById(R.id.pay_ref);
    }

    @SuppressLint("StaticFieldLeak")

    public void logic() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Payment.this, android.R.layout.simple_spinner_dropdown_item, data_list);
        s_status.setAdapter(adapter);
        s_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        parse(result);
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected String doInBackground(Void... voids) {
                        String res = "";
                        try {
                            res = RH.GET(url.product_request + "?Request=fetchMyPayment&PUsername=" + authSession.sharedPreferences.getString(authSession.username, "") + "&by=" + data_list.get(position));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return res;
                    }
                }.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ///////////////

        payment_nestedscrollview.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        scrollPressure(result);
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected String doInBackground(Void... voids) {
                        String res = "";
                        try {
                            res = RH.GET(url.product_request + "?Request=fetchMyPayment&PUsername=" + authSession.sharedPreferences.getString(authSession.username, "") + "&page=" + page + "&limit=" + limit);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return res;
                    }
                }.execute();
            }
        });
        ///////////////

        pay_ref.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pay_ref.setRefreshing(false);
                page = 1;
                paymentModels.clear();
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        parse(result);
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected String doInBackground(Void... voids) {
                        String res = "";
                        try {
                            res = RH.GET(url.product_request + "?Request=fetchMyPayment&PUsername=" + authSession.sharedPreferences.getString(authSession.username, "") + "&page=" + page + "&limit=" + limit);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return res;
                    }
                }.execute();
            }
        });
    }

    void scrollPressure(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("fetchMyPayment");

            if (jsonArray.length() <= 0) {
                Toast.makeText(this, "No Payment Request", Toast.LENGTH_SHORT).show();
            } else {
                page++;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (obj.getBoolean("status")) {
                        PaymentModel paymentModel = new PaymentModel();
                        paymentModel.setProdID(obj.getString("ProdID"));
                        paymentModel.setProdComm(obj.getString("ProdComm"));
                        paymentModel.setProdLove(obj.getString("ProdLove"));
                        paymentModel.setProdDate(obj.getString("ProdDate"));
                        paymentModel.setProdDesc(obj.getString("ProdDesc"));
                        paymentModel.setProdQuantity(obj.getString("ProdQuantity"));
                        paymentModel.setProdPrice(obj.getString("ProdPrice"));
                        paymentModel.setProdPict(obj.getString("ProdPict"));
                        paymentModel.setProdName(obj.getString("ProdName"));
                        paymentModel.setBelID(obj.getString("BelID"));
                        paymentModel.setStatus(obj.getString("Status"));
                        paymentModel.setQuantity(obj.getString("Quantity"));
                        paymentModels.add(paymentModel);
                    } else {
                        Snackbar.make(getWindow().getDecorView().getRootView(), obj.getString("message"), Snackbar.LENGTH_LONG).show();
                    }
                }
                if (paymentModels != null) {
                    //Setting data up
                    paymentAdapter = new PaymentAdapter(Payment.this, paymentModels);
                    payment_recycler.setAdapter(paymentAdapter);
                    payment_recycler.setLayoutManager(new LinearLayoutManager(Payment.this));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}