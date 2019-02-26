package com.example.jdavid004.projetandroids6;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import yuku.ambilwarna.AmbilWarnaDialog;




public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{
    /* color picker variable */
    private int mDefaultColor = 0;
    private int colorPickerOption = 0;


    private ZoomageView imageView;
    private Picture originalPictureUse;
    private Picture currentPictureUse;
    private Picture copycurrentPictureUse;
    private SeekBar seekbarlum;
    private TextView textLumi;
    private String currentPhotoPath;
    private static final int GALLERY_REQUEST = 1314;
    private static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.vegetablePicture);
        originalPictureUse = new Picture(getResources());
        currentPictureUse = new Picture(originalPictureUse);
        imageView.setImageBitmap(currentPictureUse.getBmp());

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
                    currentPictureUse.colorizeRS(getApplicationContext(), mDefaultColor);
                }
                if(colorPickerOption == 2){
                    currentPictureUse.colorOnlyHsvRS(getApplicationContext(), mDefaultColor);
                }
            }
        });
        colorPicker.show();
    }

    @Override
    /**
     * Set up the menu to choose the different image processing options.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    /**
     * Organise the different menu use by the application. It's a link between the method use for a picture and the  xml file : example_menu.xml
     */

    public boolean onOptionsItemSelected(MenuItem item) {
        seekbarlum.setVisibility(View.GONE);
        textLumi.setVisibility(View.GONE);

        switch(item.getItemId()){
            // Cas où on clique sur la caméra pour accéder à l'appareil photo.
            case R.id.camera:
                Camera cam = new Camera(this);
                currentPhotoPath = cam.dispatchTakePictureIntent(); // Take a photo with a camera app
                return true;
            // Cas où on clique sur la flèche pour annuler un effet.
            case R.id.reset:
                currentPictureUse = new Picture(originalPictureUse);
                imageView.setImageBitmap(currentPictureUse.getBmp()); // On oublie pas de réafficher l'image

                return true;
            case R.id.toGrey:
                currentPictureUse.toGreyRS(getApplicationContext());
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
                currentPictureUse.contrastDynamicExtensionRGBAverage();
                return true;
            case R.id.contrastEqualHisto:
                currentPictureUse.contrastHistogramEqualizationYuvRS(getApplicationContext());
                return true;
            case R.id.moyenneur:
                int[][] matriceMoy = new int[3][3];
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        matriceMoy[i][j]=1;
                    }
                }
                Convolution blur3x3 = new Convolution(currentPictureUse, matriceMoy, 3, 3, false);
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
                Convolution contourPrewitt = new Convolution(currentPictureUse, matrice, 3, 3,true);
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
                Convolution contourSobel= new Convolution(currentPictureUse, matrix, 3, 3,true);
                contourSobel.compute();
                return true;
            case R.id.Luminosity:
                seekbarlum.setProgress(100);
                seekbarlum.setVisibility(View.VISIBLE);
                textLumi.setVisibility(View.VISIBLE);
                copycurrentPictureUse = new Picture(currentPictureUse);
                return true;

            case R.id.importFromGallery:
                cam = new Camera(this);
                cam.getImageFromGallery();
                return true;

            case R.id.saveImage:
                Save saveFile = new Save();
                saveFile.SaveImage(this,currentPictureUse.getBmp());
                return true;

        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case REQUEST_TAKE_PHOTO: { /* Dans le cas où l'on prend une photo avec la caméra */
                if(resultCode == RESULT_OK){
                    File file = new File(currentPhotoPath);
                    Bitmap imageBitmap = null;
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),Uri.fromFile(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(imageBitmap != null){
                        originalPictureUse = new Picture(imageBitmap);
                        currentPictureUse = new Picture(imageBitmap);
                        imageView.setImageBitmap(currentPictureUse.getBmp());
                    }
                }
                break;
            }
            case(GALLERY_REQUEST): {
                if(resultCode == Activity.RESULT_OK){
                    Camera cam = new Camera(this);
                    cam.onSelectFromGalleryResult(data, currentPictureUse, originalPictureUse, imageView);
                }
            }
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
        copycurrentPictureUse.AdjustLuminosityRS(getApplicationContext(),seekBar.getProgress(),currentPictureUse);
    }

}