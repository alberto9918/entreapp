package eu.visiton.app.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import eu.visiton.app.R;
import eu.visiton.app.dto.UserEditDto;
import eu.visiton.app.dto.UserImageDto;
import eu.visiton.app.data.PoiViewModel;
import eu.visiton.app.data.ProfileViewModel;
import eu.visiton.app.materialx.utils.Tools;
import eu.visiton.app.model.Image;
import eu.visiton.app.responses.ImageInvalidResponse;
import eu.visiton.app.responses.ImageResponse;
import eu.visiton.app.responses.MyProfileResponse;
import eu.visiton.app.responses.UserEditResponse;
import eu.visiton.app.responses.UserImageResponse;
import eu.visiton.app.responses.UserSResponse;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.PoiService;
import eu.visiton.app.retrofit.services.UserService;
import eu.visiton.app.ui.pois.details.DetallePoiActivity;
import eu.visiton.app.ui.pois.details.SaveImageHelper;
import eu.visiton.app.util.Constantes;
import eu.visiton.app.util.UtilToken;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    /* Edicion foto usuario*/
    private static final int READ_REQUEST_CODE = 42;
    private Uri uriSelected;
    private UserImageDto image;
    private ImageView previewPhoto;
    private AppCompatButton postButton;
    private UserEditDto userEdit;
    private UserSResponse user;
    private static List<String> array_image_poi = new ArrayList<>();
    private static List<String> array_image_user = new ArrayList<>();
    private static List<String> array_invalid_image_user = new ArrayList<>();


    private ProfileViewModel profileViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileViewModel = ViewModelProviders.of(this)
                .get(ProfileViewModel.class);

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

       fab.setOnClickListener(view -> {
            DialogFragment dialogFragment = new ProfileDialogFragment(userId);
            dialogFragment.show(getSupportFragmentManager(),"editUser");

        });

        this.getUser();

        UserService serviceUser = ServiceGenerator.createService(UserService.class, jwt, AuthType.JWT);
        Call<UserSResponse> callUser = serviceUser.getMe();

        callUser.enqueue(new Callback<UserSResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserSResponse> call, @NonNull Response<UserSResponse> response) {
                if (response.code() != 200) {
                    Toast.makeText(ProfileDarkActivity.this, "Request Error", Toast.LENGTH_SHORT).show();
                } else {
                    user = response.body();

                }
            }

            @Override
            public void onFailure(@NonNull Call<UserSResponse> call, @NonNull Throwable t) {
                Log.e("Network Failure estoy ", t.getMessage());
                Toast.makeText(ProfileDarkActivity.this, "Network Error churra", Toast.LENGTH_SHORT).show();

            }


        });
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
        profileViewModel.userProfile.observe(this, myProfileResponse -> {
            setItemsInfo(myProfileResponse);
        });

    }

    @SuppressLint("RestrictedApi")
    public void setItemsInfo(MyProfileResponse response) {
        String points="";
        Resources res = getResources();
        myProfileResponse = response;
        userId = myProfileResponse.getId();
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
                .load(Constantes.FILES_BASE_URL+myProfileResponse.getPicture())
                .into(imageViewProfile);

        int idFlag = this.getResources().getIdentifier("ic_flag_"+myProfileResponse.getLanguage().getIsoCode().toLowerCase(), "drawable", this.getPackageName());
        ivFlag.setImageResource(idFlag);

        if(!isProfile) {
            fab.setVisibility(View.GONE);
        }

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogImageFull();


            }
        });
    }

    private void showDialogImageFull() {
        Toast.makeText(this, "Imagen Perfil", Toast.LENGTH_SHORT).show();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_image_profile);

        // Button btnDownload =  dialog.findViewById(R.id.button_download);
        // onClickListener
        CardView cardView = dialog.findViewById(R.id.cardViewDescarga);


        Glide.with(this).load( Constantes.FILES_BASE_URL+myProfileResponse.getPicture()).into((ImageView) dialog.findViewById(R.id.ImageView_photo));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();
        cardView.setOnClickListener(v ->{

            showCustomDialog();


            dialog.dismiss();




        });




    }


    private void showCustomDialog() {
        final Dialog dialog2 = new Dialog(this);
        dialog2.setContentView(R.layout.dialog_add_post);
        dialog2.setCancelable(true);
        previewPhoto = dialog2.findViewById(R.id.imageView);
        postButton = dialog2.findViewById(R.id.bt_submit);
        postButton.setEnabled(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog2.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        //final AppCompatButton bt_submit = (AppCompatButton) dialog.findViewById(R.id.bt_submit);

        /*bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Post Submitted", Toast.LENGTH_SHORT).show();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_photo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Post Photo Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Post Link Clicked", Toast.LENGTH_SHORT).show();
            }
        });*/

        dialog2.show();
        dialog2.getWindow().setAttributes(lp);

        dialog2.findViewById(R.id.bt_gallery).setOnClickListener((view) -> { performFileSearch(); });

        dialog2.findViewById(R.id.bt_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriSelected != null) {

                    String jwt = UtilToken.getToken(Objects.requireNonNull(ProfileDarkActivity.this));
                    //PoiService service = ServiceGenerator.createService(PoiService.class, jwt, AuthType.JWT);
                    PoiService service = ServiceGenerator.createService(PoiService.class, null, AuthType.NO_AUTH);

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uriSelected);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                        int cantBytes;
                        byte[] buffer = new byte[1024*4];

                        while ((cantBytes = bufferedInputStream.read(buffer,0,1024*4)) != -1) {
                            baos.write(buffer,0,cantBytes);
                        }

                        RequestBody requestFile =
                                RequestBody.create(
                                        MediaType.parse(getContentResolver().getType(uriSelected)), baos.toByteArray());


                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("photo", "photo", requestFile);



                        Call<ImageResponse> callUpload = service.uploadImage(body);



                        dialog2.dismiss();

                        UserService userService = ServiceGenerator.createService(UserService.class, jwt, AuthType.JWT);
                        callUpload.enqueue(new Callback<ImageResponse>() {
                            @Override
                            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {

                                if (response.isSuccessful()) {
                                    Log.d("Uploaded", "Éxito");
                                    Log.d("Uploaded", response.body().toString());
                                    Toast.makeText(ProfileDarkActivity.this, response.body().getKey() + " hola" , Toast.LENGTH_SHORT).show();

                                    user.setPicture(response.body().getKey());
                                    userEdit = new UserEditDto(user.getEmail(), user.getName(), user.getCountry(), user.getLanguage(), user.getPicture(), user.getLikes(), user.getFavs(), user.getFriends(), user.getImages());




                                    Call<UserEditResponse> editUserImages = userService.editUser(user.getId(), userEdit);

                                    editUserImages.enqueue(new Callback<UserEditResponse>() {
                                        @Override
                                        public void onResponse(Call<UserEditResponse> call, Response<UserEditResponse> response) {
                                            Toast.makeText(ProfileDarkActivity.this, "GG", Toast.LENGTH_SHORT).show();
                                            getUser();
                                            Glide.with(ProfileDarkActivity.this)
                                                    .load(Constantes.FILES_BASE_URL+userEdit.getPicture())
                                                    .into(imageViewProfile);

                                        }

                                        @Override
                                        public void onFailure(Call<UserEditResponse> call, Throwable t) {
                                            Toast.makeText(ProfileDarkActivity.this, "F", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    Log.e("Upload error", response.message());
                                    try {
                                        Log.e("Upload error", response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //Log.e("Upload error", response.errorBody().toString());
                                    Toast.makeText(ProfileDarkActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ImageResponse> call, Throwable t) {
                                Log.e("Upload error", t.getMessage());
                            }

                        });


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });


    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "/".
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("Filechooser URI", "Uri: " + uri.toString());
                //showImage(uri);
                Glide
                        .with(this)
                        .load(uri)
                        .into(previewPhoto);
                uriSelected = uri;
                postButton.setEnabled(true);
            }
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
