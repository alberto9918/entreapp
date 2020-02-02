package com.mario.myapplication.ui.pois.details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.common.util.ArrayUtils;
import com.mario.myapplication.R;
import com.mario.myapplication.materialx.utils.Tools;
import com.mario.myapplication.model.Image;
import com.mario.myapplication.responses.PoiResponse;
import com.mario.myapplication.retrofit.generator.AuthType;
import com.mario.myapplication.retrofit.generator.ServiceGenerator;
import com.mario.myapplication.retrofit.services.PoiService;
import com.mario.myapplication.util.Constantes;
import com.mario.myapplication.util.UtilToken;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetallePoiActivity extends AppCompatActivity {

    private static String id;
    private PoiResponse poi;

    private View parent_view;
    private ViewPager viewPager;
    private LinearLayout layout_dots;
    private AdapterImageSlider adapterImageSlider;
    private ImageView[] dots;
    private TextView tvPrecio, tvDescripcion, tvTitulo, tvInfo, tvReviews;
    AppCompatRatingBar ratingBarPoi;

    private static List<String> array_image_poi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_poi);

        initToolbar();
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        getPoiDetails();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("POI");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {
        layout_dots = findViewById(R.id.layout_dots);
        viewPager = findViewById(R.id.pager);

        ratingBarPoi = findViewById(R.id.rating_poi);
        tvReviews = findViewById(R.id.reviews_poi);
        tvTitulo = findViewById(R.id.titulo_poi);
        tvInfo = findViewById(R.id.info_poi);
        tvPrecio = findViewById(R.id.precio_poi);
        tvDescripcion = findViewById(R.id.descripcion_poi);

        ratingBarPoi.setRating(poi.getStars());
        tvReviews.setText(poi.getStars() + "/5.0");
        tvTitulo.setText(Html.fromHtml(poi.getName()));
        tvInfo.setText(poi.getSchedule() + " ["+poi.getStatus()+"]");
        tvPrecio.setText("€ "+poi.getPrice());
        tvDescripcion.setText(Html.fromHtml(poi.getDescription().getTranslations()[0].getTranslatedFile()));

        adapterImageSlider = new AdapterImageSlider(this, new ArrayList<Image>());

        List<Image> items = new ArrayList<>();
        for (int i=0; i<array_image_poi.size(); i++) {
            String img = array_image_poi.get(i);
            Image obj = new Image();
            obj.image = img;
            items.add(obj);
        }

        adapterImageSlider.setItems(items);
        viewPager.setAdapter(adapterImageSlider);

        // displaying selected image first
        viewPager.setCurrentItem(0);
        addBottomDots(layout_dots, adapterImageSlider.getCount(), 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                addBottomDots(layout_dots, adapterImageSlider.getCount(), pos);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void addBottomDots(LinearLayout layout_dots, int size, int current) {
        dots = new ImageView[size];

        layout_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(ContextCompat.getColor(this, R.color.overlay_dark_10), PorterDuff.Mode.SRC_ATOP);
            layout_dots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current].setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle_poi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /** Api call to get details of one POI **/
    private void getPoiDetails() {
        String jwt = UtilToken.getToken(Objects.requireNonNull(DetallePoiActivity.this));
        PoiService service = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);
        Call<PoiResponse> call = service.getPoi(id);

        call.enqueue(new Callback<PoiResponse>() {
            @Override
            public void onResponse(@NonNull Call<PoiResponse> call, @NonNull Response<PoiResponse> response) {
                if (response.code() != 200) {
                    Toast.makeText(DetallePoiActivity.this, "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    poi = response.body();
                    array_image_poi = new ArrayList<>();
                    array_image_poi.addAll(ArrayUtils.toArrayList(poi.getImages()));
                    initComponent();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PoiResponse> call, @NonNull Throwable t) {
                Log.e("Network Failure", t.getMessage());
                Toast.makeText(DetallePoiActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private static class AdapterImageSlider extends PagerAdapter {

        private Activity act;
        private List<Image> items;

        private OnItemClickListener onItemClickListener;

        private interface OnItemClickListener {
            void onItemClick(View view, Image obj);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        // constructor
        private AdapterImageSlider(Activity activity, List<Image> items) {
            this.act = activity;
            this.items = items;
        }

        @Override
        public int getCount() {
            return this.items.size();
        }

        public Image getItem(int pos) {
            return items.get(pos);
        }

        public void setItems(List<Image> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final Image o = items.get(position);
            LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.item_slider_image, container, false);

            ImageView image = v.findViewById(R.id.image);

            MaterialRippleLayout lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);
            Tools.displayImageOriginal(act, image, Constantes.FILES_BASE_URL+o.image);
            lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, o);
                    }
                }
            });

            ((ViewPager) container).addView(v);

            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((RelativeLayout) object);

        }

    }
}
