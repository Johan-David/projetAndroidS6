package com.example.jdavid004.projetandroids6;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;



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
