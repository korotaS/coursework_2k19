package com.example.course_work_2019.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.course_work_2019.Models.Offer;
import com.example.course_work_2019.Models.OfferParcelable;
import com.example.course_work_2019.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ShowOfferActivity extends AppCompatActivity {
    FirebaseFirestore db;
    Offer currentOffer;
    OfferParcelable offerParcelable;
    boolean isAdmin;
    boolean isMine;

    boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent;
        if(isAdmin){
            myIntent = new Intent(getApplicationContext(), AdminActivity.class);
        }
        else {
            myIntent = new Intent(getApplicationContext(), FindActivity.class);
        }
        startActivityForResult(myIntent, 0);
        return true;
    }

    void setup() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        db = FirebaseFirestore.getInstance();
        offerParcelable = getIntent().getParcelableExtra("offer");
        currentOffer = offerParcelable.makeOffer();
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        isMine = getIntent().getBooleanExtra("isMine", false);
    }

    @Override
    public void onBackPressed() {
        if(isAdmin){
            Intent myIntent = new Intent(getApplicationContext(), AdminActivity.class);
            startActivityForResult(myIntent, 0);
        }
        else {
            Intent myIntent = new Intent(getApplicationContext(), FindActivity.class);
            startActivityForResult(myIntent, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Предложение");
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.show_offer_layout);

        setup();

        final TextView name = findViewById(R.id.show_off_name2);
        final TextView desc = findViewById(R.id.show_off_desc2);
        final TextView city = findViewById(R.id.show_off_city2);
        final TextView phone = findViewById(R.id.show_off_phone2);
        final TextView soc = findViewById(R.id.show_off_soc2);
        final TextView cat = findViewById(R.id.show_off_cat2);
        final TextView pers_name = findViewById(R.id.show_off_pers_name2);
        final ImageView img1 = findViewById(R.id.show_off_image1);
        final ImageView img2 = findViewById(R.id.show_off_image2);
        final ImageView img3 = findViewById(R.id.show_off_image3);
        final ImageView img4 = findViewById(R.id.show_off_image4);
        final ImageView img5 = findViewById(R.id.show_off_image5);
        final ImageView[] imgs = {img1, img2, img3, img4, img5};
        for(ImageView img : imgs){
            img.setVisibility(View.GONE);
        }
        Button button = findViewById(R.id.show_offer_button);
        Button button2 = findViewById(R.id.show_offer_button2);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        button.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        button2.setVisibility(isAdmin || isMine ? View.VISIBLE : View.GONE);
        name.setText(currentOffer.getName());
        String cate;
        if(currentOffer.isFind_or_offer()) {
            cate = "Ищу, " + currentOffer.getCategory();
        }
        else {
            cate = "Предлагаю, " + currentOffer.getCategory();
        }
        cat.setText(cate);
        city.setText(currentOffer.getCity());
        desc.setText(currentOffer.getDescription());
        phone.setText(currentOffer.getPhone());
        pers_name.setText(currentOffer.getPers_name());
        soc.setText(currentOffer.getSoc_netw());
        for(int i = 0; i < currentOffer.getNumPhotos().intValue(); i++){
            imgs[i].setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext())
                    .load(Uri.parse(currentOffer.getPhotos()[i]))
                    .apply(new RequestOptions().override(300,300))
                    .into(imgs[i]);
        }
        if(isAdmin){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(animAlpha);
                    if(!checkInternetConnection()){
                        Toast.makeText(getApplicationContext(),
                                "Отсутствует подключение к интернету!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.collection("offers")
                            .document(currentOffer.getOffer_id())
                            .update("published", true)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ShowOfferActivity.this,
                                            "Опубликовано!",
                                            Toast.LENGTH_SHORT).show();
                                    Intent myIntent = new Intent(getApplicationContext(), AdminActivity.class);
                                    startActivityForResult(myIntent, 0);
                                }
                            });
                }
            });
        }
        if(isAdmin || isMine){
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(animAlpha);
                    if(!checkInternetConnection()){
                        Toast.makeText(getApplicationContext(),
                                "Отсутствует подключение к интернету!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.collection("offers")
                            .document(currentOffer.getOffer_id())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ShowOfferActivity.this,
                                            "Удалено!",
                                            Toast.LENGTH_SHORT).show();
                                    Intent myIntent;
                                    if(isAdmin) {
                                        myIntent = new Intent(getApplicationContext(), AdminActivity.class);
                                    }
                                    else {
                                        myIntent = new Intent(getApplicationContext(), FindActivity.class);
                                    }
                                    startActivityForResult(myIntent, 0);
                                }
                            });
                }
            });
        }

    }
}
