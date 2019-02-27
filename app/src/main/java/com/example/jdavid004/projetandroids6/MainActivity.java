package com.example.jdavid004.projetandroids6;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
    private int STORAGE_PERMISSION_CODE = 1;


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
                dispatchTakePictureIntent(); // Take a photo with a camera app
                return true;
            // Cas où on clique sur la flèche pour annuler un effet.
            case R.id.reset:
                currentPictureUse = new Picture(originalPictureUse);
                imageView.setImageBitmap(currentPictureUse.getBmp()); // On oublie pas de réafficher l'image
                return true;
            case R.id.toGrey:
                currentPictureUse.toGrey();
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
                currentPictureUse.contrasDynamicExtensionRS(getApplicationContext());
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
                getImageFromGallery();
                return true;

            case R.id.saveImage:
                if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    requestStoragePermission();
                }
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
                    onSelectFromGalleryResult(data);
                }
            }
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    /**
     * take a photo and save it
     * @return the name of photo created
     */

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
                currentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                ex.printStackTrace();// Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.jdavid004.projetandroids6.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                Log.i("Photo", "Photo prise et sauvegardé dans un fichier");
            }
        }


    }

    /**
     * create the intent and launch the activity
     */

    protected void getImageFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,GALLERY_REQUEST);
    }

    /**
     * load an image from the gallery
     * @param data
     */

    public void onSelectFromGalleryResult(Intent data){
        Bitmap bmp = currentPictureUse.getBmp();
        if(data != null){
            Log.i("t", "PASSE LE TEST IF");
            try{
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
            }catch (IOException e ){
                Toast.makeText(this, "Failed to access to gallery", Toast.LENGTH_SHORT).show();
            }
            currentPictureUse = new Picture(bmp);
            originalPictureUse = new Picture(bmp);
            imageView.setImageBitmap(currentPictureUse.getBmp());
        }
    }

    public void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Permission to access to storage is needed to save the image")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
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