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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import xyz.resizer.MainActivity;
import xyz.resizer.MainFragmentPagerAdapter;
import xyz.resizer.MainViewModel;
import xyz.resizer.R;

public class ResultFragment extends Fragment {

    static final String LOG_TAG = "ResultFragment";

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     *
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * <p>Any restored child fragments will be created before the base
     * <code>Fragment.onCreate</code> method returns.</p>
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MainActivity mainActivity = (MainActivity) requireActivity();

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back to FrontFragment
                mainActivity.transitionToFragment(MainFragmentPagerAdapter.FRONT_FRAGMENT);
            }
        };
        mainActivity.getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

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
        final MainActivity mainActivity = (MainActivity) requireActivity();

        View view = inflater.inflate(R.layout.fragment_result, container, false);

        final MainViewModel viewModel = ViewModelProviders.of(mainActivity).get(MainViewModel.class);

        final Button shareButton = view.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Share button clicked");

                mainActivity.startSharing();
            }
        });

        final Button restartButton = view.findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Restart button clicked");

                mainActivity.startOver();
            }
        });

        // Set up observer for processed bitmap
        final ImageView resultImageView = view.findViewById(R.id.resultImageView);
        final ProgressBar resultProgressBar = view.findViewById(R.id.resultProgressBar);
        Observer<Bitmap> processedBitmapObserver = new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                if (bitmap != null) {
                    viewModel.setInProgress(false);
                    resultImageView.setImageBitmap(bitmap);
                    resultImageView.setVisibility(View.VISIBLE);
                    resultProgressBar.setVisibility(View.GONE);
                    shareButton.setVisibility(View.VISIBLE);
                    restartButton.setVisibility(View.VISIBLE);
                }
            }
        };
        viewModel.getProcessedBitmap().observe(this, processedBitmapObserver);

        return view;
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();

        MainActivity mainActivity = (MainActivity) requireActivity();
        MainViewModel viewModel = ViewModelProviders.of(mainActivity).get(MainViewModel.class);
        if (viewModel.isInProgress()) {
            final Button shareButton = mainActivity.findViewById(R.id.shareButton);
            final Button restartButton = mainActivity.findViewById(R.id.restartButton);
            final ImageView resultImageView = mainActivity.findViewById(R.id.resultImageView);
            final ProgressBar resultProgressBar = mainActivity.findViewById(R.id.resultProgressBar);

            shareButton.setVisibility(View.GONE);
            restartButton.setVisibility(View.GONE);
            resultImageView.setImageResource(android.R.color.transparent);
            resultImageView.setVisibility(View.INVISIBLE);
            resultProgressBar.setVisibility(View.VISIBLE);
        }
    }
}
