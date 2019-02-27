/*
* Source : https://github.com/fritzprix/ConvolutionRs
*/

#pragma version(1)
#pragma rs java_package_name(com.example.jdavid004.projetandroids6)


rs_allocation gIn;  //Bitmap d'entrée

int* kmatrix;   //matrice filtre
uint32_t ksize; //hauteur (= largeur) de la matrice
int kdiv;       //facteur de division
bool normal;    //booléen pour déterminer si il faut normaliser

static uint32_t width;  //largeur de l'image
static uint32_t height; //hauteur de l'image

void root(const uchar4* in, uchar4* out, uint32_t x, uint32_t y) //in est le pixel d'entré, out celui de sortie ; x et y sont les coordonnées du pixel
{
    if(x < 1 || y < 1) //on ignore les bords
        return;
    if((x > width - 1) || (y > height - 1))
        return;
    uint8_t kx,ky;
    float4 temp = 0;
    const uchar4* kin = in - (ksize / 2) - (ksize / 2) * width; //tableau des pixels de l'image couvert par la matrice
    for(kx = 0; kx < ksize; kx++)   //parcours de la matrice avec kx et ky
    {
        for(ky = 0; ky < ksize;ky++)
        {
            temp += rsUnpackColor8888(kin[kx + ksize * ky]) * kmatrix[kx + ksize * ky];
        }
    }
    if(normal) { temp /= kdiv; }    //normalisation
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