package FragmentClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.qiva.jamuku.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import AdapterClass.MainProductAdapter;
import AdapterClass.PopularProductAdapter;
import ModelView.MainProductModel;
import ModelView.PopularProductModel;
import RequestHandler.RequestHandler;
import URLs.Server;


public class MainFrag extends Fragment {
    public View view;
    Activity activity;
    RecyclerView popular_product_reyclerview, recycler_main_product;
    ArrayList<PopularProductModel> popularProductModels = new ArrayList<>();
    ArrayList<MainProductModel> mainProductModels = new ArrayList<>();
    PopularProductAdapter popularProductAdapter;
    NestedScrollView main_nestedscrollview;
    ImageButton popular_refresh;

    int page = 1, limit = 10;
    int popular_page = 1, popular_limit = 5;

    RequestHandler requestHandler;
    Server server;

    MainProductAdapter mainProductAdapter;

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    SwipeRefreshLayout main_refresh;

    public MainFrag(Activity activity) {
        // Required empty public constructor
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_main, container, false);
        init();
        logic();

        return this.view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(activity.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mainProductAdapter.searchItem(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    public void init() {
        server = new Server();
        requestHandler = new RequestHandler();
        popular_product_reyclerview = (RecyclerView) this.view.findViewById(R.id.recycler_popular_product);
        recycler_main_product = (RecyclerView) this.view.findViewById(R.id.recycler_main_product);
        main_nestedscrollview = (NestedScrollView) this.view.findViewById(R.id.main_nestedscrollview);
        main_refresh = (SwipeRefreshLayout) this.view.findViewById(R.id.main_refresh);
        popular_refresh = (ImageButton) this.view.findViewById(R.id.popular_refresh);
    }

    public void logic() {
        getMainProduct();
        getPopularProduct();
        main_nestedscrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    getMainProduct();
                }
            }
        });
        main_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainProductModels.clear();
                getMainProduct();
                page = 1;
                main_refresh.setRefreshing(false);
            }
        });
        popular_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popular_page = 1;
                popularProductModels.clear();
                getPopularProduct();
            }
        });
    }

    public void getPopularProduct() {
        @SuppressLint("StaticFieldLeak")
        class GPP extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    parsePopularProduct(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try {
                    res = requestHandler.GET(server.product_request + "?Request=fetchPopularProduct&page=" + popular_page + "&limit=" + popular_limit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        GPP gpp = new GPP();
        gpp.execute();
    }

    public void parsePopularProduct(String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        JSONArray jsonArray = jsonObject.getJSONArray("popular");
        if (jsonArray.length() <= 0) {
            Toast.makeText(activity, "You're in the last page.", Toast.LENGTH_SHORT).show();
        } else {
            popular_page++;
            for (int a = 0; a < jsonArray.length(); a++) {
                JSONObject object = jsonArray.getJSONObject(a);
                if (object.getBoolean("status") == true) {
                    PopularProductModel popularProductModel = new PopularProductModel();
                    popularProductModel.setProdPrice(object.getString("ProdPrice"));
                    popularProductModel.setProdDesc(object.getString("ProdDesc"));
                    popularProductModel.setProdName(object.getString("ProdName"));
                    popularProductModel.setProdPict(object.getString("ProdPict"));
                    popularProductModel.setPID(object.getString("ProdID"));
                    popularProductModel.setProdComm(object.getString("ProdComm"));
                    popularProductModel.setProdLove(object.getString("ProdLove"));
                    popularProductModel.setProdQuantity(object.getString("ProdQuantity"));

                    popularProductModels.add(popularProductModel);
                } else {
                    Toast.makeText(activity, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
            setPopularProductAdapter();
        }
    }

    public void getMainProduct() {
        Toast.makeText(activity, server.url, Toast.LENGTH_SHORT).show();
        @SuppressLint("StaticFieldLeak")
        class GMP extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    parseMainProduct(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try {
                    res = requestHandler.GET(server.product_request + "?Request=fetchProduct&page=" + page + "&limit=" + limit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        GMP gmp = new GMP();
        gmp.execute();
    }

    public void parseMainProduct(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("product");
        if (jsonArray.length() <= 0) {
            Toast.makeText(activity, "You're in the last page.", Toast.LENGTH_SHORT).show();
        } else {
            page++;
            for (int b = 0; b < jsonArray.length(); b++) {
                JSONObject object = jsonArray.getJSONObject(b);
                if (object.getBoolean("status") == true) {

                    MainProductModel mainProductModel = new MainProductModel();
                    mainProductModel.setProdID(object.getString("ProdID"));
                    mainProductModel.setProdName(object.getString("ProdName"));
                    mainProductModel.setProdDesc(object.getString("ProdDesc"));
                    mainProductModel.setProdDate(object.getString("ProdDate"));
                    mainProductModel.setProdPrice(object.getString("ProdPrice"));
                    mainProductModel.setProdPict(object.getString("ProdPict"));
//                    mainProductModel.setProdType(object.getString("ProdType"));
                    mainProductModel.setProdQuantity(object.getString("ProdQuantity"));
                    mainProductModel.setProdWeight(object.getString("ProdWeight"));
                    mainProductModel.setProdLove(object.getString("ProdLove"));
                    mainProductModel.setProdComm(object.getString("ProdComm"));
                    mainProductModels.add(mainProductModel);

                } else {
                    Toast.makeText(activity, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
            setMainProductAdapter();
        }

    }

    public void setPopularProductAdapter() {
        PopularProductAdapter popularProductAdapter = new PopularProductAdapter(activity, popularProductModels);
        popular_product_reyclerview.setAdapter(popularProductAdapter);
        popular_product_reyclerview.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
    }

    public void setMainProductAdapter() {
        mainProductAdapter = new MainProductAdapter(activity, mainProductModels);
        recycler_main_product.setAdapter(mainProductAdapter);
        recycler_main_product.setLayoutManager(new LinearLayoutManager(activity));

    }


}