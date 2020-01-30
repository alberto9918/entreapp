package com.mario.myapplication.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mario.myapplication.R;
import com.mario.myapplication.responses.LanguageResponse;
import com.mario.myapplication.responses.LoginResponse;
import com.mario.myapplication.responses.Register;
import com.mario.myapplication.responses.ResponseContainer;
import com.mario.myapplication.retrofit.generator.AuthType;
import com.mario.myapplication.retrofit.generator.ServiceGenerator;
import com.mario.myapplication.retrofit.services.LanguageService;
import com.mario.myapplication.retrofit.services.LoginService;
import com.mario.myapplication.ui.common.DashboardActivity;
import com.mario.myapplication.util.UserStringList;
import com.mario.myapplication.util.UtilToken;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindViews;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUpFragment extends Fragment {
    @BindViews(value = {R.id.email_input_edit_sign,
            R.id.password_input_edit_sign,
            R.id.confirm_password_edit})
    protected List<TextInputEditText> views;
    EditText email_input, password_input, confirm_password;
    Spinner language;
    Context ctx = this.getContext();
    List<LanguageResponse> languageList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    // Acciones al crear el fragmento
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ctx = view.getContext();
        email_input = getActivity().findViewById(R.id.email_input_edit_sign);
        password_input = getActivity().findViewById(R.id.password_input_edit_sign);
        confirm_password = getActivity().findViewById(R.id.confirm_password_edit);
        language = getActivity().findViewById(R.id.language);

        loadAllLanguages();
    }


    public void loadAllLanguages(){
        LanguageService service = ServiceGenerator.createService(LanguageService.class);
        Call<ResponseContainer<LanguageResponse>> getAllLanguages = service.listLanguagesSignUp();
        getAllLanguages.enqueue(new retrofit2.Callback<ResponseContainer<LanguageResponse>>() {
            @Override
            public void onResponse(Call<ResponseContainer<LanguageResponse>> call, Response<ResponseContainer<LanguageResponse>> response) {
                if (response.isSuccessful()) {
                    int spinnerPosition=1;
                    Log.d("successLanguage", "languageObtained");
                    languageList = response.body().getRows();
                    System.out.println(languageList);

                    ArrayAdapter<LanguageResponse> adapter =
                            new ArrayAdapter<LanguageResponse>(ctx, android.R.layout.simple_spinner_dropdown_item, languageList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    language.setAdapter(adapter);

                    language.setSelection(languageList.size()-1);

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


    public void doSignUp() {
        // Recoger los datos del formulario
        String email = email_input.getText().toString();
        String password = password_input.getText().toString();
        String confirm = confirm_password.getText().toString();
        final Pattern EMAIL_REGEX = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);

        if (email.equals("") || password.equals("")) {
            Toast.makeText(ctx, "Fields can't be clear!", Toast.LENGTH_SHORT).show();
        } else if (!EMAIL_REGEX.matcher(email).matches()) {
            Toast.makeText(ctx, "You need to use a correct email!", Toast.LENGTH_LONG).show();
        } else if (password.length() < 6) {
            Toast.makeText(ctx, "Password must be at least 6 characters!", Toast.LENGTH_LONG).show();
        } else if (!password.equals(confirm)) {
            Toast.makeText(ctx, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        } else {


            LanguageResponse chosen = (LanguageResponse) language.getSelectedItem();
            Register register = new Register(email, password, chosen.getId() );
            LoginService service = ServiceGenerator.createService(LoginService.class);
            Call<LoginResponse> loginReponseCall = service.doSignUp(register);

            loginReponseCall.enqueue(new retrofit2.Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.code() == 201) {
                        // éxito
                        UtilToken.setToken(ctx, response.body().getToken());
                        startActivity(new Intent(ctx, DashboardActivity.class));
                    } else {
                        // error
                        Toast.makeText(ctx, "Error while signing up.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e("NetworkFailure", t.getMessage());
                    Toast.makeText(ctx, "Network Connection Failure", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}
