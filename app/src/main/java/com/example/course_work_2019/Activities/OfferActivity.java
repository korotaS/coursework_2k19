package com.example.course_work_2019.Activities;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.course_work_2019.Models.Offer;
import com.example.course_work_2019.R;
import com.example.course_work_2019.Other.ViewDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OfferActivity extends AppCompatActivity {

    String[] data = {"Не выбрана", "Халява","Спорт и инвентарь", "Фото","Книги","Животные",
                     "Помощь","Утерянные вещи","Одежда и обувь","Услуги","Разные вещи"};
    boolean isAllCorrect = true;
    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageView img4;
    ImageView img5;
    RadioGroup radioGroup;
    ArrayList<ImageView> imgs = new ArrayList<>();
    ArrayList<Uri> uris = new ArrayList<>();
    TextView photoText;
    int photoCount = 0;
    FirebaseFirestore db;
    StorageReference storageReference;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    String currentOfferId;
    String currentUserId;
    Offer currentOffer;
    ViewDialog viewDialog;
    Animation animAlpha;
    Spinner spinner;
    Button ph, but;

//    public OfferActivity() {
//
//    }

    private void addOffer(String name, String city, String cat,
                          String desc, String pers_name, String phone, String soc,
                          final int numPhotos){
        final ViewDialog viewDialog = new ViewDialog(this);
        viewDialog.showDialog();
        currentOffer = new Offer();
        currentOffer.setUser_id(currentUserId);
        currentOffer.setName(name);
        currentOffer.setCity(city);
        currentOffer.setCategory(cat);
        currentOffer.setDescription(desc);
        currentOffer.setPers_name(pers_name);
        currentOffer.setPhone(phone);
        currentOffer.setSoc_netw(soc);
        currentOffer.setNumPhotos((long)numPhotos);
        currentOffer.setPublished(false);
        currentOffer.setFind_or_offer(radioGroup.getCheckedRadioButtonId() == R.id.radio_find);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy, HH-mm-ss");
        Date date = new Date(System.currentTimeMillis());
        currentOffer.setDate(sdf.format(date));
        db.collection("offers").add(currentOffer).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {
                documentReference.update("offer_id", documentReference.getId());
                currentOfferId = documentReference.getId();
                for(int i = 0; i < uris.size(); i++){
                    StorageReference sf;
                    final int pos = i;
                    sf = storageReference.child("test_photos/user_" +
                            currentUserId + "/offer_" +
                            currentOfferId + "/" + i + ".jpg");
                    sf.putFile(uris.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if(pos == 0){
                                        documentReference.update("main_photo", uri.toString());

                                    }
                                    documentReference.update("photos", FieldValue.arrayUnion(uri.toString()));
                                    if(pos == numPhotos-1){
                                        Toast.makeText(OfferActivity.this,
                                                "Предложение добавлено!",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(OfferActivity.this, ChooseActivity.class);
                                        startActivityForResult(intent1, 0);
                                        viewDialog.hideDialog();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OfferActivity.this, "Offer not added",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    void wrongTextInputHandler(TextInputEditText inp, boolean isPhone){
        Pattern pattern = Pattern.compile("(8|\\+7)\\d{10}");
        Matcher matcher = pattern.matcher(inp.getText());
        if(Objects.requireNonNull(inp.getText()).length() == 0){
            isAllCorrect = false;
            inp.setError("Заполните это поле пожалуйста.");
        }
        else if(isPhone && !matcher.matches()){
            isAllCorrect = false;
            inp.setError("Введите телефон в формате +79991234567");
        }
    }

    boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    void setup() {
        photoText = findViewById(R.id.off_text_photo);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        radioGroup = findViewById(R.id.radioGroup);
        viewDialog = new ViewDialog(this);
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        if(auth.getCurrentUser() != null){
            currentUser = auth.getCurrentUser();
            currentUserId = currentUser.getUid();
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        storageReference = FirebaseStorage.getInstance().getReference();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = findViewById(R.id.off_cat_spinner);
        spinner.setAdapter(adapter);
        but = findViewById(R.id.off_final_button);
        ph = findViewById(R.id.off_photo_button);
        img1 = findViewById(R.id.off_image1);
        img2 = findViewById(R.id.off_image2);
        img3 = findViewById(R.id.off_image3);
        img4 = findViewById(R.id.off_image4);
        img5 = findViewById(R.id.off_image5);
        imgs.add(img1);
        imgs.add(img2);
        imgs.add(img3);
        imgs.add(img4);
        imgs.add(img5);
        for (int i = 0; i < 5; i++) {
            ImageView img = imgs.get(i);
            final int pos = i;
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < 5; j++) {
                        imgs.get(j).setVisibility(View.GONE);
                        imgs.get(j).setImageResource(0);
                    }
                    uris.remove(pos);
                    photoCount--;
                    for (int j = 0; j < photoCount; j++) {
                        imgs.get(j).setVisibility(View.VISIBLE);
                        Glide.with(getApplicationContext())
                                .load(uris.get(j))
                                .apply(new RequestOptions().override(300,300))
                                .into(imgs.get(j));
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(getApplicationContext(), ChooseActivity.class);
        startActivityForResult(myIntent, 0);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), ChooseActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.offer_layout);
        setTitle("Мое предложение");

        setup();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_find:
                        but.setText("Ищу!");
                        break;
                    case R.id.radio_offer:
                        but.setText("Предлагаю!");
                        break;
                }
            }
        });

        final TextInputEditText name = findViewById(R.id.input_name);
        final TextView test = findViewById(R.id.off_test);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                test.setText(getString(R.string.show_test, 32-s.toString().length()));
            }
        });
        final TextInputEditText desc = findViewById(R.id.input_desc);
        final TextView test2 = findViewById(R.id.off_test2);
        desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                test2.setText(getString(R.string.show_test, 200-s.toString().length()));
            }
        });
        final TextInputEditText phone = findViewById(R.id.input_phone);
        final TextInputEditText soc = findViewById(R.id.input_soc);
        final TextInputEditText city = findViewById(R.id.input_city);
        final TextInputEditText pers_name = findViewById(R.id.input_pers_name);
        final TextInputEditText[] editTexts = {name, desc, phone, soc, city, pers_name};
        viewDialog.showDialog();
        db.collection("users")
                .document("user_" + currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        pers_name.setText(documentSnapshot.getString("name"));
                        soc.setText(documentSnapshot.getString("link"));
                        phone.setText(documentSnapshot.getString("phone"));
                        viewDialog.hideDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        viewDialog.hideDialog();
                    }
                });

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllCorrect = true;
                v.startAnimation(animAlpha);
                if(!checkInternetConnection()){
                    Toast.makeText(getApplicationContext(),
                            "Отсутствует подключение к интернету!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(spinner.getSelectedItemPosition() == 0){
                    isAllCorrect = false;
                    TextView errorText = (TextView)spinner.getSelectedView();
                    errorText.setError("Выберите категорию пожалуйста.");
                }
                for(int i = 0; i < 6; ++i){
                    wrongTextInputHandler(editTexts[i], i == 2);
                }
                if(photoCount == 0){
                    isAllCorrect = false;
                }

                if(TextUtils.isEmpty(soc.getText())){
                    soc.setError("Заполните это поле пожалуйста.");
                    return;
                }

                Pattern pattern = Pattern.compile("(https?|ftp)://(-\\.)?([^\\s/?.#-]+\\.?)+([^\\s]*)");
                Matcher matcher = pattern.matcher(soc.getText());
                if(!matcher.matches()){
                    soc.setError("Введите ссылку в формате https://vk.com/id0");
                    isAllCorrect = false;
                }

                if(isAllCorrect){
                    addOffer(name.getText().toString(),
                            city.getText().toString(),
                            spinner.getSelectedItem().toString(),
                            desc.getText().toString(),
                            pers_name.getText().toString(),
                            phone.getText().toString(),
                            soc.getText().toString(),
                            uris.size());
                }
            }
        });

        ph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                if(!checkInternetConnection()){
                    Toast.makeText(getApplicationContext(),
                            "Отсутствует подключение к интернету!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(photoCount == 5){
                    Toast.makeText(OfferActivity.this,
                            "Уже есть 5 фотографий!",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, 1);
                }

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if(data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    uris.add(selectedImageUri);
                    imgs.get(photoCount).setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(selectedImageUri)
                            .apply(new RequestOptions().override(300,300))
                            .into(imgs.get(photoCount++));
                }
                else if (data.getClipData() != null) {
                    for(ImageView img: imgs){
                        img.setImageResource(0);
                    }
                    ClipData mClipData = data.getClipData();
                    if(mClipData.getItemCount() > 5){
                        Toast.makeText(this,
                                "Слишком много фотографий! Выберите не более 5.",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        uris.clear();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            uris.add(uri);
                            imgs.get(i).setVisibility(View.VISIBLE);
                            Glide.with(this)
                                    .load(uri)
                                    .apply(new RequestOptions().override(300,300))
                                    .into(imgs.get(i));
                        }
                        photoCount = mClipData.getItemCount();
                    }
                }
            }
        }
    }
}
