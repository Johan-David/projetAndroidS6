#pragma  version (1)
#pragma  rs  java_package_name(com.example.jdavid004.projetandroids6)
#pragma rs_fp_relaxed

float rand=0;

uchar4  RS_KERNEL  colorize(uchar4  in) {

    float3 pixelhsv;
    const  float4  pixelf = rsUnpackColor8888(in);

    float minRGB=min(pixelf.r,min(pixelf.g,pixelf.b));
    float maxRGB=max(pixelf.r,max(pixelf.g,pixelf.b));
    float deltaRGB=maxRGB-minRGB;

    pixelhsv.s0=rand;

    if(maxRGB==0){
        pixelhsv.s1=0;
    }else{
        pixelhsv.s1=(1-(minRGB/maxRGB));
    }

    pixelhsv.s2=maxRGB;

    int ti=((int)(pixelhsv.s0/60))%6;
    float f=(pixelhsv.s0/60)-ti;
    float l=pixelhsv.s2*(1-pixelhsv.s1);
    float m=pixelhsv.s2*(1-f*pixelhsv.s1);
    float n=pixelhsv.s2*(1-(1-f)*pixelhsv.s1);

    float R,G,B;
    if(ti==0){
        R=pixelhsv.s2;
        G=n;
        B=l;
    }else if(ti==1){
        R=m;
        G=pixelhsv.s2;
        B=l;
    }else if(ti==2){
        R=l;
        G=pixelhsv.s2;
        B=n;
    }else if(ti==3){
        R=l;
        G=m;
        B=pixelhsv.s2;
    }else if(ti==4){
        R=n;
        G=l;
        B=pixelhsv.s2;
    }else if(ti==5){
        R=pixelhsv.s2;
        G=l;
        B=m;
    }

    return  rsPackColorTo8888(R,G,B,pixelf.a);
}
