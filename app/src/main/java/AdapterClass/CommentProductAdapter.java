package AdapterClass;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiva.jamuku.ProfileActivity;
import com.qiva.jamuku.R;
import com.qiva.jamuku.ViewproductActivity;

import java.util.ArrayList;

import ModelView.CommentProductModel;
import SharedPref.AuthSession;
import URLs.Server;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentProductAdapter extends RecyclerView.Adapter<CommentProductAdapter.MyViewHolder> {
    ArrayList<CommentProductModel> data,dataCopy;
    Activity activity;
    Server server;
    AuthSession authSession;

    public CommentProductAdapter(Activity activity, ArrayList<CommentProductModel> data) {
        this.activity = activity;
        this.data = data;
        this.dataCopy = data;
        this.server = new Server();
        this.authSession = new AuthSession(activity);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(activity).inflate(R.layout.product_comment_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.KDate.setText(data.get(position).KDate);
        holder.KMessage.setText(data.get(position).KMessage);
        holder.PName.setText(data.get(position).PName);
        Glide.with(activity).load(server.img_profile + data.get(position).PProfilePicture).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.PProfilePicture);
        holder.PProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ProfileActivity.class);
                if (data.get(position).PUsername.equals(authSession.sharedPreferences.getString(authSession.username, ""))) {
                    intent.putExtra("view", "View_As_Person");
                } else {
                    intent.putExtra("view", "View_As_Visitor");
                    intent.putExtra("PUsername", data.get(position).PUsername);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });
        holder.PProfilePicture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((ViewproductActivity) activity).showProfPict(server.img_profile + data.get(position).PProfilePicture);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView PName, KDate, KMessage;
        CircleImageView PProfilePicture;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            PName = (TextView) itemView.findViewById(R.id.PName);
            KDate = (TextView) itemView.findViewById(R.id.KDate);
            KMessage = (TextView) itemView.findViewById(R.id.KMessage);
            PProfilePicture = (CircleImageView) itemView.findViewById(R.id.PProfilePicture);
        }
    }
    public void addNewComment(ArrayList<CommentProductModel> value) {
        data.clear();
        if(data.size() <= 0){
            data.addAll(value);
        }
        notifyDataSetChanged();
    }
}
