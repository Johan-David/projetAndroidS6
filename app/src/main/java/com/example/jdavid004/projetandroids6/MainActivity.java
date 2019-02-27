package com.example.jdavid004.projetandroids6;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.util.Log;


import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{
    /* color picker variable */
    private int mDefaultColor = 0;
    private int colorPickerOption = 0;


    private ZoomageView img;
    private Picture originalPicture;
    private Picture currentPicture;
    private Picture copyCurrentPicture;
    private SeekBar seekbarlum;
    private TextView textLumi;
    private static final int GALLERY_REQUEST = 1314;
    private static final int CAMERA_REQUEST = 1315;

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


        mDefaultColor = ContextCompat.getColor(MainActivity.this, R.color.colorPrimary);
    }


    public void openColorPicker(){
        final AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            public void onCancel(AmbilWarnaDialog dialog) {

            }
            public void onOk(AmbilWarnaDialog dialog, int color) {
                mDefaultColor = color;
                if(colorPickerOption == 1){
                    currentPicture.colorizeRS(getApplicationContext(), mDefaultColor);
                }
                if(colorPickerOption == 2){
                    currentPicture.colorOnlyHsvRS(getApplicationContext(), mDefaultColor);
                }
            }
        });
        colorPicker.show();
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
                getImageFromCamera();
                return true;

            // Cas où on clique sur la flèche pour annuler un effet.
            case R.id.reset:
                currentPicture = new Picture(originalPicture);
                img.setImageBitmap(currentPicture.getBmp()); // On oublie pas de réafficher l'image
                return true;

            case R.id.toGrey:
                currentPicture.toGreyRS(getApplicationContext());
                return true;

            case R.id.colorize:
                colorPickerOption = 1;
                openColorPicker();
                return true;

            case R.id.colorOnly:
                colorPickerOption = 2;
                openColorPicker();
                return true;

            case R.id.contrastDynamicExten:
                currentPicture.contrastDynamicExtensionRGBAverage();
                return true;

            case R.id.contrastEqualHisto:
                currentPicture.contrastHistogramEqualizationYuvRS(getApplicationContext());
                return true;

            case R.id.moyenneur:
                int mWidthMoy = 3;
                int mHeightMoy = mWidthMoy;
                int[][] matrixMoy = new int[mWidthMoy][mHeightMoy];
                for(int i = 0; i < mWidthMoy; i++){
                    for(int j = 0; j < mHeightMoy; j++){
                        matrixMoy[i][j]=1;
                    }
                }
                Convolution blur = new Convolution(currentPicture, matrixMoy, mWidthMoy, mHeightMoy, false, true);
                //blur.compute();
                //blur.computeRS(getApplicationContext());
                blur.computeIntrinsicConvolve(getApplicationContext());
                return true;

            case R.id.gaussien:
                int mWidthGauss = 3;
                int mHeightGauss = mWidthGauss;
                int[][] matrixGauss = new int[mWidthGauss][mHeightGauss];
                matrixGauss[0][0] = 1;
                matrixGauss[0][1] = 2;
                matrixGauss[0][2] = 1;
                matrixGauss[1][0] = 2;
                matrixGauss[1][1] = 4;
                matrixGauss[1][2] = 2;
                matrixGauss[2][0] = 1;
                matrixGauss[2][1] = 2;
                matrixGauss[2][2] = 1;
                Convolution gaussien = new Convolution(currentPicture, matrixGauss, mWidthGauss, mHeightGauss, false, true);
                //gaussien.compute();
                //gaussien.computeRS(getApplicationContext());
                gaussien.computeIntrinsicGaussianBlur(getApplicationContext(), 3);
                return true;

            case R.id.prewitt:
                int mWidthPrewitt = 3;
                int mHeightPrewitt = mWidthPrewitt;
                int[][] matrixPrewitt = new int[mWidthPrewitt][mHeightPrewitt];
                matrixPrewitt[0][0] = -1;
                matrixPrewitt[0][1] = 0;
                matrixPrewitt[0][2] = 1;
                matrixPrewitt[1][0] = -1;
                matrixPrewitt[1][1] = 0;
                matrixPrewitt[1][2] = 1;
                matrixPrewitt[2][0] = -1;
                matrixPrewitt[2][1] = 0;
                matrixPrewitt[2][2] = 1;
                Convolution contourPrewitt = new Convolution(currentPicture, matrixPrewitt, mWidthPrewitt, mHeightPrewitt,true, false);
                //contourPrewitt.compute();
                //contourPrewitt.computeRS(getApplicationContext());
                contourPrewitt.computeIntrinsicConvolve(getApplicationContext());
                return true;

            case R.id.sobel:
                int mWidthSobel = 3;
                int mHeightSobel = mWidthSobel;
                int[][] matrixSobel = new int[mWidthSobel][mHeightSobel];
                matrixSobel[0][0] = -1;
                matrixSobel[0][1] = 0;
                matrixSobel[0][2] = 1;
                matrixSobel[1][0] = -2;
                matrixSobel[1][1] = 0;
                matrixSobel[1][2] = 2;
                matrixSobel[2][0] = -1;
                matrixSobel[2][1] = 0;
                matrixSobel[2][2] = 1;
                Convolution contourSobel= new Convolution(currentPicture, matrixSobel, mWidthSobel, mHeightSobel,true, false);
                //contourSobel.compute();
                //contourSobel.computeRS(getApplicationContext());
                contourSobel.computeIntrinsicConvolve(getApplicationContext());
                return true;

            case R.id.laplacien:
                int mWidthLaplacien = 3;
                int mHeightLaplacien = mWidthLaplacien;
                int [][] matrixLaplacien = new int [mWidthLaplacien][mHeightLaplacien];
                matrixLaplacien[0][0] = 0;
                matrixLaplacien[0][1] = 1;
                matrixLaplacien[0][2] = 0;
                matrixLaplacien[1][0] = 1;
                matrixLaplacien[1][1] = -4;
                matrixLaplacien[1][2] = 1;
                matrixLaplacien[2][0] = 0;
                matrixLaplacien[2][1] = 1;
                matrixLaplacien[2][2] = 0;
                Convolution contourLaplacien = new Convolution(currentPicture, matrixLaplacien, mWidthLaplacien, mHeightLaplacien, false, false);
                //contourLaplacien.compute();
                //contourLaplacien.computeRS(getApplicationContext());
                contourLaplacien.computeIntrinsicConvolve(getApplicationContext());
                return true;

            case R.id.Luminosity:
                seekbarlum.setProgress(100);
                seekbarlum.setVisibility(View.VISIBLE);
                textLumi.setVisibility(View.VISIBLE);
                copyCurrentPicture = new Picture(currentPicture);
                return true;

            case R.id.importFromGallery:
                getImageFromGallery();
                return true;

            case R.id.saveImage:
                Save saveFile = new Save();
                saveFile.SaveImage(this,currentPicture.getBmp());
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    protected void getImageFromCamera(){
        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(cameraIntent,CAMERA_REQUEST);
    }

    //crée l'intent et lance l'activité
    protected void getImageFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == GALLERY_REQUEST){ //si la requête est l'accès à la galerie
            if(resultCode == Activity.RESULT_OK){
                onSelectFromGalleryResult(data);
            }
        }else if(requestCode == CAMERA_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                onCaptureImageResult(data);
            }

        }
    }

    private void onSelectFromGalleryResult(Intent data){
        Bitmap bmp = currentPicture.getBmp();
        if(data != null){
            try{
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
            }catch (IOException e ){
                Toast.makeText(this, "Failed to access to gallery", Toast.LENGTH_SHORT).show();
            }
            currentPicture.setBmp(bmp);
            img.setImageBitmap(currentPicture.getBmp());
        }
    }

    private void onCaptureImageResult(Intent data){
        if(data != null){
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            currentPicture.setBmp(bmp);
            img.setImageBitmap(currentPicture.getBmp());
            originalPicture.setBmp(bmp);
            img.setImageBitmap(originalPicture.getBmp());
        }
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
