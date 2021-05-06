package AdapterClass;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.qiva.jamuku.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ModelView.PaymentModel;
import SharedPref.AuthSession;
import URLs.Server;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MyViewHolder> {
    ArrayList<PaymentModel> data;
    Activity activity;
    Server url;
    AuthSession authSession;
    public PaymentAdapter(Activity activity, ArrayList<PaymentModel> data) {
        this.data = data;
        this.activity = activity;
        this.url = new Server();
        this.authSession = new AuthSession(activity);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(activity).inflate(R.layout.payment_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Picasso.get().load(url.img_product + data.get(i).ProdPict).into(holder.ProdPict);
        holder.ProdName.setText(data.get(i).ProdName);
        holder.ProdQuantity.setText(data.get(i).ProdQuantity);
        holder.ProdLove.setText(data.get(i).ProdLove);
        holder.ProdComm.setText(data.get(i).ProdComm);
        holder.ProdPrice.setText(data.get(i).ProdPrice);
        holder.card_row_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.payment_dialog_show);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);


                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ProdPict;
        TextView ProdName, ProdLove, ProdComm, ProdPrice, ProdQuantity;
        CardView card_row_payment;
        public MyViewHolder(@NonNull View v) {
            super(v);
            ProdPict = (ImageView) v.findViewById(R.id.ProdPict_view);
            ProdName = (TextView) v.findViewById(R.id.ProdName_view);
            ProdLove = (TextView) v.findViewById(R.id.ProdLove_view);
            ProdComm = (TextView) v.findViewById(R.id.ProdComm_view);
            ProdPrice = (TextView) v.findViewById(R.id.ProdPrice_view);
            ProdQuantity = (TextView) v.findViewById(R.id.ProdQuantity_view);
        }
    }
}
