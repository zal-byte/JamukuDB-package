package AdapterClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;
import com.qiva.jamuku.MainActivity;
import com.qiva.jamuku.R;
import com.qiva.jamuku.ViewproductActivity;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import FragmentClass.CartFrag;
import ModelView.CartModel;
import SharedPref.AuthSession;
import URLs.Server;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.MyViewHolder> {
    Activity activity;
    ArrayList<CartModel> data, dataCopy;
    Server server;
    AuthSession authSession;

    public CartProductAdapter(Activity activity, ArrayList<CartModel> data) {
        this.activity = activity;
        this.data = data;
        this.dataCopy = data;
        this.server = new Server();
        this.authSession = new AuthSession(activity);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.cart_row_product, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.ProdName.setText(data.get(position).ProdName);
        holder.ProdPrice.setText(data.get(position).ProdPrice);
        holder.ProdQuantity.setText(data.get(position).ProdQuantity);
        holder.ProdComm.setText(data.get(position).ProdComm);
        holder.ProdLove.setText(data.get(position).ProdLove);
        Picasso.get().load(server.img_product+data.get(position).ProdPict).into(holder.ProdPict);
//        Glide.with(activity).load(server.img_product + data.get(position).ProdPict).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.ProdPict);
        holder.ProdPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activity).showProdPict(server.img_product + data.get(position).ProdPict);
            }
        });
        holder.card_cart_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ViewproductActivity.class);
                intent.putExtra("ProdID", data.get(position).ProdID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });
        holder.card_cart_product.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.Theme_MaterialComponents_DayNight_Dialog);
                List<String> list = new ArrayList<>();
                list.add("Hapus dari keranjang");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, list);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ((MainActivity) activity).addToMyCart(data.get(position).ProdID, authSession.sharedPreferences.getString(authSession.username, ""));
                                ((MainActivity) activity).checkingMyCart(data.get(position).ProdID);

                                Toast.makeText(activity, "Berhasil dihapus dari keranjang.", Toast.LENGTH_SHORT).show();
                                CartFrag.page = 1;
                                CartFrag.cartModels.clear();
                                CartFrag.fetchCartData();

                                break;
                            default:
                                break;
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ProdPict;
        TextView ProdName, ProdPrice, ProdQuantity, ProdComm, ProdLove;
        CardView card_cart_product;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ProdPict = (ImageView) itemView.findViewById(R.id.ProdPict_cart);
            ProdName = (TextView) itemView.findViewById(R.id.ProdName_cart);
            ProdPrice = (TextView) itemView.findViewById(R.id.ProdPrice_cart);
            card_cart_product = (CardView) itemView.findViewById(R.id.card_cart_product);
            ProdQuantity = (TextView) itemView.findViewById(R.id.ProdQuantity_cart);
            ProdLove = (TextView) itemView.findViewById(R.id.ProdLove_cart);
            ProdComm = (TextView) itemView.findViewById(R.id.ProdComm_cart);
        }
    }

    public void searchItem(String text) {
        if (text.isEmpty()) {
            data.clear();
            data.addAll(dataCopy);
        } else {
            ArrayList<CartModel> result = new ArrayList<>();
            text = text.toLowerCase();
            for (CartModel model : dataCopy) {
                if (model.ProdName.contains(text)) {
                    result.add(model);
                }
            }
            data.clear();
            data.addAll(result);
        }
        notifyDataSetChanged();
    }
}
