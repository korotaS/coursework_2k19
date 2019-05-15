package com.example.course_work_2019.Other;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.example.course_work_2019.R;

public class ViewDialog {
    private Activity activity;
    private Dialog dialog;
    public ViewDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {
        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.gif_layout);
        ImageView gifImageView = dialog.findViewById(R.id.custom_loading_imageView);
        DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(gifImageView);
        Glide.with(activity)
                .load(R.drawable.pac)
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.pac))
                .into(imageViewTarget);
        dialog.show();
    }
    public void hideDialog(){
        dialog.dismiss();
    }
}
