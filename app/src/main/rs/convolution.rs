/*
* Source : https://github.com/fritzprix/ConvolutionRs
*/

#pragma version(1)
#pragma rs java_package_name(com.example.jdavid004.projetandroids6)


rs_allocation gIn;  //Bitmap input

int* kmatrix;   //matrix filter
uint32_t ksize; //height (= width) of the matrix
int kdiv;       //division's factor
bool normal;    //boolean to determine if it is necessary to normalize

static uint32_t width;  //image's width
static uint32_t height; //image's height

void root(const uchar4* in, uchar4* out, uint32_t x, uint32_t y) //"in" is the input pixel, "out" the output one ; x and y are the coordinates of the pixel
{
    if(x < 1 || y < 1) //we pass over the edges
        return;
    if((x > width - 1) || (y > height - 1))
        return;
    uint8_t kx,ky;
    float4 temp = 0;
    const uchar4* kin = in - (ksize / 2) - (ksize / 2) * width; //array of pixels in the image covered by the matrix
    for(kx = 0; kx < ksize; kx++)   //run through the matrix with kx and ky
    {
        for(ky = 0; ky < ksize;ky++)
        {
            temp += rsUnpackColor8888(kin[kx + ksize * ky]) * kmatrix[kx + ksize * ky];
        }
    }
    if(normal) { temp /= kdiv; }    //normalization
    temp.a = 1.0f;
    *out = rsPackColorTo8888(temp);
}

void init()
{

}

void setup()
{
    width = rsAllocationGetDimX(gIn);
    height = rsAllocationGetDimY(gIn);
    rsDebug("KSize : ",ksize);
}