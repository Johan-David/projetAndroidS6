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

    private ImageView img;
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
            // Cas où on clique sur la flèche pour annuler un effet.
            case R.id.reset:
                currentPicture.setBmp(originalPicture.getBmp());
                img.setImageBitmap(currentPicture.getBmp()); // On oublie pas de réafficher l'image
                return true;
            case R.id.toGrey:
                currentPicture.toGray();
                return true;
            case R.id.colorize:
                currentPicture.colorizeRS(getApplicationContext());
                return true;
            case R.id.colorOnly:
                currentPicture.redOnlyHsvRS(getApplicationContext());
                return true;
            case R.id.contrastDynamicExten:
                currentPicture.contrastDynamicExtensionRGBAverage();
                return true;
            case R.id.contrastEqualHisto:
                currentPicture.contrastHistogramEqualizationYuvRS(getApplicationContext());
                return true;
            case R.id.prewitt:
                int[][] matrice = new int[3][3];
                matrice[0][0] = -1;
                matrice[0][1] = 0;
                matrice[0][2] = 1;
                matrice[1][0] = -1;
                matrice[1][1] = 0;
                matrice[1][2] = 1;
                matrice[2][0] = -1;
                matrice[2][1] = 0;
                matrice[2][2] = 1;
                Convolution contourPrewitt = new Convolution(matrice, 3, 3,true);
                contourPrewitt.compute(currentPicture.getBmp());
            case R.id.sobel:
                int[][] matrix = new int[3][3];
                matrix[0][0] = -1;
                matrix[0][1] = 0;
                matrix[0][2] = 1;
                matrix[1][0] = -2;
                matrix[1][1] = 0;
                matrix[1][2] = 2;
                matrix[2][0] = -1;
                matrix[2][1] = 0;
                matrix[2][2] = 1;
                Convolution contourSobel= new Convolution(matrix, 3, 3,true);
                contourSobel.compute(currentPicture.getBmp());
                currentPicture.contrastHistogramEqualizationYuvRS(getApplicationContext());
                return true;
            case R.id.Luminosity:
                seekbarlum.setProgress(100);
                seekbarlum.setVisibility(View.VISIBLE);
                textLumi.setVisibility(View.VISIBLE);
                copyCurrentPicture = new Picture(currentPicture);
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
