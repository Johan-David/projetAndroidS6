#pragma  version (1)
#pragma  rs  java_package_name(com.android.rssample)


uint volatile minValue = 255;
uint volatile maxValue = 0;
static float LUT[256];


uchar4  RS_KERNEL transformation(uchar4 in) {
    //Convert input uchar4 to float4
    float4 f4 = rsUnpackColor8888(in);
    int color = f4.r;
    float4 newColor = LUT[color];
    rsDebug("Value of LUT: ", LUT[color]);
    return rsPackColorTo8888(newColor.r, newColor.g, newColor.b, f4.a);
}

void changeLUT() {
    //init the array with zeros
    for (int i = 0; i < 256; i++) {
            LUT[i] = (255*(i-minValue))/(maxValue-minValue);
            if(LUT[i] < 0){
            LUT[i] = 0;
            }
            if(LUT[i] > 255){
            LUT[i] = 255;
            }
    }
}
