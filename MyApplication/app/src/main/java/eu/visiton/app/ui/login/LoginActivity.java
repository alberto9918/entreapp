package eu.visiton.app.ui.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import eu.visiton.app.R;

public class LoginActivity extends AppCompatActivity implements IAuthListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.contenedor, LoginFragment.newInstance())
                .commit();
    }

    @Override
    public void onGoToSignUp() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor, RegisterFragment.newInstance())
                .commit();
    }

    @Override
    public void onGoToLogin() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor, LoginFragment.newInstance())
                .commit();
    }
}
