package com.example.jdavid004.projetandroids6;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by bdarmet on 01/02/19.
 */

public class Convolution {

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

