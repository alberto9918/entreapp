package com.mario.myapplication.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mario.myapplication.R;
import com.mario.myapplication.materialx.utils.Tools;
import com.mario.myapplication.responses.MyProfileResponse;
import com.mario.myapplication.retrofit.generator.AuthType;
import com.mario.myapplication.retrofit.generator.ServiceGenerator;
import com.mario.myapplication.retrofit.services.UserService;
import com.mario.myapplication.util.MusicUtils;
import com.mario.myapplication.util.UtilToken;

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

    private String userId;
    private UserService service;
    private String jwt;
    private MyProfileResponse myProfileResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_dark);
        initToolbar();

        ButterKnife.bind(this);

        jwt = UtilToken.getToken(this);
        userId = UtilToken.getId(this).toString();
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
        points = res.getString(R.string.points) + " " + countPoints(myProfileResponse);
        //points = String.valueOf(countPoints(myProfileResponse));
        textViewPointsWritten.setText(points);

        // mViewModel.selectUser(myProfileResponse);

        //image
        Glide.with(this)
                .load(myProfileResponse.getPicture())
                .into(imageViewProfile);
        Log.d("LOL2", myProfileResponse.toString());
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
