package com.mario.myapplication.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.mario.myapplication.R;
import com.mario.myapplication.responses.LoginResponse;
import com.mario.myapplication.retrofit.generator.ServiceGenerator;
import com.mario.myapplication.retrofit.services.LoginService;
import com.mario.myapplication.ui.common.DashboardActivity;
import com.mario.myapplication.util.UtilToken;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    @BindView(R.id.email) TextInputEditText email_input;
    @BindView(R.id.password) TextInputEditText password_input;
    @BindView(R.id.button_login) Button button_login;
    @BindView(R.id.textViewSignUp) TextView tv_signup;

    Context ctx;
    private IAuthListener mListener;


    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_log_in, container, false);
        ButterKnife.bind(this, root);
        ctx = getActivity();
        button_login.setOnClickListener(v -> { doLogin(); });

        if(UtilToken.getToken(ctx) != null) {
            startActivity(new Intent(ctx, DashboardActivity.class));
        }

        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onGoToSignUp();
            }
        });
        return root;
    }

    public void doLogin() {
        String username_txt = email_input.getText().toString();
        String password_txt = password_input.getText().toString();
        final Pattern EMAIL_REGEX = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);

        if (username_txt.equals("") || password_txt.equals("")) {
            Toast.makeText(ctx, "Fields can't be empty!", Toast.LENGTH_LONG).show();
        } else if (!EMAIL_REGEX.matcher(username_txt).matches()) {
            Toast.makeText(ctx, "You need to use a correct email!", Toast.LENGTH_LONG).show();
        } else if (password_txt.length() < 6) {
            Toast.makeText(ctx, "Password must be at least 6 characters!", Toast.LENGTH_LONG).show();
        } else {
            String credentials = Credentials.basic(username_txt, password_txt);
            LoginService service = ServiceGenerator.createService(LoginService.class);
            Call<LoginResponse> call = service.doLogin(credentials);

            call.enqueue(new retrofit2.Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.code() != 201) {
                        // error
                        Log.e("RequestError", response.message());
                        Toast.makeText(ctx, "Error while trying to login", Toast.LENGTH_SHORT).show();
                    } else {
                        // exito
                        UtilToken.setToken(ctx, response.body().getToken());
                        UtilToken.setId(ctx, response.body().getUser().get_Id());
                        startActivity(new Intent(ctx, DashboardActivity.class));
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e("NetworkFailure", t.getMessage());
                    Toast.makeText(ctx, "Error. Can't connect to server", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
