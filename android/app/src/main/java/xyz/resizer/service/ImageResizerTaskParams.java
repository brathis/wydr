package xyz.resizer.service;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

public class ImageResizerTaskParams {

    public enum ConversionMode {
        PORTRAIT_TO_SQUARE,
        LANDSCAPE_TO_SQUARE,
        PORTRAIT_TO_PORTRAIT,
        LANDSCAPE_TO_LANDSCAPE
    }

    /**
     * The input bitmap to be processed
     */
    private Bitmap inputBitmap;

    /**
     * The output bitmap MutableLiveData to be updated upon completion
     */
    private MutableLiveData<Bitmap> outputBitmapMutableLiveData;

    /**
     * Which way to convert
     */
    private ConversionMode conversionMode;

    public ImageResizerTaskParams(Bitmap inputBitmap, MutableLiveData<Bitmap> outputBitmapMutableLiveData, ConversionMode conversionMode) {
        this.inputBitmap = inputBitmap;
        this.outputBitmapMutableLiveData = outputBitmapMutableLiveData;
        this.conversionMode = conversionMode;
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

    public ConversionMode getConversionMode() {
        return conversionMode;
    }
}
