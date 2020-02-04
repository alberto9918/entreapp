package eu.visiton.app.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import eu.visiton.app.R;
import eu.visiton.app.materialx.utils.Tools;
import eu.visiton.app.responses.MyProfileResponse;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.UserService;
import eu.visiton.app.util.Constantes;
import eu.visiton.app.util.UtilToken;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileDarkActivity extends AppCompatActivity {
    @BindView(R.id.textViewName) TextView textViewName;
    @BindView(R.id.texViewCityWritten) TextView texViewCityWritten;
    @BindView(R.id.textViewEmailWritten) TextView textViewEmailWritten;
    @BindView(R.id.textViewLanguageWritten) TextView textViewLanguageWritten;
    @BindView(R.id.textViewFriendsWritten) TextView textViewFriendsWritten;
    @BindView(R.id.textViewPoisWritten) TextView textViewPoisWritten;
    @BindView(R.id.textViewBadgesWritten) TextView textViewBadgesWritten;
    @BindView(R.id.textViewPointsWritten) TextView textViewPointsWritten;
    @BindView(R.id.imageViewProfile) ImageView imageViewProfile;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.imageViewFlag) ImageView ivFlag;

    private String userId;
    private UserService service;
    private String jwt;
    private MyProfileResponse myProfileResponse;
    private boolean isProfile = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_dark);
        initToolbar();

        ButterKnife.bind(this);

        jwt = UtilToken.getToken(this);

        Bundle extras = getIntent().getExtras();
        try {
            userId = extras.getString(Constantes.EXTRAS_USER_ID);
        } catch (Exception e) {
            userId = UtilToken.getId(this).toString();
            isProfile = true;
        }

        if (jwt == null) {
            //redirect to login
        }

        this.getUser();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Tools.setSystemBarColor(this, R.color.grey_95);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_close, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.green_500));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == R.id.action_close) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getUser(){//obtain from the api the user logged
        service = ServiceGenerator.createService(UserService.class,
                jwt, AuthType.JWT);
        Call<MyProfileResponse> getOneUser = service.getUser(userId);
        getOneUser.enqueue(new Callback<MyProfileResponse>() {
            @Override
            public void onResponse(Call<MyProfileResponse> call, Response<MyProfileResponse> response) {
                //Resources res = getResources();
                String points = "";
                if (response.isSuccessful()) {
                    Log.d("Success", "user obtain successfully");
                    setItemsInfo(response);
                } else {
                    Log.d("Fail", "user can't be obtain successfully");
                    Toast.makeText(ProfileDarkActivity.this, "You have to log in!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MyProfileResponse> call, Throwable t) {
                Log.d("Conexion failure", "FALLITO BUENO");
                Toast.makeText(ProfileDarkActivity.this, "Fail in the request!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void setItemsInfo(Response<MyProfileResponse> response) {
        String points="";
        Resources res = getResources();
        myProfileResponse = response.body();
        textViewEmailWritten.setText(myProfileResponse.getEmail());
        textViewName.setText(myProfileResponse.getName());
        if (myProfileResponse.getLanguage() != null) {
            textViewLanguageWritten.setText(myProfileResponse.getLanguage().getName());
        } else {
            textViewLanguageWritten.setText(R.string.no_language);
        }
        if (myProfileResponse.getcity() != null) {
            texViewCityWritten.setText(myProfileResponse.getcity());
        } else {
            texViewCityWritten.setText(R.string.no_city);
        }
        textViewPoisWritten.setText(String.valueOf(countPoisVisited(myProfileResponse)));
        textViewBadgesWritten.setText(String.valueOf(countBadges(myProfileResponse)));
        textViewFriendsWritten.setText(String.valueOf(myProfileResponse.getFriends().size()));
        points = String.valueOf(countPoints(myProfileResponse));
        //points = String.valueOf(countPoints(myProfileResponse));
        textViewPointsWritten.setText(points);

        //image
        Glide.with(this)
                .load(myProfileResponse.getPicture())
                .into(imageViewProfile);

        int idFlag = this.getResources().getIdentifier("ic_flag_"+myProfileResponse.getLanguage().getIsoCode().toLowerCase(), "drawable", this.getPackageName());
        ivFlag.setImageResource(idFlag);

        if(!isProfile) {
            fab.setVisibility(View.GONE);
        }
    }

    //it counts badges points of our users
    public int countPoints(MyProfileResponse u) {
        int points = 0;
        if (u.getBadges().size() >= 1) {
            for (int i = 0; i < u.getBadges().size(); i++) {
                points = points + u.getBadges().get(i).getPoints();
            }
        }
        return points;
    }
    //it counts the number of badges
    public int countBadges(MyProfileResponse u) {
        int badges = 0;
        if (u.getBadges().size() >= 1) {
            for (int i = 0; i < u.getBadges().size(); i++) {
                badges++;
            }
        }
        return badges;
    }

    //it counts our pois visited
    public int countPoisVisited(MyProfileResponse u) {
        return u.getVisited().size();
    }
}
