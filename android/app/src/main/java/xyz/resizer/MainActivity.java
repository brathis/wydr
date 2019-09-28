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

    private MainFragmentPagerAdapter mainFragmentPagerAdapter;

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
        mainFragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
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
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                Uri editedUri = Uri.fromFile(new File(getCacheDir(), "edited_temp"));

                // Start uCrop
                UCrop uCrop = UCrop.of(imageUri, editedUri);
                uCrop.start(this);
            } else {
                Log.e(LOG_TAG, "Failed to select image");
                Toast.makeText(getApplicationContext(), "Failed to select image", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
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
    }

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

    public void startChoosing() {
        createChooseImageIntent();
    }

    public void startConversion() {
        viewModel.setInProgress(true);

        // start the task
        ImageResizerTask imageResizerTask = new WebServiceImageResizerTask(getApplicationContext());
        ImageResizerTaskParams taskParams = new ImageResizerTaskParams(viewModel.getRawBitmap().getValue(),
                viewModel.getProcessedBitmap());
        imageResizerTask.execute(taskParams);

        // switch to the rotate / crop fragment
        viewPager.setCurrentItem(MainFragmentPagerAdapter.RESULT_FRAGMENT);
    }

    private void createChooseImageIntent() {
        Intent imageSelectIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageSelectIntent.setType(MIME_TYPE);
        startActivityForResult(imageSelectIntent, REQUEST_SELECT_IMAGE);
    }

    public void startSharing() {
        String url = saveProcessedBitmap();
        createInstagramIntent(url);
    }

    private void createInstagramIntent(String mediaPath) {

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

    public void transitionToFragment(int position) {
        viewPager.setCurrentItem(position);
    }

    public void startOver() {
        viewModel.reset();
        viewPager.setCurrentItem(MainFragmentPagerAdapter.FRONT_FRAGMENT);
    }
}
