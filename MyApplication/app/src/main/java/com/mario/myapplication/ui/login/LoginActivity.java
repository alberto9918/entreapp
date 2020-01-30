package com.mario.myapplication.ui.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mario.myapplication.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.contenedor, LoginFragment.newInstance())
                .commit();
    }
}
