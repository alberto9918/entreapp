package eu.visiton.app.ui.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.visiton.app.R;
import eu.visiton.app.data.ProfileViewModel;
import eu.visiton.app.dto.UserEditDto;
import eu.visiton.app.dto.UserImageDto;
import eu.visiton.app.materialx.utils.Tools;
import eu.visiton.app.responses.ImageResponse;
import eu.visiton.app.responses.MyProfileResponse;
import eu.visiton.app.responses.UserEditResponse;
import eu.visiton.app.responses.UserSResponse;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.PoiService;
import eu.visiton.app.retrofit.services.UserService;
import eu.visiton.app.util.Constantes;
import eu.visiton.app.util.UtilToken;
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
    private String currentPhotoPath;
    private static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
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
                Toast.makeText(ProfileDarkActivity.this, "Network Error", Toast.LENGTH_SHORT).show();

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

    @SuppressLint("RestrictedApi")
    public void setItemsInfo2(Response<MyProfileResponse> response) {
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
        Toast.makeText(this, "Profile picture", Toast.LENGTH_SHORT).show();
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

        dialog2.show();
        dialog2.getWindow().setAttributes(lp);


        dialog2.findViewById(R.id.bt_photo).setOnClickListener((view) -> { checkExternalStoragePermission(); });
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

                                    user.setPicture(response.body().getKey());
                                    userEdit = new UserEditDto(user.getEmail(), user.getName(), myProfileResponse.getcity(), user.getLanguage(), user.getPicture(), user.getLikes(), user.getFavs(), user.getFriends(), user.getImages());




                                    Call<UserEditResponse> editUserImages = userService.editUser(user.getId(), userEdit);

                                    editUserImages.enqueue(new Callback<UserEditResponse>() {
                                        @Override
                                        public void onResponse(Call<UserEditResponse> call, Response<UserEditResponse> response) {
                                            getUser2();
                                            Glide.with(ProfileDarkActivity.this)
                                                    .load(Constantes.FILES_BASE_URL+userEdit.getPicture())
                                                    .into(imageViewProfile);


                                        }

                                        @Override
                                        public void onFailure(Call<UserEditResponse> call, Throwable t) {
                                            Toast.makeText(ProfileDarkActivity.this, "Error editing user", Toast.LENGTH_SHORT).show();
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
        Uri uri = null;

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriSelected);
                previewPhoto.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            postButton.setEnabled(true);
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);*/
        }
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().

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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void checkExternalStoragePermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Log.e("WRITE_EXTERNAL_STORAGE", "Permission not granted WRITE_EXTERNAL_STORAGE.");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        225);
            }
        }if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("Error CAMERA", "Permission not granted CAMERA.");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        226);
            }
        }

        dispatchTakePictureIntent();

    }

    private void dispatchTakePictureIntent() {
        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }*/
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "MyPicture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
                uriSelected = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSelected);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
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



    public void getUser2() {//obtain from the api the user logged
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
                    setItemsInfo2(response);
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


}
