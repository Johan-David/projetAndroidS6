package com.example.jdavid004.projetandroids6;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by achauveau003 on 01/02/19.
 */

public class Save {
    private Context TheThis;
    private String NameOfFolder = "ApplicationImage";
    private String NameOfFile = "MyImage";

    public void SaveImage(Context context, Bitmap ImageToSave ){
        TheThis = context;  //prend le context actuel

        String CurrentDateAndTime = getCurrentDateAndTime();  //écrit la date et l'heure actuelles
        String pictureName = NameOfFile+CurrentDateAndTime+".png"; //nom de l'image à enregistrer

        OutputStream fOut;

        try {
            //root : crée le chemin pour enregistrer l'image
            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + NameOfFolder + File.separator);
            root.mkdirs(); //crée le sous-dossier si nécessaire
            File sdImageMainDirectory = new File(root, pictureName); //crée le fichier pour l'image
            fOut = new FileOutputStream(sdImageMainDirectory); //crée un stream pour écrire la bitmap
            ImageToSave.compress(Bitmap.CompressFormat.PNG, 100, fOut); //compresse l'image vers le stream
            fOut.flush();  //vide le buffer
            fOut.close();  //ferme le fichier stream
            FileCreatedAndAvailable(sdImageMainDirectory);  //vérifie si le fichier a été crée et le rend utilisable
            AbleToSave();

        } catch (Exception e) {
            e.printStackTrace();
            UnableToSave();
        }
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
