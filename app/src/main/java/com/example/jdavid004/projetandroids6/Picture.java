package com.example.jdavid004.projetandroids6;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;

import com.android.rssample.ScriptC_dynExtension;


/**
 * It represents all the different treatments that an image can receive. A picture translate into a Bitmap and it dimensions.
 * @author Benjamin Darmet, Amandine Chauveau, Remi Barbosa, Johan David
 */
public class Picture  {

    /**
     * A Bitmap which represent a picture.
     */
    private Bitmap bmp;
    /**
     * Horizontal dimension of a picture
     */
    private int width;
    /**
     * Vertical dimension of a picture
     */
    private int height;
    /**
     * Array containing all the pixels of the bitmap
     */
    private int[] pixels;
    /**
     * Length of the array
     */
    private int length;

    /**
     * Create a picture from a Bitmap
     * @param bmp Bitmap representing the image to be stored
     */
    Picture(Bitmap bmp){
        this.bmp = bmp.copy(bmp.getConfig(),true);
        setDimensions();
        setPixels();
    }


    /**
     * Create a picture from a basical photo stock in the application. it's the first image display on the app.
     * @param resources
     */
    Picture(Resources resources) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inScaled = false;
        this.bmp = BitmapFactory.decodeResource(resources,R.drawable.vegetables,options);
        setDimensions();
        setPixels();
    }

    /* Getter & Setter */
    public Bitmap getBmp() {
        return bmp;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    void setPixels(){
        this.pixels = new int[width*height];
        this.bmp.getPixels(pixels,0,width,0,0,width,height);
        this.length = pixels.length;
    }

    void setDimensions(){
        this.width = bmp.getWidth();
        this.height = bmp.getHeight();
    }



    /**
     * Put through a treatment to the bmp to change the color in grey
     */
    void toGrey(){
        for(int i = 0; i < length; i++){
            int R = Color.red(pixels[i]);
            int G = Color.green(pixels[i]);
            int B = Color.blue(pixels[i]);
            int Grey = (int)(0.3*R+0.59*G+0.11*B);
            pixels[i] = Color.rgb(Grey,Grey,Grey);
        }
        this.bmp.setPixels(pixels,0,width,0,0,width,height);
    }

    /**
     * Put through a treatment to the image to change the color in grey but in Renderscript.
     * @param context Context of  the application
     */
    void toGreyRS(Context context){
        RenderScript rs = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(rs,bmp);
        Allocation output = Allocation.createTyped(rs,input.getType());

        ScriptC_toGrey greyScript = new ScriptC_toGrey(rs);

        greyScript.forEach_toGrey(input,output);

        output.copyTo(bmp);

        input.destroy(); output.destroy();
        greyScript.destroy(); rs.destroy();
    }


    /**
     * Change the color of the image with the color selected
     * @param color Color applicated on the image
     */

    void colorize(int color){
        float[] hsv = new float[3];
        float[] hsvColor = new float[3];
        Color.RGBToHSV(Color.red(color),Color.green(color),Color.blue(color),hsvColor);
        for(int i = 0; i < length; i++){
            Color.RGBToHSV(Color.red(pixels[i]),Color.green(pixels[i]),Color.blue(pixels[i]),hsv);
            hsv[0] = hsvColor[0];
            pixels[i] = Color.HSVToColor(hsv);
        }
        bmp.setPixels(pixels,0,width,0,0,width,height);
    }

    /**
     * Change the color of the image with the color selected in RenderScript
     * @param context Context of the application
     * @param color Color applicated on the image
     */
    void colorizeRS( Context context, int color){
        RenderScript  rs = RenderScript.create(context);

        Allocation  input = Allocation.createFromBitmap(rs,bmp);
        Allocation output = Allocation.createTyped(rs,input.getType());

        ScriptC_colorize colorizeScript = new ScriptC_colorize(rs);

        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);

        colorizeScript.set_color(hsv[0]);

        colorizeScript.forEach_colorize(input,output);

        output.copyTo(bmp);

        input.destroy(); output.destroy();
        colorizeScript.destroy(); rs.destroy();
    }

    /**
     * Keeps only the red color on the image. All the others are in grey.
     */
    void redOnlyHsv(){
        float[] hsv = new float[3];

        for(int i = 0; i < length; i++) {
            Color.RGBToHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsv);
            if (hsv[0] >= 15 && hsv[0] <= 345) {
                int R = Color.red(pixels[i]);
                int G = Color.green(pixels[i]);
                int B = Color.blue(pixels[i]);
                int Grey = (int) (0.3 * R + 0.59 * G + 0.11 * B);
                pixels[i] = Color.rgb(Grey, Grey, Grey);
            }
        }
        bmp.setPixels(pixels,0,width,0,0,width,height);
    }

    /**
     * Keeps only the selected color on the image. All the others are in grey.
     * @param color Color that wants to be kept
     */
    void colorOnlyHsv( int color){
        float[] hsv = new float[3];
        float[] hsvColor = new float[3];
        Color.RGBToHSV(Color.red(color),Color.green(color),Color.blue(color),hsvColor);

        float minHSV = hsvColor[0] - 20;
        float maxHSV =  hsvColor[0] + 20;
        //case red
        if(minHSV < 0){
            minHSV += 360;
        }

        if(maxHSV >= 360){
            maxHSV -= 360;
        }

        for(int i = 0; i < length; i++) {
            Color.RGBToHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsv);
            if(minHSV > maxHSV){
                //color other than red
                if(hsv[0] > maxHSV && hsv[0] < minHSV ){
                    int R = Color.red(pixels[i]);
                    int G = Color.green(pixels[i]);
                    int B = Color.blue(pixels[i]);
                    int Grey = (int) (0.3 * R + 0.59 * G + 0.11 * B);
                    pixels[i] = Color.rgb(Grey, Grey, Grey);
                }
            }else{
                if(!(hsv[0]  >= minHSV && hsv[0] <= maxHSV) ){
                    int R = Color.red(pixels[i]);
                    int G = Color.green(pixels[i]);
                    int B = Color.blue(pixels[i]);
                    int Grey = (int) (0.3 * R + 0.59 * G + 0.11 * B);
                    pixels[i] = Color.rgb(Grey, Grey, Grey);
                }
            }

        }
        bmp.setPixels(pixels,0,width,0,0,width,height);
    }

    /**
     * Keeps only the selected color on the image. All the others are in grey.
     * @param context Context of the application
     * @param color Keeps only the selected color on the image. All the others are in grey.
     */
    void colorOnlyHsvRS(Context context, int color){
        float[] hsvTab = new float[3];
        Color.RGBToHSV(Color.red(color),Color.green(color),Color.blue(color), hsvTab);
        RenderScript rs = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(rs,bmp);
        Allocation output = Allocation.createTyped(rs,input.getType());

        ScriptC_color_only_HSV color_only_HSVScript = new ScriptC_color_only_HSV(rs);
        color_only_HSVScript.set_hue(hsvTab[0]);
        color_only_HSVScript.forEach_color_only_hsv(input,output);

        output.copyTo(bmp);

        input.destroy(); output.destroy();
        color_only_HSVScript.destroy(); rs.destroy();
    }


    /**
     * Increase the contrast of the image in grey using the dynamic extension method
     */
    void contrastDynamicExtensionGrey(){
        int min = 255;
        int max = 0;
        int[] LUT = new int[256];

        for(int i = 0; i < length; i++){
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

        for(int i = 0; i < length; i++){
            int grey = Color.red(pixels[i]);
            pixels[i] = Color.rgb(LUT[grey],LUT[grey],LUT[grey]);
        }

        bmp.setPixels(pixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
    }

    /**
     * Increases the image contrast by calculating the color value independently of the 3 RGB ranges through the dynamic extension method.
     */
    void contrastDynamicExtensionRGBIndie(){
        int minRed = 255;
        int maxRed = 0;
        int minGreen = 255;
        int maxGreen = 0;
        int minBlue = 255;
        int maxBlue = 0;

        int[] LUTr = new int[256];
        int[] LUTg = new int[256];
        int[] LUTb = new int[256];

        for(int i = 0; i < length; i++){
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

        for(int i = 0; i < length; i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int newRed = LUTr[red];
            int newGreen = LUTg[green];
            int newBlue = LUTb[blue];
            pixels[i] = Color.rgb(newRed,newGreen,newBlue);
        }

        bmp.setPixels(pixels,0,width,0,0,width,height);
    }

    /**
     * Increases the image contrast by calculating the color value of a pixel by averaging the 3 RGB ranges accumulated through the dynamic extension method
     */
    void contrastDynamicExtensionRGBAverage(){
        int min = 255;
        int max = 0;
        int[] LUT = new int[256];

        for(int i = 0; i < length; i++){
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

        for(int i=0;i<length;i++){
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);
            int newRed = LUT[red];
            int newGreen = LUT[green];
            int newBlue = LUT[blue];
            pixels[i] = Color.rgb(newRed,newGreen,newBlue);
        }

        bmp.setPixels(pixels,0,width,0,0,width,height);
    }


    /**
     * Increases the contrast of an image using the dynamic extension method in RenderScript
     * @param context Context of the application
     */
    void contrastDynamicExtensionRS(Context context){

        //Initialisation of the composant to compute the min and max
        int min = 255;
        int max = 0;

        //Create new bitmap
        Bitmap res = bmp.copy(bmp.getConfig(), true);

        //Create renderscript
        RenderScript rs = RenderScript.create(context);

        //Create allocation from Bitmap
        Allocation allocationA = Allocation.createFromBitmap(rs, res);

        //Create allocation with same type
        Allocation allocationB = Allocation.createTyped(rs, allocationA.getType());


        //Calcul of min and max not in Renderscript.
        for(int i = 0; i < length; i++){
            int average = (Color.red(pixels[i])+Color.green(pixels[i])+Color.blue(pixels[i])) / 3;
            if(average < min){
                min = average;
            }
            if(average > max){
                max = average;
            }
        }

        //Create script from rs file.
        ScriptC_dynExtension test= new ScriptC_dynExtension(rs);

        //Call the rs method to compute the remap array. It creates the LUT
        test.set_minValue(min);
        test.set_maxValue(max);
        test.invoke_changeLUT();

        test.forEach_transformation(allocationB, allocationA);
        //Copy script result into bitmap
        allocationA.copyTo(bmp);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        test.destroy();
        rs.destroy();

    }

    /**
     * Increases the contrast of the image in grey by equalizing its histogram.
     */
    void contrastHistogramEqualizationGrey(){
        int[] H = new int[256];

        //Calculation of the histogram
        for(int i = 0; i < length; i++){
            int grey = Color.red(pixels[i]);
            H[grey]++;
        }

        //Calculation of the cumulative histogram
        for(int i = 1; i < 256; i++){
            H[i] = H[i] + H[i-1];
        }

        //Applying the new values to pixels
        for(int i = 0; i < length; i++){
            int grey = Color.red(pixels[i]);
            int newGrey = (255*H[grey]) / pixels.length;
            pixels[i] = Color.rgb(newGrey,newGrey,newGrey);
        }

        bmp.setPixels(pixels,0,width,0,0,width,height);
    }

    /**
     * Increases the contrast of the image by equalizing its histogram with the independent RGB ranges
     */
    void contrastHistogramEqualizationRGBIndie(){
        int[] Hr = new int[256];
        int[] Hg = new int[256];
        int[] Hb = new int[256];

        //Calculation of the  différent histograms : Red histogram, Green histogram and Blue histogram
        for(int i = 0; i < length; i++){
            int red = Color.red(pixels[i]);
            Hr[red]++;
            int green = Color.green(pixels[i]);
            Hg[green]++;
            int blue = Color.blue(pixels[i]);
            Hb[blue]++;
        }

        //Calculation of the cumulative histogram
        for(int i = 1; i < 256; i++){
            Hr[i] = Hr[i] + Hr[i-1];
            Hg[i] = Hg[i] + Hg[i-1];
            Hb[i] = Hb[i] + Hb[i-1];
        }

        //Applying the new values to pixels
        for(int i = 0; i < length; i++){
            int red = Color.red(pixels[i]);
            int newRed = (255*Hr[red])/length;
            int green = Color.green(pixels[i]);
            int newGreen = (255*Hg[green]) / length;
            int blue = Color.blue(pixels[i]);
            int newBlue = (255*Hb[blue]) / length;
            pixels[i] = Color.rgb(newRed,newGreen,newBlue);
        }

        bmp.setPixels(pixels,0,width,0,0,width,height);
    }

    /**
     * Increases the contrast of the image by equalizing its histogram by averaging the values of the RGB ranges of each pixel
     */
    void contrastHistogramEqualizationRGBAverage(){
        int[] H = new int[256];

        //Calculation of the histogram
        for(int i = 0; i < length; i++){
            int average = (Color.red(pixels[i])+Color.green(pixels[i])+Color.blue(pixels[i])) / 3;
            H[average]++;
        }

        //Calculation of the cumulative histogram
        for(int i = 1; i < 256; i++){
            H[i] = H[i] + H[i-1];
        }

        //Applying the new values to pixels
        for(int i=0; i < length; i++){
            int red = Color.red(pixels[i]);
            int newRed = (255*H[red])/length;
            int green = Color.green(pixels[i]);
            int newGreen = (255*H[green])/length;
            int blue = Color.blue(pixels[i]);
            int newBlue = (255*H[blue])/length;
            pixels[i] = Color.rgb(newRed,newGreen,newBlue);
        }

        bmp.setPixels(pixels,0,width,0,0,width,height);
    }

    /**
     * Equalizes the histogram of the image to increase its contrast using RenderScript
     * @param context Context of the application
     * @author Quentin mineni
     */
    void contrastHistogramEqualizationYuvRS(Context context){

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

    /**
     * Increases or decreases the brightness of the image depending on the selected increase or decrease in brightness
     * @param pourcent Percentage of selected brightness
     * @param picture Picture use to copy the value of a pixel of a bitmap where the brightness has not changed. In other words, it is a copy of the processed image.
     */
    void AdjustLuminosity(int pourcent, Picture picture){
        if(pourcent==100){
            return;
        }

        float[] hsv = new float[3];
        float luminosity = pourcent/100.f;

        for(int i = 0; i < length; i++) {
            Color.RGBToHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsv);
            float newValue = hsv[2]*luminosity;
            if(newValue > 1){
                hsv[2] = 1;
            }else{
                hsv[2] = newValue;
            }
            pixels[i] = Color.HSVToColor(hsv);
        }
        bmp.setPixels(pixels,0,width,0,0,width,height);
    }

    /**
     *  Increases or decreases the brightness of the image depending on the selected increase or decrease in brightness in RenderScript
     * @param context Context of the application
     * @param pourcent Percentage of selected brightness
     * @param picture Picture use to copy the value of a pixel of a bitmap where the brightness has not changed. In other words, it is a copy of the processed image
     */
    void AdjustLuminosityRS(Context context, int pourcent, Picture picture){
        if(pourcent != 100){
            RenderScript rs = RenderScript.create(context);

            Allocation input = Allocation.createFromBitmap(rs,picture.getBmp());
            Allocation output = Allocation.createTyped(rs,input.getType());

            ScriptC_AdjustLuminosityHSV AdjustLuminosityHSVScript = new ScriptC_AdjustLuminosityHSV(rs);

            AdjustLuminosityHSVScript.set_luminosity(pourcent/100.f);

            AdjustLuminosityHSVScript.forEach_AdjustLuminosityHSV(input,output);

            output.copyTo(bmp);

            input.destroy(); output.destroy();
            AdjustLuminosityHSVScript.destroy(); rs.destroy();
        }
    }



}