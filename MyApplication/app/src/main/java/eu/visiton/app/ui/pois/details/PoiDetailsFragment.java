package eu.visiton.app.ui.pois.details;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import eu.visiton.app.R;
import eu.visiton.app.responses.CategoryResponse;
import eu.visiton.app.responses.PoiResponse;

import java.util.Objects;


public class PoiDetailsFragment extends Fragment {

    private static String id;
    private PoiResponse poi;
    // private GestureDetector gestureDetector;

    public PoiDetailsFragment() { }
    public PoiDetailsFragment(String poiId) {
        id = poiId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPoiDetails();
        // gestureDetector = new GestureDetector(getContext(), new GestureListener());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poi_details, container, false);

       /* FloatingActionButton fab = v.findViewById(R.id.btn_poi_audioguide);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());*/
    }

    /** Api call to get details of one POI **/
    private void getPoiDetails() {
        /*String jwt = UtilToken.getToken(Objects.requireNonNull(getContext()));
        PoiService service = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);
        Call<PoiResponse> call = service.getPoi(id);

        call.enqueue(new Callback<PoiResponse>() {
            @Override
            public void onResponse(@NonNull Call<PoiResponse> call, @NonNull Response<PoiResponse> response) {
                if (response.code() != 200) {
                    Toast.makeText(getActivity(), "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    poi = response.body();
                    setDataOnView();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PoiResponse> call, @NonNull Throwable t) {
                Log.e("Network Failure", t.getMessage());
                Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    /** Set received data of POI on Layout **/
    private void setDataOnView() {
        View v = getView();
        ((TextView) Objects.requireNonNull(v).findViewById(R.id.tv_poi_name)).setText(Html.fromHtml(poi.getName()));
        //((TextView) Objects.requireNonNull(v).findViewById(R.id.tv_poi_description)).setText(Html.fromHtml(poi.getDescription().getOriginalDescription()));
        // ((TextView) Objects.requireNonNull(v).findViewById(R.id.tv_poi_description)).setText(Html.fromHtml(poi.getDescription().getTranslations()[0.getTranslatedFile()));
        Glide.with(this).load(poi.getCoverImage()).into((ImageView) v.findViewById(R.id.iv_poi_image));

        LinearLayout cg = Objects.requireNonNull(v).findViewById(R.id.cg_categories);
        for (CategoryResponse c: poi.getCategories()) {
            TextView cc = new TextView(Objects.requireNonNull(getContext()));
            //cc.setChipDrawable(ChipDrawable.createFromResource(Objects.requireNonNull(getContext()), R.xml.chip_categories));
            cc.setText(c.getName());
            cg.addView(cc);

        }
    }

    /*
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            
            return true;
        }
    }
    */
}
