package com.mario.myapplication.ui.profile;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mario.myapplication.dto.UserEditDto;
import com.mario.myapplication.responses.CategoryMyProfileResponse;
import com.mario.myapplication.responses.LanguageResponse;
import com.mario.myapplication.responses.LenguageResponseMyProfile;
import com.mario.myapplication.responses.LoginResponse;
import com.mario.myapplication.responses.ResponseContainer;
import com.mario.myapplication.responses.UserEditResponse;
import com.mario.myapplication.retrofit.generator.AuthType;
import com.mario.myapplication.R;
import com.mario.myapplication.responses.MyProfileResponse;
import com.mario.myapplication.retrofit.generator.ServiceGenerator;
import com.mario.myapplication.retrofit.services.LanguageService;
import com.mario.myapplication.retrofit.services.LoginService;
import com.mario.myapplication.retrofit.services.UserService;
import com.mario.myapplication.util.UtilToken;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfileEditFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int RESULT_OK = 73;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private UserViewModel mViewModel;
    private EditText editTextName,  editTextCity, editTextemail;
    private Spinner spinnerLanguages;
    private ImageView profile_image;
    private MyProfileResponse updatedUser;
    private MyProfileInteractionListener mListener;
    LanguageService service;
    UserService userService;
    private Context ctx;
    private String jwt;
    private String userId;
    private Button btn_save;
    FirebaseStorage storage;
    String fireBaSeUrl;
    StorageReference storageReference;
    List<LanguageResponse> languages;
    private String urlUploadedPicture=null;

    public MyProfileEditFragment() {
    }

    public static MyProfileEditFragment newInstance(String param1, String param2) {
        MyProfileEditFragment fragment = new MyProfileEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ctx = getContext();
        jwt = UtilToken.getToken(ctx);
        userId = UtilToken.getId(ctx).toString();
        View v= inflater.inflate(R.layout.fragment_my_profile_edit, container, false);
        loadItemsFragment(v);
        setItemsFragment(updatedUser);
        return v;
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof MyProfileInteractionListener) {
            mListener = (MyProfileInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onCreate(Bundle savedInstanceState) {
        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activities receive the same MyViewModel instance created by the first activity.

        super.onCreate(savedInstanceState);
        filePath=null;
        fireBaSeUrl="https://firebasestorage.googleapis.com/v0/b/";
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        mViewModel.getSelectedUser().observe(getActivity(),
                user -> {
                updatedUser = user;

                //setItemsFragment(user);
                });
    }

    public void loadAllLanguages(){
        service = ServiceGenerator.createService(LanguageService.class,
                jwt, AuthType.JWT);
        Call<ResponseContainer<LanguageResponse>> getAllLanguages = service.listLanguages();
        getAllLanguages.enqueue(new Callback<ResponseContainer<LanguageResponse>>() {
            @Override
            public void onResponse(Call<ResponseContainer<LanguageResponse>> call, Response<ResponseContainer<LanguageResponse>> response) {
                if (response.isSuccessful()) {
                    int spinnerPosition=1;
                    Log.d("successLanguage", "languageObtained");
                    languages = response.body().getRows();
                    System.out.println(languages);

                    ArrayAdapter<LanguageResponse> adapter =
                            new ArrayAdapter<LanguageResponse>(ctx, android.R.layout.simple_spinner_dropdown_item, languages);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLanguages.setAdapter(adapter);



                    spinnerPosition = languages.indexOf(updatedUser.getLanguage());



                    spinnerLanguages.setSelection(spinnerPosition);

                } else {
                    Toast.makeText(ctx, "You have to log in!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<LanguageResponse>> call, Throwable t) {
                Log.d("onFailure", "Fail in the request");
                Toast.makeText(ctx, "Fail in the request!", Toast.LENGTH_LONG).show();
            }
        });
    }

    //own methods
    public void loadItemsFragment(View view) {
        spinnerLanguages = view.findViewById(R.id.spinnerLanguage);
        editTextCity=view.findViewById(R.id.editTextCity);
        editTextemail=view.findViewById(R.id.editTextEmail);
        editTextName=view.findViewById(R.id.editTextName);
        btn_save =view.findViewById(R.id.btn_edit_profile);
        profile_image = view.findViewById(R.id.edit_profile_image);
    }
    public void updateUserCall(UserEditDto userEditDto) {
        userService = ServiceGenerator.createService(UserService.class,
                jwt, AuthType.JWT);
        Call<UserEditResponse> editUser = userService.editUser(updatedUser.getId(), userEditDto);
        editUser.enqueue(new Callback<UserEditResponse>() {
            @Override
            public void onResponse(Call<UserEditResponse> call, Response<UserEditResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("success editing user", "userUpdated");

                    //move to my profile fragment
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.contenedor, new MyProfileFragment())
                            .commit();

                } else {
                    Toast.makeText(ctx, "You have to log in!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserEditResponse> call, Throwable t) {
                Log.d("onFailure", "Fail in the request");
                Toast.makeText(ctx, "Fail in the request!", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void setItemsFragment(MyProfileResponse user){
        //upload profile image
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();

            }
        });

        //edit user
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUserCall(myProfileResponseToUserEditDto(user));

            }
        });
        //image
        Glide.with(ctx)
                .load(user.getPicture().toString())
                .into(profile_image);
        editTextName.setText(user.getName());
        editTextemail.setText(user.getEmail());
        if (user.getcity()!=null){
            editTextCity.setText(user.getcity());
        }else{
            editTextCity.setText(R.string.no_city);
        }
        loadAllLanguages();

    }
    public UserEditDto myProfileResponseToUserEditDto(MyProfileResponse user){
        UserEditDto userEditDto = new UserEditDto();
        userEditDto.setPicture(user.getPicture());
        userEditDto.setCity(editTextCity.getText().toString());
        List<String> likes = new ArrayList<>();
        userEditDto.setName(editTextName.getText().toString());
        LanguageResponse r = (LanguageResponse) spinnerLanguages.getSelectedItem();
        userEditDto.setLanguage(r.getId());
        userEditDto.setEmail(editTextemail.getText().toString());
        userEditDto.setFavs(user.getFavs());
        userEditDto.setFriends(user.getFriends());
        //iterations
        for (CategoryMyProfileResponse c:user.getLikes()){
        likes.add(c.getId());
        }
        userEditDto.setLikes(likes);


        return userEditDto;
    }
    public UserEditDto myProfileResponseToUserEditDto(MyProfileResponse user, String profilePicture){
        UserEditDto userEditDto = new UserEditDto();
        userEditDto.setCity(editTextCity.getText().toString());
        List<String> likes = new ArrayList<>();
        userEditDto.setName(editTextName.getText().toString());
        LanguageResponse r = (LanguageResponse) spinnerLanguages.getSelectedItem();
        userEditDto.setLanguage(r.getId());
        userEditDto.setEmail(editTextemail.getText().toString());
        userEditDto.setFavs(user.getFavs());
        userEditDto.setFriends(user.getFriends());
        //iterations
        for (CategoryMyProfileResponse c:user.getLikes()){
            likes.add(c.getId());
        }
        userEditDto.setLikes(likes);
        userEditDto.setPicture(profilePicture);
        updatedUser.setPicture(profilePicture);
        mViewModel.selectUser(updatedUser);


        return userEditDto;
    }

    //UPDLOAD PROFILE IMAGE METHODS
    public void performFileSearch() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            Uri uri = null;
            if (resultData != null) {
                filePath =resultData.getData();
                Log.i("Filechooser URI", "Uri: " + filePath.toString());
                //showImage(uri);
                Glide
                        .with(this)
                        .load(filePath)
                        .into(profile_image);
                uploadImage();
            }
        }
    }
    public void obtainDownloadUrl(StorageReference ref) {

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                urlUploadedPicture=uri.toString();
                UserEditDto u=myProfileResponseToUserEditDto(updatedUser, urlUploadedPicture);
                updateUserCall(u);

            }

        });
        ref.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ctx, "Error, not uploaded", Toast.LENGTH_SHORT).show();
                urlUploadedPicture=null;
            }
        });

    }
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(ctx);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("image/"+ UUID.randomUUID().toString());


            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //obtain url
                            obtainDownloadUrl(ref);


                            progressDialog.dismiss();
                            Toast.makeText(ctx, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ctx, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }


}

