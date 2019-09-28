package xyz.resizer.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import xyz.resizer.MainActivity;
import xyz.resizer.MainViewModel;
import xyz.resizer.R;

public class ResultFragment extends Fragment {
    static final String LOG_TAG = "ResultFragment";

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final MainActivity mainActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_result, container, false);

        MainViewModel viewModel = ViewModelProviders.of(mainActivity).get(MainViewModel.class);

        final Button shareButton = view.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Share button clicked");

                mainActivity.startSharing();
            }
        });

        // Set up observer for processed bitmap
        final ImageView resultImageView = view.findViewById(R.id.resultImageView);
        final ProgressBar resultProgressBar = view.findViewById(R.id.resultProgressBar);
        Observer<Bitmap> processedBitmapObserver = new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                if (bitmap != null) {
                    resultImageView.setImageBitmap(bitmap);
                    resultProgressBar.setVisibility(View.GONE);
                    shareButton.setVisibility(View.VISIBLE);
                }
            }
        };
        viewModel.getProcessedBitmap().observe(this, processedBitmapObserver);

        return view;
    }
}
