package com.example.course_work_2019.Other;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.course_work_2019.Models.Offer;
import com.example.course_work_2019.R;

import java.util.ArrayList;

public class MyListAdapter extends BaseAdapter {
    private ArrayList<Offer> listData;
    private LayoutInflater layoutInflater;
    private Context context;
    private boolean isMine;
    public MyListAdapter(Context aContext, ArrayList<Offer> listData, boolean _isMine) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
        context = aContext;
        isMine = _isMine;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View v, ViewGroup vg) {
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = layoutInflater.inflate(R.layout.list_row, null);
            holder.rel = v.findViewById(R.id.show_relative);
            holder.uName = v.findViewById(R.id.list_row_name);
            holder.uCity = v.findViewById(R.id.list_row_city);
            holder.uCat = v.findViewById(R.id.list_row_cat);
            holder.img1 = v.findViewById(R.id.find_image1);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        Offer offer = listData.get(position);
        holder.uName.setText(offer.getName());
        holder.uCity.setText(offer.getCity());
        String cat;
        if(offer.isFind_or_offer()) {
            cat = "Ищу, " + offer.getCategory();
        }
        else {
            cat = "Предлагаю, " + offer.getCategory();
        }
        holder.uCat.setText(cat);
        if(isMine){
            holder.rel.setBackgroundColor(Color.parseColor(offer.isPublished() ? "#9DE29E" : "#EE9393"));
        }
        Glide.with(context)
                .load(Uri.parse(offer.getMain_photo()))
                .apply(new RequestOptions().override(350,350))
                .into(holder.img1);
        return v;
    }
    static class ViewHolder {
        TextView uName;
        TextView uCity;
        TextView uCat;
        ImageView img1;
        RelativeLayout rel;
    }
}