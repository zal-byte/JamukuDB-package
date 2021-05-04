package AdapterClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiva.jamuku.MainActivity;
import com.qiva.jamuku.R;
import com.qiva.jamuku.ViewproductActivity;

import java.util.ArrayList;
import java.util.List;

import ModelView.MainProductModel;
import SharedPref.AuthSession;
import URLs.Server;

public class MainProductAdapter extends RecyclerView.Adapter<MainProductAdapter.MyViewHolder> {
    Activity activity;
    ArrayList<MainProductModel> data, dataCopy;
    Server server;
    final int EMPTY_VIEW = 77777;
    AuthSession authSession;

    public MainProductAdapter(Activity activity, ArrayList<MainProductModel> data) {
        this.activity = activity;
        this.data = data;
        this.dataCopy = data;
        this.server = new Server();
        this.authSession = new AuthSession(activity);
    }

    @NonNull
    @Override
    public MainProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case EMPTY_VIEW:
                return new MyViewHolder(LayoutInflater.from(activity).inflate(R.layout.nothing_yet, parent, false), viewType);
            default:
                return new MyViewHolder(LayoutInflater.from(activity).inflate(R.layout.main_row_product_v, parent, false), viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (data.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull MainProductAdapter.MyViewHolder holder, final int position) {
        if (getItemViewType(position) == EMPTY_VIEW) {
            holder.nothingPrimary.setText("No Data Yet");
            holder.nothingSecondary.setText("There's no data :(");
            holder.nothingPrimary.setCompoundDrawablesWithIntrinsicBounds(null, activity.getResources().getDrawable(R.drawable.ic_error), null, null);
        } else {
    
            Glide.with(activity).load(server.img_product + data.get(position).ProdPict).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_broken_image).into(holder.ProdPict);

            holder.ProdName.setText(data.get(position).ProdName);
            holder.ProdQuantity.setText(data.get(position).ProdQuantity);
            holder.ProdPrice.setText(data.get(position).ProdPrice);
            holder.ProdPict.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) activity).showProdPict(server.img_product + data.get(position).ProdPict);
                }
            });
            holder.ProdComm.setText(data.get(position).ProdComm);
            holder.ProdLove.setText(data.get(position).ProdLove);
            holder.card_row_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ViewproductActivity.class);
                    intent.putExtra("ProdID", data.get(position).ProdID);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                }
            });
            holder.card_row_product.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    List<String> list = new ArrayList<>();
                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, list);
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    ((MainActivity) activity).addToMyCart(data.get(position).ProdID, authSession.sharedPreferences.getString(authSession.username, ""));
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    ((MainActivity) activity).checkingMyCart(data.get(position).ProdID);
                    if (((MainActivity) activity).getBool() == false) {
                        list.clear();
                        list.add("Tambah ke Keranjang");
                        builder.show();
                    } else {
                        list.clear();
                        list.add("Hapus dari Keranjang");
                        builder.show();
                    }
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ProdPict;
        TextView ProdName, ProdPrice, ProdQuantity, ProdComm, ProdLove;
        TextView nothingPrimary, nothingSecondary;
        CardView card_row_product;

        public MyViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == EMPTY_VIEW) {
                nothingPrimary = (TextView) itemView.findViewById(R.id.nothingPrimary);
                nothingSecondary = (TextView) itemView.findViewById(R.id.nothingSecondary);
            } else {
                ProdPict = (ImageView) itemView.findViewById(R.id.ProdPict_view);
                ProdName = (TextView) itemView.findViewById(R.id.ProdName_view);
                ProdPrice = (TextView) itemView.findViewById(R.id.ProdPrice_view);
                ProdQuantity = (TextView) itemView.findViewById(R.id.ProdQuantity_view);
                ProdLove = (TextView) itemView.findViewById(R.id.ProdLove_view);
                ProdComm = (TextView) itemView.findViewById(R.id.ProdComm_view);
                card_row_product = (CardView) itemView.findViewById(R.id.card_row_product);
            }

        }
    }

    public void searchItem(String text) {
        if (text.isEmpty()) {
            data.clear();
            data.addAll(dataCopy);
        } else {
            ArrayList<MainProductModel> result = new ArrayList<>();
            text = text.toLowerCase();
            for (MainProductModel model : dataCopy) {
                if (model.ProdName.toLowerCase().contains(text)) {
                    result.add(model);
                }
            }
            data.clear();
            data.addAll(result);
        }
        notifyDataSetChanged();
    }
}
