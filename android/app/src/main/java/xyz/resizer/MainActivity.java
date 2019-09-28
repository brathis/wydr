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

import java.io.File;
import java.io.IOException;

import xyz.resizer.fragment.MainFragmentPagerAdapter;
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

        // Prompt user to select image
        createChooseImageIntent();
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
        createInstagramIntent(MIME_TYPE, url);
    }

    private void createInstagramIntent(String type, String mediaPath) {

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }

    public void transitionToFragment(int position) {
        viewPager.setCurrentItem(position);
    }
}
