package com.example.jdavid004.projetandroids6;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import java.util.Random;


/**
 * Created by bdarmet on 25/01/19.
 */

public class Picture  {

    private Bitmap bmp;
    private int width;
    private int height;
    private int[] tabPixels;

    Picture(Resources resources) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inScaled = false;
        this.bmp = BitmapFactory.decodeResource(resources,R.drawable.vegetables,options);
        this.width = bmp.getWidth();
        this.height = bmp.getHeight();
    }

    public Bitmap getBmp() {
        return bmp;
    }


    void toGray(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());

        for(int i = 0; i < pixels.length; i++){
            int R = Color.red(pixels[i]);
            int G = Color.green(pixels[i]);
            int B = Color.blue(pixels[i]);
            int Grey = (int)(0.3*R+0.59*G+0.11*B);
            pixels[i] = Color.rgb(Grey,Grey,Grey);
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    void toGreyRS(Bitmap bmp, Context context){
        RenderScript rs = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(rs,bmp);
        Allocation output = Allocation.createTyped(rs,input.getType());

        ScriptC_toGrey greyScript = new ScriptC_toGrey(rs);

        greyScript.forEach_toGrey(input,output);

        output.copyTo(bmp);

        input.destroy(); output.destroy();
        greyScript.destroy(); rs.destroy();
    }

    void colorize(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        float[] hsv = new float[3];
        Random Aleatoire = new Random();
        int rand = Aleatoire.nextInt(360);

        for(int i = 0; i < pixels.length; i++){
            Color.RGBToHSV(Color.red(pixels[i]),Color.green(pixels[i]),Color.blue(pixels[i]),hsv);
            hsv[0] = rand;
            pixels[i] = Color.HSVToColor(hsv);
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    void colorizeRS(Bitmap bmp, Context context){
        RenderScript  rs = RenderScript.create(context);

        Allocation  input = Allocation.createFromBitmap(rs,bmp);
        Allocation output = Allocation.createTyped(rs,input.getType());

        ScriptC_colorize colorizeScript = new ScriptC_colorize(rs);

        Random Aleatoire = new Random();
        float value=(float)Aleatoire.nextInt(360);
        colorizeScript.set_rand(value);

        colorizeScript.forEach_colorize(input,output);

        output.copyTo(bmp);

        input.destroy(); output.destroy();
        colorizeScript.destroy(); rs.destroy();
    }

    void redOnlyHsv(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        float[] hsv = new float[3];

        for(int i = 0; i < pixels.length; i++) {
            Color.RGBToHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsv);
            if (hsv[0] >= 15 && hsv[0] <= 345) {
                int R = Color.red(pixels[i]);
                int G = Color.green(pixels[i]);
                int B = Color.blue(pixels[i]);
                int Grey = (int) (0.3 * R + 0.59 * G + 0.11 * B);
                pixels[i] = Color.rgb(Grey, Grey, Grey);
            }
        }
        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    void redOnlyHsvRS(Bitmap bmp, Context context){
        RenderScript rs = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(rs,bmp);
        Allocation output = Allocation.createTyped(rs,input.getType());

        ScriptC_red_only_HSV red_only_HSVScript = new ScriptC_red_only_HSV(rs);

        red_only_HSVScript.forEach_red_only_hsv(input,output);

        output.copyTo(bmp);

        input.destroy(); output.destroy();
        red_only_HSVScript.destroy(); rs.destroy();
    }


    void contrastDynamicExtensionGrey(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        int min = 255;
        int max = 0;
        int[] LUT = new int[256];

        for(int i = 0; i < pixels.length; i++){
            int grey = Color.red(pixels[i]);
            if(grey < min){
                min = grey;
            }
            if(grey > max){
                max = grey;
            }
        }

        for(int ng = 0; ng < 256; ng++){
            int LUTvalue = (255 * (ng - min)) / (max - min);
            if(LUTvalue < 0){
                LUT[ng] = 0;
            }else if(LUTvalue > 255){
                LUT[ng] = 255;
            }else{
                LUT[ng] = LUTvalue;
            }
        }

        for(int i = 0; i < pixels.length; i++){
            int grey = Color.red(pixels[i]);
            pixels[i] = Color.rgb(LUT[grey],LUT[grey],LUT[grey]);
        }

        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    void contrastDynamicExtensionRGBIndie(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        int minRed = 255;
        int maxRed = 0;
        int minGreen = 255;
        int maxGreen = 0;
        int minBlue = 255;
        int maxBlue = 0;

        int[] LUTr = new int[256];
        int[] LUTg = new int[256];
        int[] LUTb = new int[256];

        for(int i = 0; i < pixels.length; i++){
            int red = Color.red(pixels[i]);
            if(red < minRed){
                minRed = red;
            }
            if(red > maxRed){
                maxRed = red;
            }
            int green = Color.green(pixels[i]);
            if(green < minGreen){
                minGreen = green;
            }
            if(green > maxGreen){
                maxGreen = green;
            }
            int blue = Color.blue(pixels[i]);
            if(blue < minBlue){
                minBlue = blue;
            }
            if(blue > maxBlue){
                maxBlue = blue;
            }
        }

        for(int ng = 0; ng < 256; ng++){
            int LUTvalue = (255*(ng-minRed))/(maxRed-minRed);
            if(LUTvalue < 0){
                LUTr[ng] = 0;
            }else if(LUTvalue > 255){
                LUTr[ng] = 255;
            }else{
                LUTr[ng] = LUTvalue;
            }
        }

        for(int ng = 0; ng < 256; ng++){
            int LUTvalue = (255*(ng-minGreen))/(maxGreen-minGreen);
            if(LUTvalue < 0){
                LUTg[ng] = 0;
            }else if(LUTvalue > 255){
                LUTg[ng] = 255;
            }else{
                LUTg[ng] = LUTvalue;
            }
        }

        for(int ng = 0; ng < 256; ng++){
            int LUTvalue = (255*(ng-minBlue))/(maxBlue-minBlue);
            if(LUTvalue < 0){
                LUTb[ng] = 0;
            }else if(LUTvalue > 255){
                LUTb[ng] = 255;
            }else{
                LUTb[ng] = LUTvalue;
            }
        }

        for(int i = 0; i < pixels.length; i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int newRed = LUTr[red];
            int newGreen = LUTg[green];
            int newBlue = LUTb[blue];
            pixels[i] = Color.rgb(newRed,newGreen,newBlue);
        }

        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    void contrastDynamicExtensionRGBAverage(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        int min = 255;
        int max = 0;
        int[] LUT = new int[256];

        for(int i = 0; i < pixels.length; i++){
            int average = (Color.red(pixels[i])+Color.green(pixels[i])+Color.blue(pixels[i])) / 3;
            if(average < min){
                min = average;
            }
            if(average > max){
                max = average;
            }
        }

        for(int ng = 0; ng < 256; ng++){
            int LUTvalue = (255*(ng-min))/(max-min);
            if(LUTvalue < 0){
                LUT[ng] = 0;
            }else if(LUTvalue > 255){
                LUT[ng] = 255;
            }else{
                LUT[ng] = LUTvalue;
            }
        }

        for(int i=0;i<pixels.length;i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int newRed = LUT[red];
            int newGreen = LUT[green];
            int newBlue = LUT[blue];
            pixels[i] = Color.rgb(newRed,newGreen,newBlue);
        }

        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }


    void contrastHistogramEqualizationGrey(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        int[] H = new int[256];

        //calcul de l'histogramme
        for(int i = 0; i < pixels.length; i++){
            int grey = Color.red(pixels[i]);
            H[grey]++;
        }

        //calcul de l'histogramme cumulé
        for(int i = 1; i < 256; i++){
            H[i] = H[i] + H[i-1];
        }

        //Transformation de l'image
        for(int i = 0; i < pixels.length; i++){
            int grey = Color.red(pixels[i]);
            int newGrey = (255*H[grey]) / pixels.length;
            pixels[i] = Color.rgb(newGrey,newGrey,newGrey);
        }

        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    void contrastHistogramEqualizationRGBIndie(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        int[] Hr = new int[256];
        int[] Hg = new int[256];
        int[] Hb = new int[256];

        //calcul des histogrammes rouge, vert, bleu
        for(int i = 0; i < pixels.length; i++){
            int red = Color.red(pixels[i]);
            Hr[red]++;
            int green = Color.green(pixels[i]);
            Hg[green]++;
            int blue = Color.blue(pixels[i]);
            Hb[blue]++;
        }

        //calcul des histogrammes cumulés
        for(int i = 1; i < 256; i++){
            Hr[i] = Hr[i] + Hr[i-1];
            Hg[i] = Hg[i] + Hg[i-1];
            Hb[i] = Hb[i] + Hb[i-1];
        }

        //Transformation de l'image
        for(int i = 0; i < pixels.length; i++){
            int red = Color.red(pixels[i]);
            int newRed = (255*Hr[red])/pixels.length;
            int green = Color.green(pixels[i]);
            int newGreen = (255*Hg[green]) / pixels.length;
            int blue = Color.blue(pixels[i]);
            int newBlue = (255*Hb[blue]) / pixels.length;
            pixels[i] = Color.rgb(newRed,newGreen,newBlue);
        }

        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    void contrastHistogramEqualizationRGBAverage(Bitmap bmp){
        int[] pixels = new int[bmp.getHeight()*bmp.getWidth()];
        bmp.getPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
        int[] H = new int[256];

        //calcul de l'histogramme
        for(int i = 0; i < pixels.length; i++){
            int average = (Color.red(pixels[i])+Color.green(pixels[i])+Color.blue(pixels[i])) / 3;
            H[average]++;
        }

        //calcul de l'histogramme cumulé
        for(int i = 1; i < 256; i++){
            H[i] = H[i] + H[i-1];
        }

        //Transformation de l'image
        for(int i=0; i < pixels.length; i++){
            int red = Color.red(pixels[i]);
            int newRed = (255*H[red])/pixels.length;
            int green = Color.green(pixels[i]);
            int newGreen = (255*H[green])/pixels.length;
            int blue = Color.blue(pixels[i]);
            int newBlue = (255*H[blue])/pixels.length;
            pixels[i] = Color.rgb(newRed,newGreen,newBlue);
        }

        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    void contrastHistogramEqualizationYuvRS(Bitmap bmp,Context context){
        //Get image size
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        //Create new bitmap
        Bitmap res = bmp.copy(bmp.getConfig(), true);

        //Create renderscript
        RenderScript rs = RenderScript.create(context);

        //Create allocation from Bitmap
        Allocation allocationA = Allocation.createFromBitmap(rs, res);

        //Create allocation with same type
        Allocation allocationB = Allocation.createTyped(rs, allocationA.getType());

        //Create script from rs file.
        ScriptC_histEq histEqScript = new ScriptC_histEq(rs);

        //Set size in script
        histEqScript.set_size(width*height);

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationB);

        //Call the rs method to compute the remap array
        histEqScript.invoke_createRemapArray();

        //Call the second kernel
        histEqScript.forEach_remaptoRGB(allocationB, allocationA);

        //Copy script result into bitmap
        allocationA.copyTo(bmp);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();
        rs.destroy();
    }

}
