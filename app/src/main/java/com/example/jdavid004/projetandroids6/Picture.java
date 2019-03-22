package com.example.jdavid004.projetandroids6;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;
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
    protected Bitmap bmp;
    /**
     * Horizontal dimension of a picture
     */
    protected int width;
    /**
     * Vertical dimension of a picture
     */
    protected int height;
    /**
     * Array containing all the pixels of the bitmap
     */
    protected int[] pixels;
    /**
     * Length of the array
     */
    protected int length;

    /**
     * Attributes used for convolution
     * matrix : 2 dimension table which is the core
     * m_width : core's width
     * m_height : core's height
     * factor : Sum of the core's values
     * secondApplyWithMatrixTranslation : Boolean to determine if we apply a matrix and its translation
     * normalize : Boolean to determine if we have to normalize
     */
    private int[][] matrix;
    private int m_width;
    private int m_height;
    private int factor;
    private boolean secondApplyWithMatrixTranslation = false;
    private boolean normalize;

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
        this.bmp = BitmapFactory.decodeResource(resources,R.drawable.contrast,options);
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
     * Set the bitmap with a sepia color
     */
    void sepia(){
        for(int i = 0; i < length; i++){
            int oldR = Color.red(pixels[i]);
            int oldG = Color.green(pixels[i]);
            int oldB = Color.blue(pixels[i]);
            int newR = (int) (oldR*0.393 + oldG*0.769 + oldB*0.189);
            if(newR>255)newR=255;
            int newG = (int) (oldR*0.349 + oldG*0.686 + oldB*0.168);
            if(newG>255)newG=255;
            int newB = (int) (oldR*0.272 + oldG*0.534 + oldB*0.131);
            if(newB>255)newB=255;
            pixels[i] = Color.rgb(newR,newG,newB);
        }
        this.bmp.setPixels(pixels,0,width,0,0,width,height);
    }

    void sepiaRS(Context context){
        RenderScript rs = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(rs,bmp);
        Allocation output = Allocation.createTyped(rs,input.getType());

        ScriptC_sepia sepiaScript = new ScriptC_sepia(rs);

        sepiaScript.forEach_sepia(input,output);

        output.copyTo(bmp);

        input.destroy(); output.destroy();
        sepiaScript.destroy(); rs.destroy();
    }

    void thresholding(){
        int seuil=125;
        int newR, newG, newB;
        for(int i = 0; i < length; i++){
            int R = Color.red(pixels[i]);
            if(R<seuil)
                newR=0;
            else
                newR=255;
            int G = Color.green(pixels[i]);
            if(G<seuil)
                newG=0;
            else
                newG=255;
            int B = Color.blue(pixels[i]);
            if(B<seuil)
                newB=0;
            else
                newB=255;
            pixels[i] = Color.rgb(newR,newG,newB);
        }
        this.bmp.setPixels(pixels,0,width,0,0,width,height);
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
   void contrastDynamicExtensionRGBAverage(int percentage, Picture copycurrentPictureUse){
        int min = 255;
        int max = 0;

        int min2 = 126 - percentage;
        int max2 =percentage + 126;
        int[] copyPixels = new int[height * width];
        Bitmap copyBitmap = copycurrentPictureUse.getBmp();
        copyBitmap.getPixels(copyPixels,0,width,0,0,width,height);
        int[] LUT = new int[256];


        for(int i = 0; i < length; i++){
            int average = (Color.red(copyPixels[i])+Color.green(copyPixels[i])+Color.blue(copyPixels[i])) / 3;
            if(average < min){
                min = average;
            }
            if(average > max){
                max = average;
            }
        }

        for(int ng = 0; ng < 256; ng++){
            int LUTvalue = ( (max2 - min2) * (ng - min))/(max-min) + min2;
            Log.i("lut", "Min2 = " + min2 + " | " + "Max2 = " + max2);
            Log.i("lut", "LUTValue = " + LUTvalue);
            if(LUTvalue < 0){
                LUT[ng] = 0;
            }else if(LUTvalue > 255){
                LUT[ng] = 255;
            }else{
                LUT[ng] = LUTvalue;
            }
        }

        for(int i=0;i<length;i++){
            int red = Color.red(copyPixels[i]);
            int green = Color.green(copyPixels[i]);
            int blue = Color.blue(copyPixels[i]);
            int newRed = LUT[red];
            int newGreen = LUT[green];
            int newBlue = LUT[blue];
            copyPixels[i] = Color.rgb(newRed,newGreen,newBlue);
        }

        bmp.setPixels(copyPixels,0,width,0,0,width,height);
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


    void pixelisation() {
        int pourcentage = 90;
        int lengthMatrice = width / pourcentage;

        // Je parcours mon image d'un bloc de 10x10
        for (int x = 0; x < width; x += lengthMatrice) {
            for (int y = 0; y < height  ; y += lengthMatrice) {

                int moyR = 0;
                int moyG = 0;
                int moyB = 0;
                // Cas des bords à droite
                if (x + lengthMatrice > width) {
                    int newX = width - x;
                    for (int i = lengthMatrice - newX; i < lengthMatrice; i++) { // On remplis en noir les case de la matrice qui ne sont pas dans l'image pour pour déterminer la valeur du pixel
                        for (int j = y; j < y + lengthMatrice; j++) {
                            moyR += Color.red(0);
                            moyG += Color.green(0);
                            moyB += Color.blue(0);
                        }
                    }

                    for (int i = x; i < width; i++) {
                        for (int j = y; j < y + lengthMatrice; j++) {
                            int pixelValue = pixels[i + j * width];
                            moyR += Color.red(pixelValue);
                            moyG += Color.green(pixelValue);
                            moyB += Color.blue(pixelValue);
                        }


                    }
                    Log.i("PIXELISATION", "On est dans la boucle du x");
                    //Cas des bords en bas de l'image
                }else if(y + lengthMatrice > height){
                    int newY = height - y;
                    for (int i = x; i < x + lengthMatrice; i++) { // On remplis en noir les case de la matrice qui ne sont pas dans l'image pour pour déterminer la valeur du pixel
                        for (int j = lengthMatrice - newY; j < lengthMatrice; j++) {
                            moyR += Color.red(0);
                            moyG += Color.green(0);
                            moyB += Color.blue(0);
                        }
                    }

                    for (int i = x; i < x + lengthMatrice; i++) {
                        for (int j = y; j < height ; j++) {
                            int pixelValue = pixels[i + j * width];
                            moyR += Color.red(pixelValue);
                            moyG += Color.green(pixelValue);
                            moyB += Color.blue(pixelValue);
                        }


                    }
                    Log.i("PIXELISATION", "On est dans la boucle du y");
                }else{
                    // Je parcours ensuite ma matrice de  taille length * length
                    for (int i = x; i < x + lengthMatrice; i++) {
                        for (int j = y; j < y + lengthMatrice; j++) {
                            Log.i("PIXELISATION", "On est dans la boucle normal");
                            int pixelValue = pixels[i + j * width];
                            moyR += Color.red(pixelValue);
                            moyG += Color.green(pixelValue);
                            moyB += Color.blue(pixelValue);
                        }
                    }
                }

                moyR = moyR / (lengthMatrice * lengthMatrice);
                moyG = moyG / (lengthMatrice * lengthMatrice);
                moyB = moyB / (lengthMatrice * lengthMatrice);

                for (int i = x; i < x + lengthMatrice; i++) {
                    for (int j = y; j < y + lengthMatrice; j++) {
                        // J'attribue la nouvelle valeur du pixel
                        pixels[i + j * width] = Color.rgb(moyR, moyG, moyB);
                    }
                }
            }
        }

        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
    }

    /**
     * Change the attributes used for a convolution
     * @param mat Core's values
     * @param width Core's width
     * @param height Core's height
     * @param secondApplyWithMatrixTranslation Boolean to determine if we apply a matrix and its translation
     * @param normalize Boolean to determine if we have to normalize
     */
    void ModifyConvolutionAttributes(int[][] mat, int width, int height, boolean secondApplyWithMatrixTranslation, boolean normalize){
        this.m_width = width;
        this.m_height = height;
        this.matrix = new int[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0;  j < height; j++){
                this.matrix[i][j] = mat[i][j];
                this.factor += mat[i][j];
            }
        }
        this.secondApplyWithMatrixTranslation = secondApplyWithMatrixTranslation;
        this.normalize = normalize;
        if(!normalize) factor = 1;
    }

    /**
     * Apply the convolution corresponding to the core set in attribute
     */
    void compute(){
        int[] SrcPixels = new int[height*width];
        bmp.getPixels(SrcPixels,0,width,0,0,width,height);
        int[] ResPixels = SrcPixels.clone();

        //These 6 variables are only used when a contour is applied
        int maxModuleGradiantRed = 0;
        int maxModuleGradiantGreen = 0;
        int maxModuleGradiantBlue = 0;

        int[] GradiantRed = new int[height*width];
        int[] GradiantGreen = new int[height*width];
        int[] GradiantBlue = new int[height*width];

        for(int y = 0; y < height-this.m_height+1; y++){                   //Run through the image from left to right, downhill
            for(int x = 0; x < width-this.m_width+1; x++){
                int index_center = ((x + (this.m_width/2)) + (y + (this.m_height/2))*width);   //pixel's index in the center of the matrix
                int sumRed = 0;
                int sumGreen = 0;
                int sumBlue = 0;

                int sumRed2 = 0;
                int sumGreen2 = 0;
                int sumBlue2 = 0;

                for(int my = 0; my < this.m_height; my++){                          //Run through the matrix
                    for(int mx = 0; mx < this.m_width; mx++){
                        int curPixel = SrcPixels[(x+mx)+(y+my)*bmp.getWidth()];     //Pixels covered by the matrix
                        int mValue = matrix[mx][my];                                //Value of the matrix in (mx, my)
                        sumRed = sumRed + (Color.red(curPixel)*mValue);
                        sumGreen = sumGreen + (Color.green(curPixel)*mValue);
                        sumBlue = sumBlue + (Color.blue(curPixel)*mValue);

                        if(secondApplyWithMatrixTranslation){
                            mValue = matrix[my][mx];                                //Value of the matrix in (my, mx)
                            sumRed2 = sumRed2 + (Color.red(curPixel)*mValue);
                            sumGreen2 = sumGreen2 + (Color.green(curPixel)*mValue);
                            sumBlue2 = sumBlue2 + (Color.blue(curPixel)*mValue);
                        }
                    }
                }

                if(secondApplyWithMatrixTranslation){

                    int moduleGradiantRed = (int)Math.sqrt(Math.pow(sumRed,2) + Math.pow(sumRed2,2));
                    int moduleGradiantGreen = (int)Math.sqrt(Math.pow(sumGreen,2) + Math.pow(sumGreen2,2));
                    int moduleGradiantBlue = (int)Math.sqrt(Math.pow(sumBlue,2) + Math.pow(sumBlue2,2));

                    maxModuleGradiantRed = Math.max(maxModuleGradiantRed,moduleGradiantRed);
                    maxModuleGradiantGreen = Math.max(maxModuleGradiantGreen,moduleGradiantGreen);
                    maxModuleGradiantBlue = Math.max(maxModuleGradiantBlue,moduleGradiantBlue);

                    GradiantRed[index_center] = moduleGradiantRed;
                    GradiantGreen[index_center] = moduleGradiantGreen;
                    GradiantBlue[index_center] = moduleGradiantBlue;

                } else {
                    int newRed = 0;
                    try {
                        newRed = (sumRed / this.factor);
                    }catch(Exception e ){
                        System.out.print(e.getMessage());
                    }
                    if(newRed < 0){
                        newRed = 0;
                    }else if(newRed > 255){
                        newRed = 255;
                    }

                    int newGreen = 0;
                    try {
                        newGreen = (sumGreen / this.factor);
                    }catch(Exception e ){
                        System.out.print(e.getMessage());
                    }
                    if(newGreen < 0){
                        newGreen = 0;
                    }else if(newGreen > 255){
                        newGreen = 255;
                    }

                    int newBlue = 0;
                    try {
                        newBlue = (sumBlue / this.factor);
                    }catch(Exception e ){
                        System.out.print(e.getMessage());
                    }
                    if(newBlue < 0){
                        newBlue = 0;
                    }else if(newBlue > 255){
                        newBlue = 255;
                    }

                    ResPixels[index_center] = Color.rgb(newRed,newGreen,newBlue);
                }
            }
        }

        if(secondApplyWithMatrixTranslation){
            for(int y = 0; y < height - this.m_height+1; y++) {                   //Run through the image from left to right, downhill
                for (int x = 0; x < width - this.m_width + 1; x++) {
                    int index_center = ((x + (this.m_width / 2)) + (y + (this.m_height / 2)) * width);   //pixel's index in the center of the matrix

                    float newRed = (GradiantRed[index_center] / (float)maxModuleGradiantRed) * 255;          //notrmalization by the max
                    float newGreen = (GradiantGreen[index_center] / (float)maxModuleGradiantGreen) * 255;
                    float newBlue = (GradiantBlue[index_center] / (float)maxModuleGradiantBlue) * 255;
                    ResPixels[index_center] = Color.rgb((int)newRed, (int)newGreen, (int)newBlue);
                }
            }
        }

        if(this.m_width == this.m_height  && this.m_height == 3){                           //Edge management for 3x3 filter
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    if(y == 0 && x != 0 && x != width-1){                          //case of the upper edge without corner
                        ResPixels[x+y*bmp.getWidth()] = ResPixels[x+(y+1)*bmp.getWidth()];
                    }
                    if(y == height-1 && x != 0 && x != height-1){          //case of the lower edge without corner
                        ResPixels[x+y*width] = ResPixels[x+(y-1)*width];
                    }
                    if(x == 0){                                                             //case of the left edge
                        ResPixels[x+y*width] = ResPixels[(x+1)+y*width];
                    }
                    if(x == width-1){                                              //case of the right edge
                        ResPixels[x+y*width] = ResPixels[(x-1)+y*width];
                    }

                }
            }
        }

        bmp.setPixels(ResPixels,0,width,0,0,width,height);   //Assigning new pixels to the image
    }

    /**
     * Apply the convolution corresponding to the core set in attribute in Renderscript
     * @param context Context of the application
     */
    void computeRS(Context context){
        RenderScript rs = RenderScript.create(context);

        Allocation inAllocation = Allocation.createFromBitmap(rs, bmp);

        Allocation outAllocation = Allocation.createTyped(rs, inAllocation.getType());

        ScriptC_convolution convolutionScript = new ScriptC_convolution(rs);


        int matrix1d[] = new int[m_width*m_height];
        int k = 0;
        for(int i = 0; i < m_width; i++){
            for(int j = 0;  j < m_height; j++){
                matrix1d[k] = matrix[i][j];     //Flatten the matrix
                k++;
            }
        }
        convolutionScript.set_ksize(m_width);
        Allocation mat = Allocation.createSized(rs, Element.I32(rs), matrix1d.length);
        mat.copyFrom(matrix1d);
        convolutionScript.bind_kmatrix(mat);
        convolutionScript.set_kdiv(factor);
        convolutionScript.set_normal(normalize);
        convolutionScript.set_gIn(inAllocation);

        convolutionScript.invoke_setup();
        convolutionScript.forEach_root(inAllocation,outAllocation);
        if(secondApplyWithMatrixTranslation){
            k = 0;
            for(int i = 0; i < m_width; i++){
                for(int j = 0;  j < m_height; j++){
                    matrix1d[k] = matrix[j][i];     //Flatten the matrix translated
                    k++;
                }
            }
            mat.copyFrom(matrix1d);
            convolutionScript.bind_kmatrix(mat);
            convolutionScript.forEach_root(inAllocation,outAllocation);
        }

        outAllocation.copyTo(bmp);

        inAllocation.destroy(); outAllocation.destroy(); mat.destroy();
        convolutionScript.destroy(); rs.destroy();
    }

    /**
     * Apply a gaussian blur (intrinsic implementation in Renderscript)
     * @param context Context of the application
     * @param radius Radius of the gaussian blur
     */
    void computeIntrinsicGaussianBlur(Context context, float radius){
        RenderScript rs = RenderScript.create(context);

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation inAllocation = Allocation.createFromBitmap(rs, bmp);
        Allocation outAllocation = Allocation.createTyped(rs, inAllocation.getType());

        blurScript.setRadius(radius);

        blurScript.setInput(inAllocation);
        blurScript.forEach(outAllocation);

        outAllocation.copyTo(bmp);

        blurScript.destroy();
        inAllocation.destroy(); outAllocation.destroy(); rs.destroy();
    }

    /**
     * Apply the convolution corresponding to the core set in attribute in Renderscript by using intrinsic implementations
     * @param context Context of the application
     */
    void computeIntrinsicConvolve(Context context){
        if(m_width != 3){
            if(m_width != 5){
                return;
            }
        }

        RenderScript rs = RenderScript.create(context);

        Allocation inAllocation = Allocation.createFromBitmap(rs, bmp);
        Allocation outAllocation = Allocation.createTyped(rs, inAllocation.getType());

        if(m_width == 3){
            final ScriptIntrinsicConvolve3x3 convolution3x3Script = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));

            convolution3x3Script.setInput(inAllocation);
            convolution3x3Script.setCoefficients(new float[]
                    {
                            (float)matrix[0][0]/factor, (float)matrix[0][1]/factor, (float)matrix[0][2]/factor,
                            (float)matrix[1][0]/factor, (float)matrix[1][1]/factor, (float)matrix[1][2]/factor,
                            (float)matrix[2][0]/factor, (float)matrix[2][1]/factor, (float)matrix[2][2]/factor
                    });

            convolution3x3Script.forEach(outAllocation);

            if(secondApplyWithMatrixTranslation){
                convolution3x3Script.setCoefficients(new float[]
                        {
                                (float)matrix[0][0]/factor, (float)matrix[1][0]/factor, (float)matrix[2][0]/factor,
                                (float)matrix[0][1]/factor, (float)matrix[1][1]/factor, (float)matrix[2][1]/factor,
                                (float)matrix[0][2]/factor, (float)matrix[1][2]/factor, (float)matrix[2][2]/factor
                        });

                convolution3x3Script.forEach(outAllocation);
            }

            convolution3x3Script.destroy();
        }else{
            final ScriptIntrinsicConvolve5x5 convolution5x5Script = ScriptIntrinsicConvolve5x5.create(rs, Element.U8_4(rs));

            convolution5x5Script.setInput(inAllocation);
            convolution5x5Script.setCoefficients(new float[]
                    {
                            (float)matrix[0][0]/factor,(float)matrix[0][1]/factor,(float)matrix[0][2]/factor,(float)matrix[0][3]/factor,(float)matrix[0][4]/factor,
                            (float)matrix[1][0]/factor,(float)matrix[1][1]/factor,(float)matrix[1][2]/factor,(float)matrix[1][3]/factor,(float)matrix[1][4]/factor,
                            (float)matrix[2][0]/factor,(float)matrix[2][1]/factor,(float)matrix[2][2]/factor,(float)matrix[2][3]/factor,(float)matrix[2][4]/factor,
                            (float)matrix[3][0]/factor,(float)matrix[3][1]/factor,(float)matrix[3][2]/factor,(float)matrix[3][3]/factor,(float)matrix[3][4]/factor,
                            (float)matrix[4][0]/factor,(float)matrix[4][1]/factor,(float)matrix[4][2]/factor,(float)matrix[4][3]/factor,(float)matrix[4][4]/factor
                    });

            convolution5x5Script.forEach(outAllocation);

            convolution5x5Script.destroy();
        }

        outAllocation.copyTo(bmp);

        inAllocation.destroy(); outAllocation.destroy(); rs.destroy();
    }
}