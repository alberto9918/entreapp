package eu.visiton.app.ui.badges.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import eu.visiton.app.R;
import eu.visiton.app.responses.BadgePoiResponse;

import java.util.List;

public class PoisAdapter extends RecyclerView.Adapter<PoisAdapter.ViewHolder> {
    private Context ctx;
    private List<BadgePoiResponse> items;
    private final BadgeDetailListener mListener;

    public PoisAdapter(Context ctx, List<BadgePoiResponse> items, BadgeDetailListener mListener) {
        this.ctx = ctx;
        this.items = items;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.badge_detail_poi_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = items.get(position);
        holder.name.setText(holder.mItem.getName());

        Glide.with(ctx)
                .load(holder.mItem.getCoverImage())
                .apply(new RequestOptions().centerCrop())
                .into(holder.coverImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView coverImage;
        public final TextView name;
        public final View mView;
        public BadgePoiResponse mItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            coverImage = itemView.findViewById(R.id.badge_detail_poi_cover);
            name = itemView.findViewById(R.id.badge_detail_poi_title);
        }
    }
}
