package com.example.jdavid004.projetandroids6;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private ImageView img;
    Picture picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        img = findViewById(R.id.vegetablePicture);
        this.picture = new Picture(getResources());
        img.setImageBitmap(picture.getBmp());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            // Cas où on clique sur la caméra pour accéder à l'appareil photo.
            case R.id.camera:
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent,0);
                return true;
            case R.id.toGrey:
                picture.toGray(picture.getBmp());
            case R.id.contrastMenu:
                Toast.makeText(this,"menu selected", Toast.LENGTH_SHORT);
                return true;

            case R.id.convolutionMenu:
                Toast.makeText(this,"menu selected", Toast.LENGTH_SHORT);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
