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
        currentPictureUse = new Picture(originalPictureUse.getBmp());
        imageView.setImageBitmap(currentPictureUse.getBmp());

        seekbarlum = (SeekBar)findViewById(R.id.seekbarlum);
        seekbarlum.setVisibility(View.GONE);
        seekbarlum.setOnSeekBarChangeListener(this);
        seekbarlum.setMax(200);
        textLumi = (TextView) findViewById(R.id.textLumi);
        textLumi.setVisibility(View.GONE);


        mDefaultColor = ContextCompat.getColor(MainActivity.this, R.color.colorPrimary);
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
            // click on the camera icon to access the camera of the phone
            case R.id.camera:
                dispatchTakePictureIntent(); // Take a photo with a camera app
                return true;

            // click on the arrow to cancel effects
            case R.id.reset:
                currentPictureUse = new Picture(originalPictureUse.getBmp());
                imageView.setImageBitmap(currentPictureUse.getBmp()); // On oublie pas de réafficher l'image
                return true;

            case R.id.toGrey:
                //currentPictureUse.toGrey();
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
                //currentPictureUse.contrastDynamicExtensionRS(getApplicationContext());
                return true;

            case R.id.contrastEqualHisto:
                currentPictureUse.contrastHistogramEqualizationYuvRS(getApplicationContext());
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
                Convolution blur = new Convolution(currentPictureUse, matrixMoy, mWidthMoy, mHeightMoy, false, true);
                //blur.compute();
                blur.computeRS(getApplicationContext());
                //blur.computeIntrinsicConvolve(getApplicationContext());
                return true;

            case R.id.gaussien:
                int mWidthGauss = 3;
                int mHeightGauss = mWidthGauss;
                int [][] matrixGauss = { {1,2,1},
                                         {2,4,2},
                                         {1,2,1} };
                Convolution gaussien = new Convolution(currentPictureUse, matrixGauss, mWidthGauss, mHeightGauss, false, true);
                //gaussien.compute();
                //gaussien.computeRS(getApplicationContext());
                gaussien.computeIntrinsicGaussianBlur(getApplicationContext(), 3);
                return true;

            case R.id.prewitt:
                int mWidthPrewitt = 3;
                int mHeightPrewitt = mWidthPrewitt;
                int[][] matrixPrewitt = { {-1,0,1},
                                          {-1,0,1},
                                          {-1,0,1} };
                Convolution contourPrewitt = new Convolution(currentPictureUse, matrixPrewitt, mWidthPrewitt, mHeightPrewitt,true, false);
                //contourPrewitt.compute();
                contourPrewitt.computeRS(getApplicationContext());
                //contourPrewitt.computeIntrinsicConvolve(getApplicationContext());
                return true;

            case R.id.sobel:
                int mWidthSobel = 3;
                int mHeightSobel = mWidthSobel;
                int[][] matrixSobel = { {-1,0,1},
                                        {-2,0,2},
                                        {-1,0,1} };
                Convolution contourSobel= new Convolution(currentPictureUse, matrixSobel, mWidthSobel, mHeightSobel,true, false);
                //contourSobel.compute();
                //contourSobel.computeRS(getApplicationContext());
                contourSobel.computeIntrinsicConvolve(getApplicationContext());
                return true;

            case R.id.laplacien:
                int mWidthLaplacien = 3;
                int mHeightLaplacien = mWidthLaplacien;
                int [][] matrixLaplacien = { {0,1,0},
                                             {1,-4,1},
                                             {0,1,0} };
                Convolution contourLaplacien = new Convolution(currentPictureUse, matrixLaplacien, mWidthLaplacien, mHeightLaplacien, false, false);
                //contourLaplacien.compute();
                //contourLaplacien.computeRS(getApplicationContext());
                contourLaplacien.computeIntrinsicConvolve(getApplicationContext());
                return true;

            case R.id.Luminosity:
                seekbarlum.setProgress(100);
                seekbarlum.setVisibility(View.VISIBLE);
                textLumi.setVisibility(View.VISIBLE);
                copycurrentPictureUse = new Picture(currentPictureUse.getBmp());
                return true;

            //click on the import icon to access to the gallery
            case R.id.importFromGallery:
                getImageFromGallery();
                return true;

            case R.id.saveImage:
                //check if the permission to write in the storage is already granted
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
    /**
     * Treats the result of the activity depending on the request
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case REQUEST_TAKE_PHOTO: { //to take a photo using the camera
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
            case(GALLERY_REQUEST): { //to import a photo from the gallery
                if(resultCode == Activity.RESULT_OK){
                    onSelectFromGalleryResult(data);
                }
            }
        }
    }

    /**
     * Create an image file
     * @return the file
     * @throws IOException
     */
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

    /**
     * requestStoragePermission : if the permission to access to the gallery is not already granted, asks permission
     */
    public void requestStoragePermission(){
        //if the permission has already been asked and denied, create a dialog to explain why we need the permission
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)  //creates a dialog with the user
                    .setTitle("Permission needed")
                    .setMessage("Permission to access to the storage is needed to save the image")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        //if the user clicks on "OK", asks permission again
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        //if the user clicks on "CANCEL", shut the dialog
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            //asks permission to write in the storage
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    /**
     * Callback for the result from requesting permissions
     * @param requestCode : code of the permission
     * @param permissions : requested permissions
     * @param grantResults : grant results for the corresponding permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE){ //if the permission requested is to access to the storage
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //if the permission is granted
                Toast.makeText(this, "Permission granted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
            }
        }
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //currentPictureUse.AdjustLuminosity(seekBar.getProgress(),copycurrentPictureUse);
        currentPictureUse.AdjustLuminosityRS(getApplicationContext(),seekBar.getProgress()+25,copycurrentPictureUse);
    }

}