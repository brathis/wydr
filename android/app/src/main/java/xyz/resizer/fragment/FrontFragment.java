package xyz.resizer.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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

public class FrontFragment extends Fragment {

    static final String LOG_TAG = "FrontFragment";

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
        final MainActivity mainActivity = (MainActivity) getActivity();

        final View view = inflater.inflate(R.layout.fragment_front, container, false);

        final MainViewModel viewModel = ViewModelProviders.of(mainActivity).get(MainViewModel.class);

        Button convertButton = view.findViewById(R.id.chooseButton);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Choose button clicked");

                mainActivity.startChoosing();
            }
        });

        Button startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Start button clicked");

                mainActivity.startConversion();
            }
        });

        // Set up observer for raw bitmap to update frontImageView
        final ImageView frontImageView = view.findViewById(R.id.frontImageView);
        final Observer<Bitmap> rawBitmapObserver = new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                frontImageView.setImageBitmap(bitmap);
            }
        };
        viewModel.getRawBitmap().observe(this, rawBitmapObserver);

        return view;
    }
}
