package xyz.resizer.fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    FrontFragment frontFragment;
    RotateCropFragment rotateCropFragment;
    ResultFragment resultFragment;

    public static final int FRONT_FRAGMENT = 0;
    public static final int ROTATE_CROP_FRAGMENT = 1;
    public static final int RESULT_FRAGMENT = 2;

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

        frontFragment = new FrontFragment();
        rotateCropFragment = new RotateCropFragment();
        resultFragment = new ResultFragment();
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case FRONT_FRAGMENT:
                return frontFragment;
            case ROTATE_CROP_FRAGMENT:
                return rotateCropFragment;
            case RESULT_FRAGMENT:
                return resultFragment;
            default:
                throw new RuntimeException("Invalid fragment position: " + position);
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 3;
    }
}
