package xyz.resizer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

import xyz.resizer.service.ImageResizerTask;
import xyz.resizer.service.ImageResizerTaskParams;
import xyz.resizer.service.WebServiceImageResizerTask;

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = "MainActivity";

    static final int REQUEST_SELECT_IMAGE = 1;

    static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;

    static final String MIME_TYPE = "image/*";

    private MainViewModel viewModel;

    ViewPager viewPager;

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
        MainFragmentPagerAdapter mainFragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(mainFragmentPagerAdapter);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGE) {
            handleRequestSelectImageResult(resultCode, data);
        } else if (requestCode == UCrop.REQUEST_CROP) {
            handleRequestCropResult(resultCode, data);
        }
    }

    /**
     * Handle the result of the image choosing activity by launching the UCrop activity on success
     *
     * @param resultCode
     * @param data
     */
    private void handleRequestSelectImageResult(int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Uri editedUri = Uri.fromFile(new File(getCacheDir(), "edited_temp"));

            // Start uCrop
            UCrop uCrop = UCrop.of(imageUri, editedUri);
            uCrop.start(this);
        } else {
            Log.d(LOG_TAG, "No image selected");
        }
    }

    /**
     * Handle the result of the UCrop activity by updating the view model
     * @param resultCode
     * @param data
     */
    private void handleRequestCropResult(int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(LOG_TAG, "Cropping successful");
            final Uri resultUri = UCrop.getOutput(data);

            try {
                // Load bitmap and save in view model
                viewModel.loadRawBitmap(getContentResolver(), resultUri);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed to load Bitmap", e);
                Toast.makeText(getApplicationContext(), "Failed to open image", Toast.LENGTH_SHORT).show();
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.e(LOG_TAG, "UCrop exited with error", cropError);
            Toast.makeText(getApplicationContext(), "Editing image failed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Start an activity for choosing images
     */
    public void startChoosing() {
        createChooseImageIntent();
    }

    /**
     * Clear any previously processed bitmap, then start a new conversion task and navigate to the ResultFragment
     * @param toSquare convert to square?
     */
    public void startConversion(boolean toSquare) {
        // clear the output
        viewModel.getProcessedBitmap().setValue(null);

        // Determine conversion mode
        ImageResizerTaskParams.ConversionMode conversionMode;
        Bitmap inputBitmap = viewModel.getRawBitmap().getValue();
        if (toSquare) {
            if (inputBitmap.getHeight() >= inputBitmap.getWidth()) {
                conversionMode = ImageResizerTaskParams.ConversionMode.PORTRAIT_TO_SQUARE;
            } else {
                conversionMode = ImageResizerTaskParams.ConversionMode.LANDSCAPE_TO_SQUARE;
            }
        } else {
            if (inputBitmap.getHeight() >= inputBitmap.getWidth()) {
                conversionMode = ImageResizerTaskParams.ConversionMode.PORTRAIT_TO_PORTRAIT;
            } else {
                conversionMode = ImageResizerTaskParams.ConversionMode.LANDSCAPE_TO_LANDSCAPE;
            }
        }

        // start the task
        ImageResizerTask imageResizerTask = new WebServiceImageResizerTask(getApplicationContext().getResources());
        ImageResizerTaskParams taskParams = new ImageResizerTaskParams(viewModel.getRawBitmap().getValue(),
                viewModel.getProcessedBitmap(), conversionMode);
        imageResizerTask.execute(taskParams);

        // switch to the rotate / crop fragment
        viewPager.setCurrentItem(MainFragmentPagerAdapter.RESULT_FRAGMENT);
    }

    /**
     * Save the processed image to external storage, then launch a share intent
     */
    public void startSharing() {
        String url = saveProcessedBitmap();
        createShareIntent(url);
    }

    /**
     * Clear the input image, then navigate to the FrontFragment
     */
    public void startOver() {
        viewModel.getRawBitmap().setValue(null);
        goBack();
    }

    /**
     * Navigate to the FrontFragment
     */
    public void goBack() {
        viewPager.setCurrentItem(MainFragmentPagerAdapter.FRONT_FRAGMENT);
    }

    /**
     * Persist the processed image file to external storage
     * @return the URL corresponding to the persisted file
     */
    private String saveProcessedBitmap() {
        Bitmap processedBitmap = viewModel.getProcessedBitmap().getValue();

        // TODO: Title and description
        String url = MediaStore.Images.Media.insertImage(getContentResolver(), processedBitmap, "FOO", "BAR");
        if (url == null) {
            Log.e(LOG_TAG, "Failed to store processed image");
        } else {
            Log.d(LOG_TAG, "Stored image to " + url);
        }

        return url;
    }

    /**
     * Start an activity using a GET_CONTENT request for image types
     */
    private void createChooseImageIntent() {
        Intent imageSelectIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageSelectIntent.setType(MIME_TYPE);
        startActivityForResult(imageSelectIntent, REQUEST_SELECT_IMAGE);
    }

    /**
     * Start an activity using an ACTION_SEND request using the processed image file
     *
     * @param mediaPath
     */
    private void createShareIntent(String mediaPath) {

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(MIME_TYPE);

        // Create a URI from the string
        Uri uri = Uri.parse(mediaPath);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }
}
