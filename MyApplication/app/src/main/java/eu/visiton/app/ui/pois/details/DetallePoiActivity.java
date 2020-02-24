package eu.visiton.app.ui.pois.details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import eu.visiton.app.R;
import eu.visiton.app.materialx.utils.Tools;
import eu.visiton.app.model.Image;
import eu.visiton.app.responses.PoiResponse;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.PoiService;
import eu.visiton.app.ui.pois.qrScanner.QrCodeActivity;
import eu.visiton.app.util.Constantes;
import eu.visiton.app.util.MusicUtils;
import eu.visiton.app.util.UtilToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetallePoiActivity extends AppCompatActivity {

    private static String id, dialogTitle, dialogMessage, dialogAnimation;
    private PoiResponse poi;

    private View parent_view;
    private ViewPager viewPager;
    private LinearLayout layout_dots;
    private AdapterImageSlider adapterImageSlider;
    private ImageView[] dots;
    private ImageButton imgbtRated;
    private TextView tvPrecio, tvDescripcion, tvTitulo, tvInfo, tvReviews;
    AppCompatRatingBar ratingBarPoi;
    private CardView audioPlayer;
    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 2;


    private static List<String> array_image_poi = new ArrayList<>();


    // MEDIA PLAYER
    private AppCompatSeekBar seek_song_progressbar;
    private FloatingActionButton bt_play;
    private TextView tv_song_current_duration, tv_song_total_duration;

    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();

    //private SongsManager songManager;
    private MusicUtils utils;

    boolean repetir = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_poi);

        initToolbar();
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");

        try {
            dialogTitle = extras.getString(Constantes.EXTRA_DIALOG_TITLE);
            dialogMessage = extras.getString(Constantes.EXTRA_DIALOG_MESSAGE);
            dialogAnimation = extras.getString(Constantes.EXTRA_DIALOG_ANIMATION);
            if(dialogTitle != null && dialogMessage != null && dialogAnimation != null) {
                showAlertPoi();
            }
        } catch (Exception e) {
        }
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
        audioPlayer = findViewById(R.id.audioPlayer);
        imgbtRated = findViewById(R.id.imageButtonRated);

        // Log.e("rating",poi.getAverageRating().toString());
        ratingBarPoi.setRating(poi.getAverageRating());
        tvReviews.setText(poi.getAverageRating().toString());
        tvTitulo.setText(Html.fromHtml(poi.getName()));

        if(poi.getIsRated() == false){
            imgbtRated.setVisibility(View.INVISIBLE);
        }

        tvInfo.setText(poi.getSchedule() + " ["+poi.getStatus()+"]");

        if(poi.getPrice() == 0.0f) {
            tvPrecio.setText("Gratis");
        } else {
            tvPrecio.setText("€ "+poi.getPrice());
        }

        tvDescripcion.setText(Html.fromHtml(poi.getDescription().getTranslations().get(0).getTranslatedDescription()));

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
        showUserRating();
    }

    private void showUserRating() {
        imgbtRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(DetallePoiActivity.this, "Your rating is: "+poi.getUserRating()[0].getRating(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPlayer() {
        if(poi.getAudioguides().getTranslations().size() == 0) {
            audioPlayer.setVisibility(View.GONE);
        } else {

            parent_view = findViewById(R.id.parent_view);
            seek_song_progressbar = (AppCompatSeekBar) findViewById(R.id.seek_song_progressbar);
            bt_play = (FloatingActionButton) findViewById(R.id.bt_play);

            // set Progress bar values
            seek_song_progressbar.setProgress(0);
            seek_song_progressbar.setMax(MusicUtils.MAX_PROGRESS);

            tv_song_current_duration = (TextView) findViewById(R.id.tv_song_current_duration);
            tv_song_total_duration = (TextView) findViewById(R.id.tv_song_total_duration);

            // Media Player
            mp = new MediaPlayer();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Changing button image to play button
                    bt_play.setImageResource(R.drawable.ic_play_arrow);
                }
            });

            try {
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setDataSource(Constantes.FILES_BASE_URL + poi.getAudioguides().getTranslations().get(0).getTranslatedFile());
                mp.prepare();
            } catch (Exception e) {
                Snackbar.make(parent_view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
            }

            utils = new MusicUtils();
            // Listeners
            seek_song_progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // remove message Handler from updating progress bar
                    mHandler.removeCallbacks(mUpdateTimeTask);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    int totalDuration = mp.getDuration();
                    int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                    // forward or backward to certain seconds
                    mp.seekTo(currentPosition);

                    // update timer progress again
                    mHandler.post(mUpdateTimeTask);
                }
            });
            buttonPlayerAction();
            updateTimerAndSeekbar();

        }
    }

    /**
     * Play button click event plays a song and changes button to pause image
     * pauses a song and changes button to play image
     */
    private void buttonPlayerAction() {
        bt_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mp.isPlaying()) {
                    mp.pause();
                    // Changing button image to play button
                    bt_play.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    // Resume song
                    mp.start();
                    // Changing button image to pause button
                    bt_play.setImageResource(R.drawable.ic_pause);
                    // Updating progress bar
                    mHandler.post(mUpdateTimeTask);
                }

            }
        });
    }

    public void controlClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_repeat: {
                toggleButtonColor((ImageButton) v);
                break;
            }
        }
    }

    private boolean toggleButtonColor(ImageButton bt) {
        String selected = (String) bt.getTag(bt.getId());
        if (repetir) { // selected
            mp.setLooping(false);
            repetir = false;
            bt.setColorFilter(getResources().getColor(R.color.grey_90), PorterDuff.Mode.SRC_ATOP);
            return false;
        } else {
            mp.setLooping(true);
            repetir = true;
            bt.setColorFilter(getResources().getColor(R.color.green_500), PorterDuff.Mode.SRC_ATOP);
            Snackbar.make(parent_view, "Repetir audioguía", Snackbar.LENGTH_SHORT).show();

            return true;
        }
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();
            // Running this thread after 10 milliseconds
            if (mp.isPlaying()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void updateTimerAndSeekbar() {
        long totalDuration = mp.getDuration();
        long currentDuration = mp.getCurrentPosition();

        // Displaying Total Duration time
        tv_song_total_duration.setText(utils.milliSecondsToTimer(totalDuration));
        // Displaying time completed playing
        tv_song_current_duration.setText(utils.milliSecondsToTimer(currentDuration));

        // Updating progress bar
        int progress = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));
        seek_song_progressbar.setProgress(progress);
    }

    // stop player when destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        if(mp != null) {
            mp.release();
        }
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
        } else if(item.getItemId() == R.id.action_qr_scan) {
            showQrScanner();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    /** Api call to get details of one POI **/
    private void getPoiDetails() {
        String jwt = UtilToken.getToken(Objects.requireNonNull(DetallePoiActivity.this));
        PoiService service = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);
        String idLang = UtilToken.getLanguageId(this);
        Call<PoiResponse> call = service.getPoiLang(id, idLang);

        call.enqueue(new Callback<PoiResponse>() {
            @Override
            public void onResponse(@NonNull Call<PoiResponse> call, @NonNull Response<PoiResponse> response) {
                if (response.code() != 200) {
                    Toast.makeText(DetallePoiActivity.this, "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    poi = response.body();
                    // Log.i("rating", poi.getAverageRating().toString());
                    array_image_poi = new ArrayList<>();
                    if(poi.getImages().size() > 0) {
                        array_image_poi.addAll(poi.getImages());
                    }
                    initComponent();
                    initPlayer();
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



    // Qr Code
    public void showQrScanner() {
        checkCameraPermissions();
    }

    /** Check if camera permissions are granted. If not, ask for it. **/
    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(this, QrCodeActivity.class);
            startActivity(i);
            finish();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_ACCESS_CAMERA);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // El usuario concede el permiso
                    Intent i = new Intent(this, QrCodeActivity.class);
                    startActivity(i);
                } else {
                    // El usuario ha denegado el permiso de Cámara
                    Toast.makeText(DetallePoiActivity.this, "Sin el permiso de cámara no podrá escanear códigos Qr", Toast.LENGTH_SHORT).show();

                }
                return;

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void showAlertPoi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(dialogMessage)
                .setTitle(dialogTitle);

        View v = LayoutInflater.from(this).inflate(R.layout.dialog_visit_poi, null);
        LottieAnimationView animationView = v.findViewById(R.id.lottieBadge);
        animationView.setAnimation(dialogAnimation);
        animationView.playAnimation();

        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
