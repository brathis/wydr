package xyz.resizer;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import xyz.resizer.fragment.FrontFragment;
import xyz.resizer.fragment.ResultFragment;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    FrontFragment frontFragment;
    ResultFragment resultFragment;

    public static final int FRONT_FRAGMENT = 0;
    public static final int RESULT_FRAGMENT = 1;

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

        frontFragment = new FrontFragment();
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
        return 2;
    }
}
