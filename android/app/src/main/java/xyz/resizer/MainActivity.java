package xyz.resizer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import java.io.IOException;

import xyz.resizer.fragment.MainFragmentPagerAdapter;
import xyz.resizer.service.ImageResizerTask;
import xyz.resizer.service.ImageResizerTaskParams;
import xyz.resizer.service.WebServiceImageResizerTask;

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = "MainActivity";

    static final int REQUEST_SELECT_IMAGE = 1;

    static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;

    private MainViewModel viewModel;

    private MainFragmentPagerAdapter mainFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ensure that the necessary permissions have been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "WRITE_EXTERNAL_STORAGE has not been granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }

        // Set up layout & fragments
        setContentView(R.layout.activity_main);
        mainFragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(mainFragmentPagerAdapter);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);

        // Prompt user to select image
        startChooseImageActivity();
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_SELECT_IMAGE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                try {
                    // Load bitmap and save in view model
                    viewModel.loadRawBitmap(getContentResolver(), imageUri);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Failed to load Bitmap", e);
                    Toast.makeText(getApplicationContext(), "Failed to open image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void convertButtonClicked(View view) {
        Log.d(LOG_TAG, "Convert button clicked");

        // start the task
        ImageResizerTask imageResizerTask = new WebServiceImageResizerTask(getApplicationContext());
        ImageResizerTaskParams taskParams = new ImageResizerTaskParams(viewModel.getRawBitmap().getValue(),
                viewModel.getProcessedBitmap());
        imageResizerTask.execute(taskParams);
    }

    public void shareButtonClicked(View view) {
        // TODO: Launch the share intent
        Log.d(LOG_TAG, "Share button clicked");

        saveProcessedBitmap(viewModel.getProcessedBitmap().getValue());
    }

    protected void saveProcessedBitmap(Bitmap processedBitmap) {
        String url = MediaStore.Images.Media.insertImage(getContentResolver(), processedBitmap, "FOO", "BAR");
        if (url == null) {
            Log.e(LOG_TAG, "Failed to store processed image");
        } else {
            Log.d(LOG_TAG, "Stored image to " + url);
        }
    }

    protected void startChooseImageActivity() {
        Intent imageSelectIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageSelectIntent.setType("image/*");
        startActivityForResult(imageSelectIntent, REQUEST_SELECT_IMAGE);
    }
}
