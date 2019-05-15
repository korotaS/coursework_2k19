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
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.course_work_2019.Other.ViewDialog;
import com.example.course_work_2019.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText email_edit;
    private FirebaseAuth auth;
    Animation animAlpha;
    Button res_pas_button;
    ViewDialog viewDialog;

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    void setup() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        email_edit = findViewById(R.id.reset_pas_email_edit);
        res_pas_button = findViewById(R.id.reset_pas_button);
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        auth = FirebaseAuth.getInstance();
        viewDialog = new ViewDialog(this);
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.reset_password_layout);
        setTitle("Восстановление пароля");

        setup();

        res_pas_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);

                if(!checkInternetConnection()){
                    Toast.makeText(getApplicationContext(),
                            "Отсутствует подключение к интернету!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = email_edit.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    email_edit.setError("Пожалуйста введите e-mail.");
                    return;
                }

                viewDialog.showDialog();
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                viewDialog.hideDialog();
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, "На email отправлено письмо.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivityForResult(intent, 0);
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "Ошибка!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        viewDialog.hideDialog();
                        Toast.makeText(ResetPasswordActivity.this,
                                "На данный e-mail нет пользователя!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
