package com.example.course_work_2019.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.course_work_2019.Other.MyListAdapter;
import com.example.course_work_2019.Models.Offer;
import com.example.course_work_2019.Models.OfferParcelable;
import com.example.course_work_2019.R;
import com.example.course_work_2019.Other.ViewDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {
    String[] data = {"Не выбрана", "Халява","Спорт и инвентарь", "Фото","Книги","Животные",
            "Помощь","Утерянные вещи","Одежда и обувь","Услуги","Разные вещи"};
    String[] data2 = {"Не выбрано", "Ищу", "Предлагаю"};
    ArrayList<Offer> offer_list = new ArrayList<>();
    ImageView img1;
    FirebaseFirestore db;
    ListView lv;
    ViewDialog viewDialog;
    Spinner spinner, spinner2;
    String find_or_offer_flag;
    boolean start;

    boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    void setup() {
        db = FirebaseFirestore.getInstance();
        find_or_offer_flag = "0";
        start = true;
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = findViewById(R.id.find_spinner);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2 = findViewById(R.id.find_spinner2);
        spinner2.setAdapter(adapter2);
        img1 = findViewById(R.id.find_image1);
        spinner.setAdapter(adapter);
        lv = findViewById(R.id.find_list_view);
        viewDialog = new ViewDialog(this);
    }

    void getFromDb(final boolean isCatNeeded, final String cat, final String find_or_offer) {
        viewDialog.showDialog();
        offer_list = new ArrayList<>();
        db.collection("offers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        Offer off = new Offer();
                        off.setUser_id(document.getString("user_id"));
                        off.setOffer_id(document.getString("offer_id"));
                        off.setName(document.getString("name"));
                        off.setDescription(document.getString("description"));
                        off.setCategory(document.getString("category"));
                        off.setCity(document.getString("city"));
                        off.setPers_name(document.getString("pers_name"));
                        off.setPhone(document.getString("phone"));
                        off.setSoc_netw(document.getString("soc_netw"));
                        off.setNumPhotos(document.getLong("numPhotos"));
                        off.setMain_photo(document.getString("main_photo"));
                        off.setPublished(document.getBoolean("published"));
                        off.setFind_or_offer(document.getBoolean("find_or_offer"));
                        final ArrayList<String> arr = (ArrayList<String>)document.get("photos");
                        off.setPhotos(arr.toArray(new String[off.getNumPhotos().intValue()]));
                        boolean flag1 = find_or_offer.equals("0");
                        boolean flag2 = find_or_offer.equals("1") && off.isFind_or_offer() ||
                                        find_or_offer.equals("2") && !off.isFind_or_offer();
                        if(!off.isPublished() && (!isCatNeeded || off.getCategory().equals(cat))
                            && (flag1 || flag2)) {
                            offer_list.add(off);
                        }
                    }
                    lv.setAdapter(new MyListAdapter(getBaseContext(), offer_list, false));
                    start = false;
                    viewDialog.hideDialog();
                }
                else {
                    viewDialog.hideDialog();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.find_layout);
        setTitle("Админ");

        setup();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!checkInternetConnection()){
                    Toast.makeText(getApplicationContext(),
                            "Отсутствует подключение к интернету!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                getFromDb(position != 0,
                        spinner.getSelectedItem().toString(), find_or_offer_flag);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!start) {
                    if(!checkInternetConnection()){
                        Toast.makeText(getApplicationContext(),
                                "Отсутствует подключение к интернету!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    find_or_offer_flag = String.valueOf(position);
                    getFromDb(spinner.getSelectedItemPosition() != 0,
                            spinner.getSelectedItem().toString(), find_or_offer_flag);
                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!checkInternetConnection()){
                    Toast.makeText(getApplicationContext(),
                            "Отсутствует подключение к интернету!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent1 = new Intent(AdminActivity.this, ShowOfferActivity.class);
                intent1.putExtra("offer", new OfferParcelable(offer_list.get(position)));
                intent1.putExtra("isAdmin", true);
                intent1.putExtra("isMine", false);
                startActivityForResult(intent1, 0);
            }
        });
    }
}
