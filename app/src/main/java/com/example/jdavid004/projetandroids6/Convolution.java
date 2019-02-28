package com.example.jdavid004.projetandroids6;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Script;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;


/**
 * Created by bdarmet on 01/02/19.
 */

public class Convolution {

    private Picture picture;
    private int[][] matrix;
    private int m_width;
    private int m_height;
    private int factor;     //Somme des valeurs de la matrice
    private boolean secondApplyWithMatrixTranslation = false;   //Utiliser pour appliquer les 2 matrices h1 et h2 du cours avec une seule matrice
    private boolean normalize;

    Convolution(Picture picture, int[][] mat, int width, int height, boolean secondApplyWithMatrixTranslation, boolean normalize){
        this.picture = picture;
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

    void compute(){
        Bitmap bmp = picture.getBmp();
        int height = picture.getHeight();
        int width = picture.getWidth();
        int[] SrcPixels = new int[height*width];
        bmp.getPixels(SrcPixels,0,width,0,0,width,height);  //obtenir à partir de picture (comme plus haut)
        int[] ResPixels = SrcPixels.clone();

        //Ces 6 variables ne servent que dans le cas d'un contour
        int maxModuleGradiantRed = 0;
        int maxModuleGradiantGreen = 0;
        int maxModuleGradiantBlue = 0;

        int[] GradiantRed = new int[height*width];
        int[] GradiantGreen = new int[height*width];
        int[] GradiantBlue = new int[height*width];

        for(int y = 0; y < height-this.m_height+1; y++){                   //Parcours de l'image comme dans le cours
            for(int x = 0; x < width-this.m_width+1; x++){
                int index_center = ((x + (this.m_width/2)) + (y + (this.m_height/2))*width);   //Indice du pixel au centre de la matrice
                int sumRed = 0;
                int sumGreen = 0;
                int sumBlue = 0;

                int sumRed2 = 0;
                int sumGreen2 = 0;
                int sumBlue2 = 0;

                for(int my = 0; my < this.m_height; my++){                          //Parcours de la matrice
                    for(int mx = 0; mx < this.m_width; mx++){
                        int curPixel = SrcPixels[(x+mx)+(y+my)*bmp.getWidth()];     //Pixels couvèrent par la matrice
                        int mValue = matrix[mx][my];                                //Valeur de la matrice en (mx,my)
                        sumRed = sumRed + (Color.red(curPixel)*mValue);
                        sumGreen = sumGreen + (Color.green(curPixel)*mValue);
                        sumBlue = sumBlue + (Color.blue(curPixel)*mValue);

                        if(secondApplyWithMatrixTranslation){
                            mValue = matrix[my][mx];                                //Valeur de la matrice en (my,mx)
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
            for(int y = 0; y < height - this.m_height+1; y++) {                   //Parcours de l'image comme dans le cours
                for (int x = 0; x < width - this.m_width + 1; x++) {
                    int index_center = ((x + (this.m_width / 2)) + (y + (this.m_height / 2)) * width);   //Indice du pixel au centre de la matrice

                    float newRed = (GradiantRed[index_center] / (float)maxModuleGradiantRed) * 255;          //normalisation par le max
                    float newGreen = (GradiantGreen[index_center] / (float)maxModuleGradiantGreen) * 255;
                    float newBlue = (GradiantBlue[index_center] / (float)maxModuleGradiantBlue) * 255;
                    ResPixels[index_center] = Color.rgb((int)newRed, (int)newGreen, (int)newBlue);
                }
            }
        }

        if(this.m_width == this.m_height  && this.m_height == 3){                           //Gestion des bords pour filtre 3x3
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    if(y == 0 && x != 0 && x != width-1){                          //cas du bord haut sans le coin
                        ResPixels[x+y*bmp.getWidth()] = ResPixels[x+(y+1)*bmp.getWidth()];
                    }
                    if(y == height-1 && x != 0 && x != height-1){          //cas du bord bas sans le coin
                        ResPixels[x+y*width] = ResPixels[x+(y-1)*width];
                    }
                    if(x == 0){                                                             //cas du bord gauche
                        ResPixels[x+y*width] = ResPixels[(x+1)+y*width];
                    }
                    if(x == width-1){                                              //cas du bord droit
                        ResPixels[x+y*width] = ResPixels[(x-1)+y*width];
                    }

                }
            }
        }

        bmp.setPixels(ResPixels,0,width,0,0,width,height);   //Affectation des nouveaux pixels à l'image
    }


    void computeRS(Context context){
        Bitmap bmp = picture.getBmp();

        RenderScript rs = RenderScript.create(context);

        Allocation inAllocation = Allocation.createFromBitmap(rs, bmp);

        Allocation outAllocation = Allocation.createTyped(rs, inAllocation.getType());

        ScriptC_convolution convolutionScript = new ScriptC_convolution(rs);


        int matrix1d[] = new int[m_width*m_height];
        int k = 0;
        for(int i = 0; i < m_width; i++){
            for(int j = 0;  j < m_height; j++){
                matrix1d[k] = matrix[i][j];     //On aplatit la matrice
                k++;
            }
        }
        convolutionScript.set_ksize(m_width);
        Allocation mat = Allocation.createSized(rs, Element.I32(rs),matrix1d.length);
        mat.copyFrom(matrix1d);
        convolutionScript.bind_kmatrix(mat);
        convolutionScript.set_kdiv(factor);
        convolutionScript.set_normal(true);
        convolutionScript.set_gIn(inAllocation);

        convolutionScript.invoke_setup();
        convolutionScript.forEach_root(inAllocation,outAllocation);

        outAllocation.copyTo(bmp);

        inAllocation.destroy(); outAllocation.destroy(); mat.destroy();
        convolutionScript.destroy(); rs.destroy();
    }

    void computeIntrinsicGaussianBlur(Context context, float radius){
        Bitmap bmp = picture.getBmp();

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

    void computeIntrinsicConvolve(Context context){
        if(m_width != 3){
            if(m_width != 5){
                return;
            }
        }
        Bitmap bmp = picture.getBmp();

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

