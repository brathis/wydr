package xyz.resizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;

import xyz.resizer.service.ImageResizerTask;
import xyz.resizer.service.ImageResizerTaskParams;
import xyz.resizer.service.WebServiceImageResizerTask;

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = "MainActivity";

    static final int REQUEST_SELECT_IMAGE = 1;

    private MainViewModel viewModel;

    private ImageResizerTask imageResizerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);

        // Set up observer to watch for changes of the processed bitmap
        final Observer<Bitmap> processedBitmapObserver = new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                ImageView mainImageView = findViewById(R.id.mainImageView);
                mainImageView.setImageBitmap(bitmap);
            }
        };

        imageResizerTask = new WebServiceImageResizerTask(getApplicationContext());

        // Prompt user to select image
        Intent imageSelectIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageSelectIntent.setType("image/*");
        startActivityForResult(imageSelectIntent, REQUEST_SELECT_IMAGE);
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
                    viewModel.loadRawBitmap(getContentResolver(), imageUri);

                    // start the task
                    ImageResizerTaskParams taskParams = new ImageResizerTaskParams(viewModel.getRawBitmap().getValue(),
                            viewModel.getProcessedBitmap());
                    imageResizerTask.execute(taskParams);

                } catch (IOException e) {
                    Log.e(LOG_TAG, "Failed to load Bitmap", e);
                    // TODO: Show error to user
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
