package com.example.jdavid004.projetandroids6;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by bdarmet on 05/04/19.
 */

public class Preview {
    private ImageView imagePreview;
    private Treatment treatmentUse;

    Preview(ImageView Preview, Treatment treatmentUse){
        this.imagePreview = Preview;
        this.treatmentUse = treatmentUse;
    }

    public Treatment getTreatmentUse() {
        return treatmentUse;
    }

    public ImageView getImagePreview() {
        return imagePreview;
    }
}
