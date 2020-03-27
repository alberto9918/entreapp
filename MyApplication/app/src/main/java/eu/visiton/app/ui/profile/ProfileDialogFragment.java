package eu.visiton.app.ui.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import eu.visiton.app.R;
import eu.visiton.app.data.ProfileViewModel;
import eu.visiton.app.dto.UserEditDto;
import eu.visiton.app.responses.CategoryMyProfileResponse;
import eu.visiton.app.responses.LanguageResponse;
import eu.visiton.app.responses.MyProfileResponse;
import eu.visiton.app.responses.ResponseContainer;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.LanguageService;
import eu.visiton.app.util.UtilToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileDialogFragment extends DialogFragment {
    private View v;
    private EditText editTextName,  editTextCity, editTextemail;
    private Spinner spinnerLanguages;
    private Context ctx;
    private String jwt;
    private String userId;
    private String selectedLanguage;
    LanguageResponse idioma;
    private Button btn_save;

    List<LanguageResponse> languages;
    private ProfileViewModel profileViewModel;
    private MyProfileResponse updatedUser;
    LanguageService service;

    private ProfileDarkActivity profileDarkActivity;

    public ProfileDialogFragment(){

    }

    public ProfileDialogFragment(String id){
        this.userId = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ctx = getContext();

        jwt = UtilToken.getToken(ctx);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Edit user");

        builder.setCancelable(true);

        v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_my_profile_edit, null);

        builder.setView(v);

        loadItemsFragment(v);

        profileViewModel = ViewModelProviders.of(this)
                .get(ProfileViewModel.class);

        if(userId != null){
            loadProfile();
        }

        loadAllLanguages();

        spinnerLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idioma = (LanguageResponse) parent.getItemAtPosition(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if(validate()){//if the edit text are correct the user is updated
                    profileViewModel.updateProfile(updatedUser.getId(), myProfileResponseToUserEditDto(updatedUser));
                    dialog.dismiss();
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reiniciarActivity((Activity) ctx);
                        }
                    }, 500);

                }
            }
        });

        return builder.create();
    }

    public static void reiniciarActivity(Activity actividad){
        Intent intent=new Intent();
        intent.setClass(actividad, actividad.getClass());
        //llamamos a la actividad
        actividad.startActivity(intent);
        //finalizamos la actividad actual
        actividad.finish();
    }

    public void loadAllLanguages(){//take every language in node api
        service = ServiceGenerator.createService(LanguageService.class,
                jwt, AuthType.JWT);
        Call<ResponseContainer<LanguageResponse>> getAllLanguages = service.listLanguages();
        getAllLanguages.enqueue(new Callback<ResponseContainer<LanguageResponse>>() {
            @Override
            public void onResponse(Call<ResponseContainer<LanguageResponse>> call, Response<ResponseContainer<LanguageResponse>> response) {
                if (response.isSuccessful()) {
                    int spinnerPosition=1;
                    Log.d("successLanguage", "languageObtained");
                    //result put in languages
                    languages = response.body().getRows();

                    //setting spinner with languages
                    ArrayAdapter<LanguageResponse> adapter =
                            new ArrayAdapter<LanguageResponse>(ctx, android.R.layout.simple_spinner_dropdown_item, languages);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLanguages.setAdapter(adapter);

                    //getting the spinner position looking for the user's language

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

    public void loadItemsFragment(View view) {//load every layaout element
        spinnerLanguages = view.findViewById(R.id.spinnerLanguage);
        editTextCity=view.findViewById(R.id.editTextCity);
        editTextemail=view.findViewById(R.id.editTextEmail);
        editTextName=view.findViewById(R.id.editTextName);
        btn_save =view.findViewById(R.id.btn_edit_profile);
    }

    public void loadProfile() {
        profileViewModel.userProfile.observe(getActivity(), new Observer<MyProfileResponse>() {
            @Override
            public void onChanged(MyProfileResponse myProfileResponse) {
                updatedUser = myProfileResponse;
                editTextCity.setText(updatedUser.getcity());
                editTextemail.setText(updatedUser.getEmail());
                editTextName.setText(updatedUser.getName());
                selectedLanguage = updatedUser.getLanguage().toString();
            }
        });
    }

    public boolean validate(){//validate every edit text with a static class called validatorUserEdit
        ValidatorUserEdit.clearError(editTextCity);
        ValidatorUserEdit.clearError(editTextName);
        String incorrectName, incorrectCity;
        incorrectName = getString(R.string.incorrect_name);
        incorrectCity = getString(R.string.incorrect_city);
        boolean isValid=true;
        if (!ValidatorUserEdit.onlyLetters(editTextCity) || !ValidatorUserEdit.isNotEmpty(editTextCity)){
            isValid=false;
            ValidatorUserEdit.setError(editTextCity, incorrectCity);
        }
        if (!ValidatorUserEdit.onlyLetters(editTextName) || !ValidatorUserEdit.isNotEmpty(editTextName)){
            isValid=false;

            //if there is any error we show it next to the editText
            ValidatorUserEdit.setError(editTextName, incorrectName);
        }

        return isValid;
    }

    public UserEditDto myProfileResponseToUserEditDto(MyProfileResponse user){
        UserEditDto userEditDto = new UserEditDto();
        userEditDto.setPicture(user.getPicture());
        userEditDto.setCity(editTextCity.getText().toString());
        List<String> likes = new ArrayList<>();
        userEditDto.setName(editTextName.getText().toString());
        userEditDto.setLanguage(idioma.getId());
        userEditDto.setEmail(editTextemail.getText().toString());
        userEditDto.setFavs(user.getFavs());
        userEditDto.setFriends(user.getFriends());
        userEditDto.setImages(user.getImages());

        //iterations
        for (CategoryMyProfileResponse c:user.getLikes()){
            likes.add(c.getId());
        }
        //userEditDto.setLikes(likes);


        return userEditDto;
    }
}
