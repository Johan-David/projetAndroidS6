/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

#pragma  version (1)
#pragma  rs  java_package_name(com.example.jdavid004.projetandroids6)
#pragma rs_fp_relaxed

const uchar4 *gInPixels;
uchar4 *gOutPixels;
int bmpWidth;
int bmpHeight;
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;

int matrix[];
int m_width;
int m_height;
int factor;



void  root(const int32_t *v_in, int32_t  *v_out, const void *usrData, uint32_t x, uint32_t y) {

    int32_t row_index = *v_in;

    float4 pixels[9];

    int matrixWidth = m_width;
   	int matrixHeight = m_height;
    int diff = (int) (-matrixWidth/2);

		for(int x = 0; x < bmpWidth; x++){
			int col = diff, row = diff;
			double red = 0.0, green = 0.0, blue = 0.0;

			int curPixel = 0;
		    for(int i=0; i < matrixWidth; i++){
			   for(int j=0; j < matrixHeight; j++){
			   		pixels[curPixel] = rsUnpackColor8888(gInPixels[x + row_index + row + col++]);
			   		red += (pixels[curPixel].r * matrix[curPixel]);
			   		green += (pixels[curPixel].g * matrix[curPixel]);
			   		blue += (pixels[curPixel].b * matrix[curPixel]);

			   		curPixel++;
			   }

			   col = diff;
			   row++;

		    }

			red = red / factor;
			green = green / factor;
			blue = blue / factor;

            red = red < 0 ? 0 : red;
			red = red > 255 ? 255 : red;
			green = green < 0 ? 0 : green;
			green = green > 255 ? 255 : green;
			blue = blue < 0 ? 0 : blue;
			blue = blue > 255 ? 255 : blue;

			float3 output = {red, green, blue};
    		gOutPixels[x + row_index] = rsPackColorTo8888(output);
    	}
}

void filter() {
    rsForEach(gScript, gIn, gOut, 0, 0);
}