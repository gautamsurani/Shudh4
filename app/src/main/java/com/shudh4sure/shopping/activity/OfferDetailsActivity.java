package com.shudh4sure.shopping.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.shudh4sure.shopping.R;
import com.shudh4sure.shopping.utils.TouchImageView;


public class OfferDetailsActivity  extends AppCompatActivity{

    String imgURl="";
    TouchImageView imageZoom;
    ProgressBar progressBarZoom;
    Toolbar toolbarZoom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoomimg_view);
        imageZoom = (TouchImageView)findViewById(R.id.imageZoom);
        progressBarZoom= (ProgressBar)findViewById(R.id.progressBarZoom);
        toolbarZoom= (Toolbar)findViewById(R.id.toolbarZoom);

        toolbarZoom.setTitle(getResources().getString(R.string.toolbar_offers));
        setSupportActionBar(toolbarZoom);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b=getIntent().getExtras();
        if (b!=null){

            imgURl=b.getString("IMG");

            Glide.with(this)
                    .load(imgURl)
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_default_notification)
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBarZoom.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageZoom);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}
