#pragma  version (1)
#pragma  rs  java_package_name(com.example.jdavid004.projetandroids6)
#pragma rs_fp_relaxed

float hue = 0;

uchar4  RS_KERNEL  color_only_hsv(uchar4  in){
    float minHSV = hue - 20;
    float maxHSV = hue + 20;
    if(minHSV < 0){
        minHSV += 360;
    }
    if(maxHSV >= 360){
        maxHSV -= 360;
    }
    float pixelh;
    const  float4  pixelf = rsUnpackColor8888(in);

    float minRGB=min(pixelf.r,min(pixelf.g,pixelf.b));
    float maxRGB=max(pixelf.r,max(pixelf.g,pixelf.b));
    float deltaRGB=maxRGB-minRGB;

    if(deltaRGB==0){
        pixelh=0;
    }
    if(maxRGB==pixelf.r){
        pixelh=(int)(60*((pixelf.g-pixelf.b)/(deltaRGB))+360)%360;
    }
    if(maxRGB==pixelf.g){
        pixelh=60*((pixelf.b-pixelf.r)/(deltaRGB))+120;
    }
    if(maxRGB==pixelf.b){
        pixelh=60*((pixelf.r-pixelf.g)/(deltaRGB))+240;
    }

    if(minHSV > maxHSV){
        if (pixelh > maxHSV && pixelh < minHSV) {
            float Grey = (0.3 * pixelf.r + 0.59 * pixelf.g + 0.11 * pixelf.b);
            return rsPackColorTo8888(Grey,Grey,Grey,pixelf.a);
           }
    }else{
        if(!(pixelh >= minHSV && pixelh <= maxHSV) ){
               float Grey = (0.3 * pixelf.r + 0.59 * pixelf.g + 0.11 * pixelf.b);
               return rsPackColorTo8888(Grey,Grey,Grey,pixelf.a);
        }
    }

    return in;
}