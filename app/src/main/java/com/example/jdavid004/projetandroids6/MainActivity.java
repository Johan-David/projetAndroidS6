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
                int[][] matriceMoy = new int[3][3];
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        matriceMoy[i][j]=1;
                    }
                }
                Convolution blur3x3 = new Convolution(currentPicture, matriceMoy, 3, 3, false);
                blur3x3.compute();
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
                Convolution contourPrewitt = new Convolution(currentPicture, matrice, 3, 3,true);
                contourPrewitt.compute();
                return true;

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
                Convolution contourSobel= new Convolution(currentPicture, matrix, 3, 3,true);
                contourSobel.compute();
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
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
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
