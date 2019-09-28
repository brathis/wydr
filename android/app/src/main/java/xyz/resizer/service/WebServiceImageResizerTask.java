package xyz.resizer.service;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import xyz.resizer.R;

/**
 * ImageResizerTask which delegates the heavy lifting to a web service.
 */
public class WebServiceImageResizerTask extends ImageResizerTask {

    static final String LOG_TAG = "WebServiceImageResizerTask";

    private URL webServiceUrl;

    private MutableLiveData<Bitmap> bitmapMutableLiveData;

    public WebServiceImageResizerTask(Context context) {
        Resources resources = context.getResources();
        String webServiceUrlString = resources.getString(R.string.web_service_url);
        try {
            webServiceUrl = new URL(webServiceUrlString);
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Web service URL is malformed: " + webServiceUrlString);
        }
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This will normally run on a background thread. But to better
     * support testing frameworks, it is recommended that this also tolerates
     * direct execution on the foreground thread, as part of the {@link #execute} call.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected ImageResizerTaskResult doInBackground(ImageResizerTaskParams... params) {
        InputStream stream;
        HttpURLConnection connection = null;
        ImageResizerTaskResult result = new ImageResizerTaskResult();

        if (params.length != 1) {
            result.setStatus(ImageResizerTaskResult.Status.ERROR);
            result.setErrorMessage("Only one param at a time is supported");
            return result;
        }

        ImageResizerTaskParams param = params[0];

        // Retain a reference to the MutableLiveData to update the UI on completion of the task
        bitmapMutableLiveData = param.getOutputBitmapMutableLiveData();

        // Create a copy of the input bitmap to be used within the worker thread
        Bitmap bitmap = Bitmap.createBitmap(param.getInputBitmap());

        result.setStatus(ImageResizerTaskResult.Status.IDLE);
        result.setBitmap(bitmap);

        try {
            connection = (HttpURLConnection) webServiceUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "image/csv");
            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            outputStream.flush();
            outputStream.close();

            result.setStatus(ImageResizerTaskResult.Status.RUNNING);
            Log.d(LOG_TAG, "Sending CSV to server");
            connection.connect();

            stream = connection.getInputStream();
            result.setBitmap(BitmapFactory.decodeStream(stream));
            result.setStatus(ImageResizerTaskResult.Status.FINISHED);

        } catch (IOException e) {
            result.setStatus(ImageResizerTaskResult.Status.ERROR);
            result.setErrorMessage(e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.
     * To better support testing frameworks, it is recommended that this be
     * written to tolerate direct execution as part of the execute() call.
     * The default version does nothing.</p>
     *
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param imageResizerTaskResult The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(ImageResizerTaskResult imageResizerTaskResult) {
        super.onPostExecute(imageResizerTaskResult);

        if (imageResizerTaskResult.getStatus() == ImageResizerTaskResult.Status.FINISHED) {
            if (imageResizerTaskResult.getBitmap() != null) {
                // Update the ViewModel
                bitmapMutableLiveData.setValue(imageResizerTaskResult.getBitmap());
            } else {
                Log.e(LOG_TAG, "Task finished with 'null' bitmap");
            }
        } else {
            Log.e(LOG_TAG, "Task finished without state being 'FINISHED', state is " + imageResizerTaskResult.getStatus().toString());
            if (imageResizerTaskResult.getStatus() == ImageResizerTaskResult.Status.ERROR) {
                Log.e(LOG_TAG, "Error message: " + imageResizerTaskResult.getErrorMessage());
            }
        }
    }
}
