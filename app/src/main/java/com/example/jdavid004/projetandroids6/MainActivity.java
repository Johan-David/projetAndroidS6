package com.example.jdavid004.projetandroids6;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Allocation;
import android.widget.ImageView;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.vegetablePicture);
        Picture picture = new Picture(getResources());
        img.setImageBitmap(picture.getBmp());
    }





}
