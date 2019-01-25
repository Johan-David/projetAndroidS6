package com.example.jdavid004.projetandroids6;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Allocation;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    void toGreyRS(Bitmap bmp){
        RenderScript rs = RenderScript.create(this);

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

    void colorizeRS(Bitmap bmp){
        RenderScript  rs = RenderScript.create(this);

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

    void redOnlyHsvRS(Bitmap bmp){
        RenderScript rs = RenderScript.create(this);

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

    void contrastHistogramEqualizationYuvRS(Bitmap bmp){
        //Get image size
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        //Create new bitmap
        Bitmap res = bmp.copy(bmp.getConfig(), true);

        //Create renderscript
        RenderScript rs = RenderScript.create(this);

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
    } //Trouver sur internet

    class Convolution{

        int[][] matrix;
        int m_width;
        int m_height;
        int factor;     //Somme des valeurs de la matrice
        boolean secondApplyWithMatrixTranslation = false;   //Utiliser pour appliquer les 2 matrices h1 et h2 du cours avec une seule matrice

        Convolution(int[][] mat, int width, int height, boolean secondApplyWithMatrixTranslation){
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
        }

        void compute(Bitmap bmp){
            int[] SrcPixels = new int[bmp.getHeight()*bmp.getWidth()];
            bmp.getPixels(SrcPixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());
            int[] ResPixels = SrcPixels.clone();

            //Ces 6 variables ne servent que dans le cas d'un contour
            int maxModuleGradiantRed = 0;
            int maxModuleGradiantGreen = 0;
            int maxModuleGradiantBlue = 0;

            int[] GradiantRed = new int[bmp.getHeight()*bmp.getWidth()];
            int[] GradiantGreen = new int[bmp.getHeight()*bmp.getWidth()];
            int[] GradiantBlue = new int[bmp.getHeight()*bmp.getWidth()];

            for(int y = 0; y < bmp.getHeight()-this.m_height+1; y++){                   //Parcours de l'image comme dans le cours
                for(int x = 0; x < bmp.getWidth()-this.m_width+1; x++){
                    int index_center = ((x + (this.m_width/2)) + (y + (this.m_height/2))*bmp.getWidth());   //Indice du pixel au centre de la matrice
                    int sumRed = 0;
                    int sumGreen = 0;
                    int sumBlue = 0;

                    for(int my = 0; my < this.m_height; my++){                           //Parcours de la matrice
                        for(int mx = 0; mx < this.m_width; mx++){
                            int curPixel = SrcPixels[(x+mx)+(y+my)*bmp.getWidth()];     //Pixels couvèrent par la matrice
                            int mValue = matrix[my][mx];                                //Valeur de la matrice en (mx,my)

                            sumRed = sumRed + (Color.red(curPixel)*mValue);
                            sumGreen = sumGreen + (Color.green(curPixel)*mValue);
                            sumBlue = sumBlue + (Color.blue(curPixel)*mValue);
                        }
                    }

                    int sumRed2 = 0;
                    int sumGreen2 = 0;
                    int sumBlue2 = 0;

                    if(secondApplyWithMatrixTranslation){
                        for(int my = 0; my < this.m_height; my++){                           //Parcours de la matrice
                            for(int mx = 0; mx < this.m_width; mx++){
                                int curPixel = SrcPixels[(x+mx)+(y+my)*bmp.getWidth()];     //Pixels couvèrent par la matrice
                                int mValue = matrix[mx][my];                                //Valeur de la matrice en (my,mx)

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
                        int newRed = (sumRed / this.factor);
                        if(newRed < 0){
                            newRed = 0;
                        }else if(newRed > 255){
                            newRed = 255;
                        }

                        int newGreen = (sumGreen / this.factor);
                        if(newGreen < 0){
                            newGreen = 0;
                        }else if(newGreen > 255){
                            newGreen = 255;
                        }

                        int newBlue = (sumBlue / this.factor);
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
                for(int y = 0; y < bmp.getHeight()-this.m_height+1; y++) {                   //Parcours de l'image comme dans le cours
                    for (int x = 0; x < bmp.getWidth() - this.m_width + 1; x++) {
                        int index_center = ((x + (this.m_width / 2)) + (y + (this.m_height / 2)) * bmp.getWidth());   //Indice du pixel au centre de la matrice

                        float newRed = (GradiantRed[index_center] / (float)maxModuleGradiantRed) * 255;          //normalisation par le max
                        float newGreen = (GradiantGreen[index_center] / (float)maxModuleGradiantGreen) * 255;
                        float newBlue = (GradiantBlue[index_center] / (float)maxModuleGradiantBlue) * 255;
                        ResPixels[index_center] = Color.rgb((int)newRed, (int)newGreen, (int)newBlue);
                    }
                }
            }

            if(this.m_width == this.m_height  && this.m_height == 3){                           //Gestion des bords pour filtre 3x3
                for(int y = 0; y < bmp.getHeight(); y++){
                    for(int x = 0; x < bmp.getWidth(); x++){
                        if(y == 0 && x != 0 && x != bmp.getWidth()-1){                          //cas du bord haut sans le coin
                            ResPixels[x+y*bmp.getWidth()] = ResPixels[x+(y+1)*bmp.getWidth()];
                        }
                        if(y == bmp.getHeight()-1 && x != 0 && x != bmp.getWidth()-1){          //cas du bord bas sans le coin
                            ResPixels[x+y*bmp.getWidth()] = ResPixels[x+(y-1)*bmp.getWidth()];
                        }
                        if(x == 0){                                                             //cas du bord gauche
                            ResPixels[x+y*bmp.getWidth()] = ResPixels[(x+1)+y*bmp.getWidth()];
                        }
                        if(x == bmp.getWidth()-1){                                              //cas du bord droit
                            ResPixels[x+y*bmp.getWidth()] = ResPixels[(x-1)+y*bmp.getWidth()];
                        }

                    }
                }
            }

            bmp.setPixels(ResPixels,0,bmp.getWidth(),0,0,bmp.getWidth(),bmp.getHeight());   //Affectation des nouveaux pixels à l'image
        }

    }
}
