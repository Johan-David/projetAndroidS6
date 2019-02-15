package com.example.jdavid004.projetandroids6;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import static java.security.AccessController.getContext;

import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    private ZoomageView img;
    private Picture originalPicture;
    private Picture currentPicture;
    private Picture copyCurrentPicture;
    private SeekBar seekbarlum;
    private TextView textLumi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.vegetablePicture);
        originalPicture = new Picture(getResources());
        currentPicture = new Picture(originalPicture);
        img.setImageBitmap(currentPicture.getBmp());

        seekbarlum = (SeekBar)findViewById(R.id.seekbarlum);
        seekbarlum.setVisibility(View.GONE);
        seekbarlum.setOnSeekBarChangeListener(this);
        seekbarlum.setMax(300);
        textLumi = (TextView) findViewById(R.id.textLumi);
        textLumi.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Met en place le menu pour choisir les différents traitements d'images.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    /**
     * Fonction utilisée pour faire le lien entre le xml et les fonctions pour les menus
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        seekbarlum.setVisibility(View.GONE);
        textLumi.setVisibility(View.GONE);
        switch(item.getItemId()){
            // Cas où on clique sur la caméra pour accéder à l'appareil photo.
            case R.id.camera:
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent,0);
                return true;
            case R.id.toGrey:
                currentPicture.toGray(currentPicture.getBmp());
                return true;
            case R.id.colorize:
                currentPicture.colorizeRS(currentPicture.getBmp(),getApplicationContext());
                return true;
            case R.id.colorOnly:
                currentPicture.redOnlyHsvRS(currentPicture.getBmp(),getApplicationContext());
                return true;
            case R.id.contrastDynamicExten:
                currentPicture.contrastDynamicExtensionRGBAverage(currentPicture.getBmp());
                return true;
            case R.id.contrastEqualHisto:
                currentPicture.contrastHistogramEqualizationYuvRS(currentPicture.getBmp(),getApplicationContext());
                return true;
            case R.id.Luminosity:
                seekbarlum.setProgress(100);
                seekbarlum.setVisibility(View.VISIBLE);
                textLumi.setVisibility(View.VISIBLE);
                copyCurrentPicture = new Picture(currentPicture);
                return true;



            case R.id.contrastMenu:                                                     // Aide juste au debug
                Toast.makeText(this,"menu selected", Toast.LENGTH_SHORT);
                return true;
            case R.id.convolutionMenu:
                Toast.makeText(this,"menu selected", Toast.LENGTH_SHORT);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        copyCurrentPicture.AdjustLuminosityRS(getApplicationContext(),seekBar.getProgress(),currentPicture);
    }

}
