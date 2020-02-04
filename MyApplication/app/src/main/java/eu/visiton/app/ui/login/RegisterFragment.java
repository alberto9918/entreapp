package eu.visiton.app.ui.login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import eu.visiton.app.R;
import eu.visiton.app.responses.LanguageResponse;
import eu.visiton.app.responses.MyProfileResponse;
import eu.visiton.app.responses.Register;
import eu.visiton.app.responses.ResponseContainer;
import eu.visiton.app.responses.SignUpResponse;
import eu.visiton.app.retrofit.generator.AuthType;
import eu.visiton.app.retrofit.generator.ServiceGenerator;
import eu.visiton.app.retrofit.services.LanguageService;
import eu.visiton.app.retrofit.services.LoginService;
import eu.visiton.app.retrofit.services.UserService;
import eu.visiton.app.ui.common.DashboardActivity;
import eu.visiton.app.util.UtilToken;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterFragment extends Fragment {
    @BindView(R.id.progress_bar) ProgressBar progress_bar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.content) View parent_view;
    @BindView(R.id.textViewLogin) TextView tv_login;
    @BindView(R.id.email_input_edit_sign) TextView email_input;
    @BindView(R.id.password_input_edit_sign) TextView password_input;
    @BindView(R.id.confirm_password_edit) TextView confirm_password;
    @BindView(R.id.language) Spinner language;

    Context ctx = this.getContext();
    List<LanguageResponse> languageList = new ArrayList<>();
    private IAuthListener mListener;
    String jwt, userId;

    public static RegisterFragment newInstance() {
        Bundle args = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        ButterKnife.bind(this, v);
        ctx = getActivity();

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onGoToLogin();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                doSignUp();
            }
        });

        loadAllLanguages();


        return v;
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
        progress_bar.setVisibility(View.VISIBLE);
        fab.setAlpha(0f);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progress_bar.setVisibility(View.GONE);
                fab.setAlpha(1f);
                Snackbar.make(parent_view, "Login data submitted", Snackbar.LENGTH_SHORT).show();
            }
        }, 1000);

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
            Call<SignUpResponse> loginReponseCall = service.doSignUp(register);

            loginReponseCall.enqueue(new retrofit2.Callback<SignUpResponse>() {
                @Override
                public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                    if (response.code() == 201) {
                        // éxito
                        UtilToken.setToken(ctx, response.body().getToken());
                        UtilToken.setId(ctx, response.body().getUser().getId());
                        getUser();
                        startActivity(new Intent(ctx, DashboardActivity.class));
                    } else {
                        // error
                        Toast.makeText(ctx, "Error while signing up.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<SignUpResponse> call, Throwable t) {
                    Log.e("NetworkFailure", t.getMessage());
                    Toast.makeText(ctx, "Network Connection Failure", Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    public void getUser(){//obtain from the api the user logged
        jwt = UtilToken.getToken(getActivity());
        userId = UtilToken.getId(getActivity()).toString();

        UserService service = ServiceGenerator.createService(UserService.class,
                jwt, AuthType.JWT);
        Call<MyProfileResponse> getOneUser = service.getUser(userId);
        getOneUser.enqueue(new Callback<MyProfileResponse>() {
            @Override
            public void onResponse(Call<MyProfileResponse> call, Response<MyProfileResponse> response) {
                if (response.isSuccessful()) {
                    UtilToken.setLanguageIsoCode(getActivity(), response.body().getLanguage().getIsoCode());
                    UtilToken.setLanguageId(getActivity(), response.body().getLanguage().getId());
                    startActivity(new Intent(ctx, DashboardActivity.class));
                } else {
                    Toast.makeText(getActivity(), "Fail get user", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MyProfileResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Fail get user", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IAuthListener) {
            mListener = (IAuthListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IAuthListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
