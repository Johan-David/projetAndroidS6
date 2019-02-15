package com.example.jdavid004.projetandroids6;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by achauveau003 on 01/02/19.
 */

public class Save {
    private Context TheThis;
    private String NameOfFolder = "/ApplicationImage";
    private String NameOfFile = "MyImage";

    public void SaveImage(Context context, Bitmap ImageToSave ){
        TheThis = context;
        String file_path = Environment.getDataDirectory().getAbsolutePath();
        String CurrentDateAndTime = getCurrentDateAndTime();
        File dir = new File(file_path);

        if(!dir.exists()){
            Log.i("Save","dir not exists");
            dir.mkdirs();
        }
        if(dir.exists()){
            Log.i("Save","dir exists");
        }
        dir.setWritable(true);

        if(dir.canWrite()){
            Log.i("Save","canWrite");
        }
        File file = new File(dir, NameOfFile+CurrentDateAndTime+".jpg");

        if(!file.exists()){
            Log.i("Save","file not exists");
        }
/*
        Log.i("Save", "1"+String.valueOf(new File(file_path,NameOfFolder).exists()));
        Log.i("Save", "2"+String.valueOf(new File(file_path,NameOfFolder).mkdirs()));
        Log.i("Save", "3"+String.valueOf(new File(file_path,NameOfFolder).exists()));
        Log.i("Save", "4"+String.valueOf(new File(file_path,NameOfFolder).isDirectory()));
        Log.i("Save", "5"+String.valueOf(new File(file_path,NameOfFolder).getAbsolutePath()));

        File dir = new File(file_path);
        File file = new File(dir, NameOfFile+CurrentDateAndTime+".jpg");
*/
        try {
            Log.i("Save","ok1 file : "+file);
            FileOutputStream fOut = new FileOutputStream(file);
            Log.i("Save","ok2");
            ImageToSave.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            Log.i("Save","ok3");
            fOut.flush();
            Log.i("Save","ok4");
            fOut.close();
            Log.i("Save","ok5");
            FileCreatedAndAvailable(file);
            Log.i("Save","ok6");
            AbleToSave();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            UnableToSave();}
        catch (IOException e) {
            e.printStackTrace();
            UnableToSave();}
    }

    private String getCurrentDateAndTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"); //Format pour la date.
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    private void FileCreatedAndAvailable(File file){
        MediaScannerConnection.scanFile(TheThis, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.e("ExternalStorage","Scanned" + path + ":");
                Log.e("ExternalStorage", "->uri="+uri);
            }
        });
    }

    private void UnableToSave() {
        Toast.makeText(TheThis, "Failed to access to gallery", Toast.LENGTH_SHORT).show();
    }

    private void AbleToSave() {
        Toast.makeText(TheThis, "Picture saved in gallery", Toast.LENGTH_SHORT).show();
    }
}
