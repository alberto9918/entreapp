package eu.visiton.app.ui.badges.list;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import eu.visiton.app.R;
import eu.visiton.app.responses.BadgeResponse;
import eu.visiton.app.responses.UserResponse;
import eu.visiton.app.retrofit.services.UserService;
import eu.visiton.app.util.SvgSoftwareLayerSetter;
import eu.visiton.app.util.UtilToken;

import java.util.List;

class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.ViewHolder> {
    private final BadgeListener mListener;
    UserResponse user;
    private List<BadgeResponse> data;
    private Context context;
    private UserService service;
    private String jwt;
    private RequestBuilder<PictureDrawable> requestBuilder;


    public BadgesAdapter(Context ctx, List<BadgeResponse> data, BadgeListener mListener) {
        this.data = data;
        this.context = ctx;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.badges_custom_item, viewGroup, false);
        requestBuilder = Glide.with(context).as(PictureDrawable.class).listener(new SvgSoftwareLayerSetter<>());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        if(data != null) {
            jwt = UtilToken.getToken(context);
            viewHolder.mItem = data.get(i);
            Glide.with(context).load(data.get(i).getIcon()).into(viewHolder.icon);
            viewHolder.title.setText(data.get(i).getName());
            viewHolder.body.setText(data.get(i).getDescription());
            viewHolder.points.setText(String.valueOf(data.get(i).getPoints()));
            if (data.get(i).isEarned()) {
                viewHolder.earned.setVisibility(View.VISIBLE);
            }

            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onBadgeClick(v, viewHolder.mItem);
                }
            });
        }
    }

    public void setData(List<BadgeResponse> badgeList){
        this.data = badgeList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(data != null){
            return data.size();
        }else{
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView title, body, points;
        public final ImageView earned;
        public final ImageView icon;
        public BadgeResponse mItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            title = itemView.findViewById(R.id.tv_card_title);
            body = itemView.findViewById(R.id.tv_card_body);
            earned = itemView.findViewById(R.id.iv_earned_icon);
            icon = itemView.findViewById(R.id.iv_badge_icon);
            points = itemView.findViewById(R.id.tv_badge_points);
        }

    }

}
