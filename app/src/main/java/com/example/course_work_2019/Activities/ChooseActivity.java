package com.example.course_work_2019.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.course_work_2019.R;
import com.google.firebase.firestore.FirebaseFirestore;


public class ChooseActivity extends AppCompatActivity {

    Button but1, but2, but3;
    private int CODE_1 = 0;
    private int CODE_2 = 0;
    FirebaseFirestore db;
    Animation animAlpha;

    boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    void setup() {
        db = FirebaseFirestore.getInstance();
        but1 = findViewById(R.id.button_find);
        but2 = findViewById(R.id.button_offer);
        but3 = findViewById(R.id.button_pers_page);

        float scale = 0.09765625f;

        Drawable find = getResources().getDrawable(R.drawable.search);
        find.setBounds(0, 0, (int)(find.getIntrinsicWidth()*scale),
                (int)(find.getIntrinsicHeight()*scale));
        ScaleDrawable sd_find = new ScaleDrawable(find, 0, scale,scale);

        Drawable person = getResources().getDrawable(R.drawable.person1);
        person.setBounds(0, 0, (int)(person.getIntrinsicWidth()*0.9090909f),
                (int)(person.getIntrinsicHeight()*0.9090909f));
        ScaleDrawable sd_person = new ScaleDrawable(person, 0, 0.9090909f,0.9090909f);

        Drawable offer = getResources().getDrawable(R.drawable.offer1);
        offer.setBounds(0, 0, (int)(offer.getIntrinsicWidth()*0.10352f),
                (int)(offer.getIntrinsicHeight()*0.10352f));
        ScaleDrawable sd_offer = new ScaleDrawable(offer, 0, 0.10352f,0.10352f);

        but1.setCompoundDrawables(sd_find.getDrawable(), null, null, null);
        but2.setCompoundDrawables(sd_offer.getDrawable(), null, null, null);
        but3.setCompoundDrawables(sd_person.getDrawable(), null, null, null);

        animAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
    }

    @Override
    public void onBackPressed() {
        //do nothing on pressing back button
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.choose_layout);
        setTitle("Что хотите?");

        setup();

        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                if(!checkInternetConnection()){
                    Toast.makeText(getApplicationContext(),
                            "Отсутствует подключение к интернету!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent1 = new Intent(ChooseActivity.this, FindActivity.class);
                startActivityForResult(intent1, CODE_1);
            }
        });

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
                Intent intent1 = new Intent(ChooseActivity.this, OfferActivity.class);
                startActivityForResult(intent1, CODE_2);
            }
        });

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
                Intent intent1 = new Intent(ChooseActivity.this, PersPageActivity.class);
                startActivityForResult(intent1, CODE_2);
            }
        });
    }
}
