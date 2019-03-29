#pragma  version (1)
#pragma  rs  java_package_name(com.example.jdavid004.projetandroids6)
#pragma rs_fp_relaxed


uchar4  RS_KERNEL  thresholding(uchar4  in) {
    float4  pixelf = rsUnpackColor8888(in);
    float red, green, blue;
    if(pixelf.r>0.5){
        red = 1;
    }else{
        red = 0;
    }
    if(pixelf.g>0.5){
        green = 1;
    }else{
        green = 0;
    }
    if(pixelf.b>0.5){
        blue = 1;
    }else{
        blue = 0;
    }
    return  rsPackColorTo8888(red, green, blue, pixelf.a);
}