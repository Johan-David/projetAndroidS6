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
        TheThis = context;  //prend le context actuel
        String file_path = TheThis.getFilesDir().getPath()+NameOfFolder;  //nom du chemin dans lequel enregistrer l'image
        String CurrentDateAndTime = getCurrentDateAndTime();  //écrit la date et l'heure actuelles
        File dir = new File(file_path);   // crée le répertoire à partir du nom du chemin
        dir.setWritable(true);

        if(!dir.exists()){
            dir.mkdirs();
        }




        File file = new File(dir, NameOfFile+CurrentDateAndTime+".jpg");


        try {
            FileOutputStream fOut = new FileOutputStream(file);  //crée un stream à partir du fichier
            ImageToSave.compress(Bitmap.CompressFormat.JPEG, 85, fOut);  //compresse l'image et l'écrit dans le stream
            fOut.flush();
            fOut.close();
            FileCreatedAndAvailable(file);  //vérifie si le fichier a été crée et le rend utilisable
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
