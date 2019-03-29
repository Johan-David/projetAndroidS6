#pragma  version (1)
#pragma  rs  java_package_name(com.example.jdavid004.projetandroids6)
#pragma rs_fp_relaxed


uchar4  RS_KERNEL  invert(uchar4  in) {
    float4  pixelf = rsUnpackColor8888(in);
    float red = 1-pixelf.r;
    float green = 1-pixelf.g;
    float blue = 1-pixelf.b;
    return  rsPackColorTo8888(red, green, blue, pixelf.a);
}