package xyz.resizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_SELECT_IMAGE = 1;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

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
        // Instantiate bitmap using the image the user has selected
        if (requestCode == REQUEST_SELECT_IMAGE) {
            if (data != null && data.getDataString() != null) {
                File bitmapFile = new File(data.getDataString());
                bitmap = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath());
                if (bitmap == null) {
                    throw new RuntimeException("Failed to load selected image");
                }
                ImageView mainImageView = (ImageView) findViewById(R.id.mainImageView);
                mainImageView.setImageBitmap(bitmap);
                mainImageView.setVisibility(ImageView.VISIBLE);
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
