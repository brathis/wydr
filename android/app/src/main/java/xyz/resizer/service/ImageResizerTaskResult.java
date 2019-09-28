package xyz.resizer.service;

import android.graphics.Bitmap;

public class ImageResizerTaskResult {
    enum Status {
        IDLE,
        RUNNING,
        FINISHED,
        ERROR
    }

    /**
     * The processed bitmap
     */
    private Bitmap bitmap;

    /**
     * The current status of the task
     */
    private Status status;

    /**
     * An error message, if there was an error
     */
    private String errorMessage;

    public ImageResizerTaskResult() {
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
