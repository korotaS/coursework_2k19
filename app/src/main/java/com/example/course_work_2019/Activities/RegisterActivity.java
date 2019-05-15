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

import com.example.course_work_2019.R;
import com.example.course_work_2019.Models.User;
import com.example.course_work_2019.Other.ViewDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    FirebaseFirestore db;
    EditText email_edit, password_edit, phone_edit, name_edit, link_edit;
    Button registerButton;
    FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    ViewDialog viewDialog;
    Animation animAlpha;

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

    void addUser(String user_id, String phone, String link, String name, final FirebaseUser _user){
        User user = new User(user_id, phone, link, name);
        db.collection("users").document("user_" + user_id).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                _user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterActivity.this,
                                "Зарегистрирован! Верифицируйте свою почту.",
                                Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        viewDialog.hideDialog();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        viewDialog.hideDialog();
                        Toast.makeText(RegisterActivity.this,
                                "Ошибка отправки верификационного письма.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                viewDialog.hideDialog();
                Toast.makeText(getApplicationContext(), "Не добавлено", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void setup() {
        db = FirebaseFirestore.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        email_edit = findViewById(R.id.reg_edit_email);
        password_edit =  findViewById(R.id.reg_edit_password);
        phone_edit = findViewById(R.id.reg_edit_phone);
        name_edit = findViewById(R.id.reg_edit_name);
        link_edit = findViewById(R.id.reg_edit_link);
        registerButton =  findViewById(R.id.reg_reg_button);
        viewDialog = new ViewDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
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
        setContentView(R.layout.register_layout);
        setTitle("Регистрация");

        setup();

        registerButton.setOnClickListener(new View.OnClickListener() {
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
                final String name = name_edit.getText().toString();
                final String link = link_edit.getText().toString();
                final String phone = phone_edit.getText().toString();

                if(TextUtils.isEmpty(name)){
                    name_edit.setError("Пожалуйста введите имя.");
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    email_edit.setError("Пожалуйста введите e-mail.");
                    return;
                }
                Pattern pattern = Pattern.compile("[a-zA-Z0-9.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
                Matcher matcher = pattern.matcher(email);
                if(!matcher.matches()){
                    email_edit.setError("Введите e-mail правильно пожалуйста.");
                    return;
                }

                if(TextUtils.isEmpty(phone)){
                    phone_edit.setError("Пожалуйста введите мобильный телефон.");
                    return;
                }
                pattern = Pattern.compile("(8|\\+7)\\d{10}");
                matcher = pattern.matcher(phone);
                if(!matcher.matches()){
                    phone_edit.setError("Введите телефон в формате +79991234567.");
                    return;
                }

                if(TextUtils.isEmpty(link)){
                    link_edit.setError("Пожалуйста введите ссылку.");
                    return;
                }

                pattern = Pattern.compile("(https?|ftp)://(-\\.)?([^\\s/?.#-]+\\.?)+([^\\s]*)");
                matcher = pattern.matcher(link);
                if(!matcher.matches()){
                    link_edit.setError("Введите ссылку в формате https://vk.com/id0");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    password_edit.setError("Пожалуйста введите пароль.");
                    return;
                }

                if(password.length()<6){
                    Toast.makeText(getApplicationContext(),"Пароль должен быть длиннее 6 символов!",Toast.LENGTH_SHORT).show();
                    return;
                }
                viewDialog.showDialog();
                firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull final Task<AuthResult> task) {
                                try {
                                    if (task.isSuccessful()) {
                                        final FirebaseUser user = firebaseAuth.getCurrentUser();
                                        String id = user.getUid();
                                        addUser(id, phone, link, name, user);
                                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                        Toast.makeText(getApplicationContext(), "Email уже зарегистрирован!", Toast.LENGTH_SHORT).show();
                                        viewDialog.hideDialog();
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Ошибка регистрации.", Toast.LENGTH_SHORT).show();
                                        viewDialog.hideDialog();
                                    }
                                }
                                catch (Exception ignored){
                                    viewDialog.hideDialog();
                                }
                            }
                        });
            }
        });

    }
}
