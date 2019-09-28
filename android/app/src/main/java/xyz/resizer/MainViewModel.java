package xyz.resizer;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Bitmap> rawBitmap = new MutableLiveData<>();
    private MutableLiveData<Bitmap> processedBitmap = new MutableLiveData<>();

    public void loadRawBitmap(ContentResolver contentResolver, Uri uri) throws IOException {
        rawBitmap.setValue(MediaStore.Images.Media.getBitmap(contentResolver, uri));
    }

    public MutableLiveData<Bitmap> getRawBitmap() {
        return rawBitmap;
    }

    public MutableLiveData<Bitmap> getProcessedBitmap() {
        return processedBitmap;
    }
}
