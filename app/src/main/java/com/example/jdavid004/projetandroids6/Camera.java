package com.example.jdavid004.projetandroids6;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jdavid004 on 26/02/19.
 */

/**
 * Class Camera : contains all the necessary methods to take an image and load it from the gallery
 */

public class Camera {

    private static final int GALLERY_REQUEST = 1314;
    private static final int REQUEST_TAKE_PHOTO = 1;

    /**
     * Main context
     */
    private Context TheThis;

    /**
     * Main activity
     */
    private Activity activity;

    /**
     * Constructor
     * @param TheThis
     */

    Camera(Context TheThis){
        this.TheThis = TheThis;
        this.activity = (Activity) TheThis;
    }

    /**
     * Create a file to save the image taken by
     * @return image creates
     * @throws IOException
     */

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = TheThis.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

    public String dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                Camera cam = new Camera(TheThis);
                photoFile = cam.createImageFile();

            } catch (IOException ex) {
                ex.printStackTrace();// Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(TheThis,
                        "com.example.jdavid004.projetandroids6.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                Log.i("Photo", "Photo prise et sauvegardé dans un fichier");
            }
        }
        return photoFile.getAbsolutePath();


    }

    /**
     * create the intent and launch the activity
     */

    protected void getImageFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(galleryIntent,GALLERY_REQUEST);
    }

    /**
     * load an image from the gallery
     * @param data
     * @param currentPicture
     * @param originalPicture
     * @param img
     */

    public void onSelectFromGalleryResult(Intent data, Picture currentPicture, Picture originalPicture, ZoomageView img){
        Bitmap bmp = currentPicture.getBmp();
        if(data != null){
            try{
                bmp = MediaStore.Images.Media.getBitmap(TheThis.getContentResolver(),data.getData());
            }catch (IOException e ){
                Toast.makeText(TheThis, "Failed to access to gallery", Toast.LENGTH_SHORT).show();
            }
            currentPicture.setBmp(bmp);
            img.setImageBitmap(currentPicture.getBmp());
            originalPicture.setBmp(bmp);

        }
    }
}
