package xyz.resizer.fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    FrontFragment frontFragment;
    ResultFragment resultFragment;

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
            case 0:
                return frontFragment;
            case 1:
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
