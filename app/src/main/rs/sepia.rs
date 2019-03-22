#pragma  version (1)
#pragma  rs  java_package_name(com.example.jdavid004.projetandroids6)
#pragma rs_fp_relaxed


uchar4  RS_KERNEL  sepia(uchar4  in) {
    float4  pixelf = rsUnpackColor8888(in);
    float red = (pixelf.r*0.393 + pixelf.g*0.769 + pixelf.b*0.189);
    float green = (pixelf.r*0.349 + pixelf.g*0.686 + pixelf.b*0.168);
    float blue = (pixelf.r*0.272 + pixelf.g*0.534 + pixelf.b*0.131);
    return  rsPackColorTo8888(red , green , blue , pixelf.a);
}