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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.course_work_2019.Other.MyListAdapter;
import com.example.course_work_2019.Models.Offer;
import com.example.course_work_2019.Models.OfferParcelable;
import com.example.course_work_2019.R;
import com.example.course_work_2019.Other.ViewDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FindActivity extends AppCompatActivity {
    String[] data = {"Не выбрана", "Халява","Спорт и инвентарь", "Фото","Книги","Животные",
                     "Помощь","Утерянные вещи","Одежда и обувь","Услуги","Разные вещи"};
    String[] data2 = {"Не выбрано", "Ищу", "Предлагаю"};
    ArrayList<Offer> offer_list = new ArrayList<>();
    ImageView img1;
    FirebaseFirestore db;
    ListView lv;
    FirebaseAuth auth;
    ViewDialog viewDialog;
    Spinner spinner, spinner2;
    String currentUserId;
    boolean isMine;
    boolean start;
    String flag;
    String find_or_offer_flag;
    TextView textView;

    boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                Intent myIntent = new Intent(getApplicationContext(), ChooseActivity.class);
                startActivityForResult(myIntent, 0);
                return true;
            }

            case R.id.menu_1: {
                setTitle("Все предложения");
                isMine = false;
                getFromDb(spinner.getSelectedItemPosition() != 0,
                        spinner.getSelectedItem().toString(), "", find_or_offer_flag);                return true;
            }

            case R.id.menu_2: {
                setTitle("Мои предложения");
                isMine = true;
                flag = "0";
                getFromDb(spinner.getSelectedItemPosition() != 0,
                        spinner.getSelectedItem().toString(), "0", find_or_offer_flag);
                return true;
            }

            case R.id.menu_3: {
                setTitle("Мои опубликованные");
                isMine = true;
                flag = "1";
                getFromDb(spinner.getSelectedItemPosition() != 0,
                        spinner.getSelectedItem().toString(), "1", find_or_offer_flag);
                return true;
            }

            case R.id.menu_4: {
                setTitle("Мои неопубликованные");
                isMine = true;
                flag = "2";
                getFromDb(spinner.getSelectedItemPosition() != 0,
                        spinner.getSelectedItem().toString(), "2", find_or_offer_flag);
                return true;
            }
        }
        return true;
    }

    void setup() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        start = true;
        textView = findViewById(R.id.text_null);
        if (auth.getCurrentUser() != null) {
            currentUserId = auth.getCurrentUser().getUid();
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = findViewById(R.id.find_spinner);
        spinner.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2 = findViewById(R.id.find_spinner2);
        spinner2.setAdapter(adapter2);
        img1 = findViewById(R.id.find_image1);
        lv = findViewById(R.id.find_list_view);
        viewDialog = new ViewDialog(this);
        isMine = false;
        find_or_offer_flag = "0";
    }

    private void getFromDb(final boolean isCatNeeded, final String cat,
                           final String flag, final String find_or_offer) {
        viewDialog.showDialog();
        textView.setVisibility(View.GONE);
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
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy, HH-mm-ss");
                        Date date1 = new Date();
                        Date date2 = null;
                        try {
                            date2 = sdf.parse(document.getString("date"));
                            off.setDate(date2.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long diff = date1.getTime() - date2.getTime();
                        long day_diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                        final ArrayList<String> arr = (ArrayList<String>)document.get("photos");
                        off.setPhotos(arr.toArray(new String[off.getNumPhotos().intValue()]));
                        boolean flag1 = find_or_offer.equals("0");
                        boolean flag2 = find_or_offer.equals("1") && off.isFind_or_offer() ||
                                        find_or_offer.equals("2") && !off.isFind_or_offer();
                        if(isMine) {
                            if(off.getUser_id().equals(currentUserId) &&
                                    (flag.equals("0") || flag.equals("1") && off.isPublished() ||
                                            flag.equals("2") && !off.isPublished()) &&
                                    (!isCatNeeded || off.getCategory().equals(cat)) &&
                                    (flag1 || flag2)) {
                                offer_list.add(off);
                            }
                        }
                        else {
                            if((day_diff == 0 || day_diff == 1) && off.isPublished()
                                    && (!isCatNeeded || off.getCategory().equals(cat))
                                    && (flag1 || flag2)){
                                offer_list.add(off);
                            }
                        }
                    }
                    lv.setAdapter(new MyListAdapter(getBaseContext(), offer_list, isMine));
                    viewDialog.hideDialog();
                    start = false;
                    if(offer_list.size() == 0){
                        textView.setText("Не найдено предложений по заданным параметрам.");
                        textView.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    viewDialog.hideDialog();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(getApplicationContext(), ChooseActivity.class);
        startActivityForResult(myIntent, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.find_layout);
        setTitle("Все предложения");

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

                getFromDb(position != 0, spinner.getSelectedItem().toString(),
                        flag, find_or_offer_flag);

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

                    getFromDb(spinner.getSelectedItemPosition() != 0, spinner.getSelectedItem().toString(),
                            flag, find_or_offer_flag);
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
                Intent intent1 = new Intent(FindActivity.this, ShowOfferActivity.class);
                intent1.putExtra("offer", new OfferParcelable(offer_list.get(position)));
                intent1.putExtra("isAdmin", false);
                intent1.putExtra("isMine", isMine);
                startActivityForResult(intent1, 0);
            }
        });
    }
}
