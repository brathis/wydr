package xyz.resizer.service;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

public class ImageResizerTaskParams {
    /**
     * The input bitmap to be processed
     */
    private Bitmap inputBitmap;

    /**
     * The output bitmap MutableLiveData to be updated upon completion
     */
    private MutableLiveData<Bitmap> outputBitmapMutableLiveData;

    public ImageResizerTaskParams(Bitmap inputBitmap, MutableLiveData<Bitmap> outputBitmapMutableLiveData) {
        this.inputBitmap = inputBitmap;
        this.outputBitmapMutableLiveData = outputBitmapMutableLiveData;
    }

    public Bitmap getInputBitmap() {
        return inputBitmap;
    }

    public void setInputBitmap(Bitmap inputBitmap) {
        this.inputBitmap = inputBitmap;
    }

    public MutableLiveData<Bitmap> getOutputBitmapMutableLiveData() {
        return outputBitmapMutableLiveData;
    }

    public void setOutputBitmapMutableLiveData(MutableLiveData<Bitmap> outputBitmapMutableLiveData) {
        this.outputBitmapMutableLiveData = outputBitmapMutableLiveData;
    }
}
