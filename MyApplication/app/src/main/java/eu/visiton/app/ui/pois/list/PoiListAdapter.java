package eu.visiton.app.ui.pois.list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import eu.visiton.app.R;
import eu.visiton.app.responses.PoiResponse;
import eu.visiton.app.responses.ResponseContainer;
import eu.visiton.app.responses.UserFavResponse;
import eu.visiton.app.responses.UserResponse;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.PoiService;
import eu.visiton.app.retrofit.services.UserService;
import eu.visiton.app.util.UtilToken;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PoiListAdapter extends RecyclerView.Adapter<PoiListAdapter.ViewHolder> {

    private final PoiListListener mListener;
    private List<PoiResponse> data;
    private Context context;
    private String jwt;

    // private RequestBuilder<PictureDrawable> requestBuilder;


    PoiListAdapter(Context ctx, List<PoiResponse> data, PoiListListener mListener) {
        this.data = data;
        this.context = ctx;
        this.mListener = mListener;
        jwt = UtilToken.getToken(Objects.requireNonNull(context));
    }

    @NonNull
    @Override
    public PoiListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poi_list_custom_item, viewGroup, false);
        // requestBuilder = Glide.with(context).as(PictureDrawable.class).listener(new SvgSoftwareLayerSetter<>());
        return new PoiListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PoiListAdapter.ViewHolder viewHolder, int i) {
        if(data != null) {

            String jwt = UtilToken.getToken(context);
            // private final PoiListListener mListener;
            UserService service = ServiceGenerator.createService(UserService.class, jwt, AuthType.JWT);
            Call<UserResponse> call = service.getUserResponse(UtilToken.getId(context));
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(context, "You have to be logged in", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                    Toast.makeText(context, "You have to be logged in", Toast.LENGTH_SHORT).show();
                }
            });

            viewHolder.mItem = data.get(i);
            viewHolder.imageViewFav.setImageResource(R.drawable.ic_like_empty);
            viewHolder.title.setText(viewHolder.mItem.getName());
            if (viewHolder.mItem.getDistance() != null) {
                viewHolder.distance.setText(Math.round(viewHolder.mItem.getDistance()) + " " + context.getString(R.string.meters));
            }
            if (viewHolder.mItem.getPrice() == 0.0f) {
                viewHolder.price.setText("Gratis");
            } else {
                viewHolder.price.setText("€ " + viewHolder.mItem.getPrice());
            }

            Glide.with(context)
                    .load(viewHolder.mItem.getCoverImage())
                    .apply(new RequestOptions().centerCrop())
                    .into(viewHolder.bgImage);

            viewHolder.bgImage.setOnClickListener(v -> mListener.goPoiDetails(viewHolder.mItem.getId()));

            if (viewHolder.mItem.getFav()) {
                viewHolder.imageViewFav.setImageResource(R.drawable.ic_like_red);
            }

            viewHolder.imageViewFav.setOnClickListener(view -> {
                if (viewHolder.mItem.getFav()) {
                    viewHolder.imageViewFav.setImageResource(R.drawable.ic_like_empty);
                    viewHolder.mItem.setFav(false);
                    deleteFav(viewHolder.mItem.getId());
                } else {
                    viewHolder.mItem.setFav(true);
                    viewHolder.imageViewFav.setVisibility(View.GONE);
                    viewHolder.lottieAnimationView.setVisibility(View.VISIBLE);
                    viewHolder.lottieAnimationView.playAnimation();
                    addFav(viewHolder.mItem.getId());
                }
            });

            viewHolder.lottieAnimationView.setOnClickListener(view -> {
                viewHolder.imageViewFav.setImageResource(R.drawable.ic_like_empty);
                viewHolder.imageViewFav.setVisibility(View.VISIBLE);
                viewHolder.lottieAnimationView.setVisibility(View.GONE);
                viewHolder.mItem.setFav(false);
            });

            viewHolder.distance.setOnClickListener(view -> {
                String coords = viewHolder.mItem.getLoc().getCoordinates().get(0) + "," + viewHolder.mItem.getLoc().getCoordinates().get(1);
                mListener.showGoogleMaps(coords);
            });
        }
    }

    private void addFav(String id) {
        PoiService service = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);
        Call<UserFavResponse> call = service.addPoiFav(id);
        call.enqueue(new Callback<UserFavResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserFavResponse> call, @NonNull Response<UserFavResponse> response) {
                if (response.code() != 200) {
                    Toast.makeText(context, "Request Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserFavResponse> call, @NonNull Throwable t) {
                Log.e("Network Failure", t.getMessage());
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFav(String id) {
        PoiService service = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);
        Call<UserFavResponse> call = service.delPoiFav(id);
        call.enqueue(new Callback<UserFavResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserFavResponse> call, @NonNull Response<UserFavResponse> response) {
                if (response.code() != 200) {
                    Toast.makeText(context, "Request Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserFavResponse> call, @NonNull Throwable t) {
                Log.e("Network Failure", t.getMessage());
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setData(List<PoiResponse> poiList){
        this.data = poiList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        if(data!=null){
            return data.size();
        }else{
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView title, price, distance;
        final ImageView bgImage;
        final ImageView imageViewFav;
        final LottieAnimationView lottieAnimationView;
        PoiResponse mItem;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            title = itemView.findViewById(R.id.poi_list_title);
            price = itemView.findViewById(R.id.poi_price);
            distance = itemView.findViewById(R.id.textViewDistance);
            bgImage = itemView.findViewById(R.id.poi_list_bgImage);
            imageViewFav = itemView.findViewById(R.id.imageViewFavorito);
            lottieAnimationView = itemView.findViewById(R.id.lottieFavorito);
        }

    }
}
