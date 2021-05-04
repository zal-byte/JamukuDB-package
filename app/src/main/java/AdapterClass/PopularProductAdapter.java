package AdapterClass;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import ModelView.PopularProductModel;
import URLs.Server;

public class PopularProductAdapter extends RecyclerView.Adapter<PopularProductAdapter.MyViewHolder> {
    Activity activity;
    ArrayList<PopularProductModel> data;
    Server server;

    public PopularProductAdapter(Activity activity, ArrayList<PopularProductModel> data) {
        this.activity = activity;
        this.data = data;
        this.server = new Server();
    }

    @NonNull
    @Override
    public PopularProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.main_popular_product, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularProductAdapter.MyViewHolder holder, final int position) {
        Glide.with(activity).load(server.img_product + data.get(position).ProdPict).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_broken_image).into(holder.ProdPict_popular);
        holder.ProdPrice_popular.setText(data.get(position).ProdPrice);
        holder.ProdName_popular.setText(data.get(position).ProdName);
        holder.ProdPict_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activity).showProdPict(server.img_product + data.get(position).ProdPict);
            }
        });
        holder.ProdComm_popular.setText(data.get(position).ProdComm);
        holder.ProdLove_popular.setText(data.get(position).ProdLove);
        holder.ProdQuantity_popular.setText(data.get(position).ProdQuantity);
        holder.card_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ViewproductActivity.class);
                intent.putExtra("ProdID", data.get(position).PID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ProdName_popular, ProdPrice_popular, ProdComm_popular, ProdLove_popular, ProdQuantity_popular;
        ImageView ProdPict_popular;
        CardView card_popular;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ProdName_popular = (TextView) itemView.findViewById(R.id.ProdName_popular);
            ProdPrice_popular = (TextView) itemView.findViewById(R.id.ProdPrice_popular);
            ProdPict_popular = (ImageView) itemView.findViewById(R.id.ProdPict_popular);
            ProdLove_popular = (TextView) itemView.findViewById(R.id.ProdLove_popular);
            ProdComm_popular = (TextView) itemView.findViewById(R.id.ProdComm_popular);
            ProdQuantity_popular = (TextView) itemView.findViewById(R.id.ProdQuantity_popular);
            card_popular = (CardView) itemView.findViewById(R.id.card_popular);
        }
    }
}
