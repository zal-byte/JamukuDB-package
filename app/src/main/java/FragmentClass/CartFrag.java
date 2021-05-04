package FragmentClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.icu.text.SearchIterator;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.qiva.jamuku.MainActivity;
import com.qiva.jamuku.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import AdapterClass.CartProductAdapter;
import ModelView.CartModel;
import RequestHandler.RequestHandler;
import SharedPref.AuthSession;
import URLs.Server;


public class CartFrag extends Fragment {
    public View view;
    Activity activity;
    public static ArrayList<CartModel> cartModels = new ArrayList<>();
    static RecyclerView cart_recyclerview;
    SwipeRefreshLayout cart_refresh;
    static RequestHandler requestHandler;
    static Server server;
    static CartProductAdapter cartProductAdapter;
    public static int page = 1;
    static int limit = 10;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    static AuthSession authSession;
    public static Activity context;

    public CartFrag(Activity activity) {
        // Required empty public constructor
        this.activity = activity;
        this.requestHandler = new RequestHandler();
        this.server = new Server();
        this.authSession = new AuthSession(activity);
        this.context = MainActivity.context;
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
        this.view = inflater.inflate(R.layout.fragment_cart, container, false);
        init();
        logic();
        return this.view;
    }

    public void init() {
        cart_recyclerview = (RecyclerView) this.view.findViewById(R.id.cart_recyclerview);
        cart_refresh = (SwipeRefreshLayout) this.view.findViewById(R.id.cart_refresh);
    }

    public void logic() {
        fetchCartData();
        cart_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cartModels.clear();
                if (cartProductAdapter != null) {
                    cartProductAdapter.notifyDataSetChanged();
                }
                page = 1;
                fetchCartData();
                cart_refresh.setRefreshing(false);
            }
        });
    }

    public static void fetchCartData() {
        @SuppressLint("StaticFieldLeak")
        class FCD extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    parseCartData(s);
                    Log.d("ASU", s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                String res = null;
                try {
                    res = requestHandler.GET(server.product_request + "?Request=fetchMyCart&PUsername=" + authSession.sharedPreferences.getString(authSession.username, "") + "&page=" + page + "&limit=" + limit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }
        }
        FCD fcd = new FCD();
        fcd.execute();
    }

    public static void parseCartData(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("cart");
        if (jsonArray.length() <= 0) {

        } else {
            page++;
            for (int b = 0; b < jsonArray.length(); b++) {
                JSONObject object = jsonArray.getJSONObject(b);
                if (object.getBoolean("status") == true) {
                    CartModel cartModel = new CartModel();
                    cartModel.setProdName(object.getString("ProdName"));
                    cartModel.setProdPrice(object.getString("ProdPrice"));
                    cartModel.setProdPict(object.getString("ProdImage"));
                    cartModel.setProdID(object.getString("ProdID"));
                    cartModel.setProdLove(object.getString("ProdLove"));
                    cartModel.setProdComm(object.getString("ProdComm"));
                    cartModel.setProdQuantity(object.getString("ProdQuantity"));
                    cartModel.setKerID(object.getString("KerID"));
                    cartModels.add(cartModel);
                } else {
                    Toast.makeText(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
            setCartAdapter();
        }

    }

    public static void setCartAdapter() {
        cartProductAdapter = new CartProductAdapter(context, cartModels);
        cart_recyclerview.setAdapter(cartProductAdapter);
        cart_recyclerview.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Activity.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    cartProductAdapter.searchItem(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
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
}