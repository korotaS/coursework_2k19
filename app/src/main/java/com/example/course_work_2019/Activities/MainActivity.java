package com.example.course_work_2019.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.course_work_2019.R;
import com.example.course_work_2019.Other.ViewDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    EditText email_edit,password_edit;
    Button registerButton,loginButton, pasButton;
    FirebaseAuth firebaseAuth;
    ViewDialog viewDialog;
    Animation animAlpha;

    boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    void setup() {
        db = FirebaseFirestore.getInstance();
        email_edit = findViewById(R.id.edit_email);
        password_edit =  findViewById(R.id.edit_password);
        registerButton =  findViewById(R.id.reg_button);
        loginButton =  findViewById(R.id.login_button);
        pasButton = findViewById(R.id.pas_button);
        viewDialog = new ViewDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        if(firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), ChooseActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main_layout);
        setTitle("Вход");

        setup();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        pasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                startActivityForResult(new Intent(getApplicationContext(), ResetPasswordActivity.class), 0);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);

                if(!checkInternetConnection()){
                    Toast.makeText(getApplicationContext(),
                            "Отсутствует подключение к интернету!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = email_edit.getText().toString();
                String password = password_edit.getText().toString();

                if(email.equals("admin") && password.equals("admin")){
                    startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                }
                else {
                    if(TextUtils.isEmpty(email)){
                        email_edit.setError("Пожалуйста введите E-mail.");
                        return;
                    }
                    if(TextUtils.isEmpty(password)){
                        password_edit.setError("Пожалуйста введите пароль.");
                        return;
                    }
                    viewDialog.showDialog();
                    firebaseAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    try {
                                        if (task.isSuccessful()) {
                                            if(!firebaseAuth.getCurrentUser().isEmailVerified()) {
                                                Toast.makeText(getApplicationContext(), "E-mail не верифицирован!", Toast.LENGTH_SHORT).show();
                                                firebaseAuth.signOut();
                                            }
                                            else {
                                                startActivity(new Intent(MainActivity.this, ChooseActivity.class));
                                                Toast.makeText(getApplicationContext(), "Вход успешный!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                            viewDialog.hideDialog();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Неверный email или пароль!", Toast.LENGTH_SHORT).show();
                                        }
                                        viewDialog.hideDialog();
                                    }
                                    catch (Exception ignored){
                                    }
                                }
                            });
                }
            }
        });

    }
}
