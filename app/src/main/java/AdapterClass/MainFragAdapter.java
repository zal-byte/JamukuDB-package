package AdapterClass;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import FragmentClass.CartFrag;
import FragmentClass.MainFrag;
import FragmentClass.ProfileFrag;

public class MainFragAdapter extends FragmentPagerAdapter {
    public Activity activity;
    public MainFragAdapter(@NonNull FragmentManager fm, int behavior, Activity activity) {
        super(fm, behavior);
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MainFrag(activity);
            case 1:
                return new CartFrag(activity);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
