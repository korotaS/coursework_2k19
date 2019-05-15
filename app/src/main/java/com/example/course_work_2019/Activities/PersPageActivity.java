package com.example.course_work_2019.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.course_work_2019.Other.ViewDialog;
import com.example.course_work_2019.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersPageActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser currentUser;
    EditText name_edit, phone_edit, link_edit, email_edit, password_edit;
    TextView name, phone, link, email, password, new_email;
    FirebaseFirestore db;
    ViewDialog viewDialog;
    TextInputLayout pas, em;
    boolean isChanged;
    boolean isChangedEmail;
    Animation animAlpha;

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), ChooseActivity.class);
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
        viewDialog = new ViewDialog(this);
        viewDialog.showDialog();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        name_edit = findViewById(R.id.pers_edit_name);
        phone_edit = findViewById(R.id.pers_edit_phone);
        link_edit = findViewById(R.id.pers_edit_link);
        email_edit = findViewById(R.id.pers_edit_email);
        password_edit = findViewById(R.id.pers_edit_password);
        email = findViewById(R.id.pers_email);
        name = findViewById(R.id.pers_name);
        phone = findViewById(R.id.pers_phone);
        link = findViewById(R.id.pers_link);
        password = findViewById(R.id.pers_password);
        new_email = findViewById(R.id.pers_new_email);
        em = findViewById(R.id.em_layout);
        pas = findViewById(R.id.pas_layout);
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        db.collection("users")
                .document("user_" + currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name.setText(getString(R.string.pers_page_name, documentSnapshot.getString("name")));
                        name_edit.setText(documentSnapshot.getString("name"));
                        link.setText(getString(R.string.pers_page_link, documentSnapshot.getString("link")));
                        link_edit.setText(documentSnapshot.getString("link"));
                        phone.setText(getString(R.string.pers_page_phone, documentSnapshot.getString("phone")));
                        phone_edit.setText(documentSnapshot.getString("phone"));
                        email.setText(getString(R.string.show_off_email, currentUser.getEmail()));
                        viewDialog.hideDialog();
                    }
                });
        isChanged = true;
        isChangedEmail = true;
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(getApplicationContext(), ChooseActivity.class);
        startActivityForResult(myIntent, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.pers_page_layout);
        setTitle("Мои данные");

        setup();

        final Button but = findViewById(R.id.pers_change_button);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                if(!checkInternetConnection()){
                    Toast.makeText(getApplicationContext(),
                            "Отсутствует подключение к интернету!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isChanged){
                    name.setText("Имя");
                    link.setText("Ссылка");
                    phone.setText("Телефон");
                    but.setText("Применить");
                    name_edit.setVisibility(View.VISIBLE);
                    phone_edit.setVisibility(View.VISIBLE);
                    link_edit.setVisibility(View.VISIBLE);
                    isChanged = false;
                }
                else {
                    final String _name = name_edit.getText().toString();
                    final String _link = link_edit.getText().toString();
                    final String _phone = phone_edit.getText().toString();

                    if(TextUtils.isEmpty(_name)){
                        name_edit.setError("Пожалуйста введите имя.");
                        return;
                    }

                    if(TextUtils.isEmpty(_phone)){
                        phone_edit.setError("Пожалуйста введите мобильный телефон.");
                        return;
                    }
                    Pattern pattern = Pattern.compile("(8|\\+7)\\d{10}");
                    Matcher matcher = pattern.matcher(_phone);
                    if(!matcher.matches()){
                        phone_edit.setError("Введите телефон в формате +79991234567");
                        return;
                    }

                    if(TextUtils.isEmpty(_link)){
                        link_edit.setError("Пожалуйста введите ссылку.");
                        return;
                    }

                    pattern = Pattern.compile("(https?|ftp)://(-\\.)?([^\\s/?.#-]+\\.?)+([^\\s]*)");
                    matcher = pattern.matcher(_link);
                    if(!matcher.matches()){
                        link_edit.setError("Введите ссылку в формате https://vk.com/id0");
                        return;
                    }

                    viewDialog.showDialog();

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("phone", _phone);
                    updates.put("link", _link);
                    updates.put("name", _name);

                    db.collection("users")
                            .document("user_" + currentUser.getUid())
                            .update(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    isChanged = true;
                                    name.setText(getString(R.string.pers_page_name, _name));
                                    link.setText(getString(R.string.pers_page_link, _link));
                                    phone.setText(getString(R.string.pers_page_phone, _phone));
                                    name_edit.setVisibility(View.GONE);
                                    link_edit.setVisibility(View.GONE);
                                    phone_edit.setVisibility(View.GONE);
                                    viewDialog.hideDialog();
                                    but.setText("Изменить");
                                }
                            });
                }
            }
        });
        final Button but2 = findViewById(R.id.pers_email_button);
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                if(!checkInternetConnection()){
                    Toast.makeText(getApplicationContext(),
                            "Отсутствует подключение к интернету!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isChangedEmail){
                    em.setVisibility(View.VISIBLE);
                    email_edit.setVisibility(View.VISIBLE);
                    pas.setVisibility(View.VISIBLE);
                    password_edit.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    new_email.setVisibility(View.VISIBLE);
                    isChangedEmail = false;
                }
                else {
                    final String _email = email_edit.getText().toString();
                    String _password = password_edit.getText().toString();

                    if(TextUtils.isEmpty(_email)){
                        email_edit.setError("Пожалуйста введите e-mail.");
                        return;
                    }
                    Pattern pattern = Pattern.compile("[a-zA-Z0-9.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
                    Matcher matcher = pattern.matcher(_email);
                    if(!matcher.matches()){
                        email_edit.setError("Введите e-mail правильно пожалуйста.");
                        return;
                    }

                    if(TextUtils.isEmpty(_password)){
                        password_edit.setError("Пожалуйста введите пароль.");
                        return;
                    }
                    viewDialog.showDialog();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(currentUser.getEmail(), _password);
                    currentUser.reauthenticate(credential)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.updateEmail(_email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        isChangedEmail = true;
                                                        em.setVisibility(View.GONE);
                                                        pas.setVisibility(View.GONE);
                                                        email_edit.setVisibility(View.GONE);
                                                        new_email.setVisibility(View.GONE);
                                                        password_edit.setVisibility(View.GONE);
                                                        password.setVisibility(View.GONE);
                                                        email.setText(getString(R.string.show_off_email, _email));
                                                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(PersPageActivity.this,
                                                                        "Зарегистрирован! Верифицируйте свою почту плз.",
                                                                        Toast.LENGTH_SHORT).show();
                                                                auth.signOut();
                                                                viewDialog.hideDialog();
                                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                                finish();
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),
                                            "Неверный пароль!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        final Button but3 = findViewById(R.id.sign_out_button);
        but3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                if(!checkInternetConnection()){
                    Toast.makeText(getApplicationContext(),
                            "Отсутствует подключение к интернету!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.signOut();
                startActivityForResult(new Intent(getApplicationContext(), MainActivity.class), 0);
            }
        });
    }
}
