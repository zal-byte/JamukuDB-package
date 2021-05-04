package AdapterClass;

import android.app.Activity;
import android.widget.ActionMenuView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import FragmentClass.LoginFrag;
import FragmentClass.SignupFrag;

public class AuthFragAdapter extends FragmentPagerAdapter {
    public Activity activity;
    public AuthFragAdapter(@NonNull FragmentManager fm, int behavior, Activity activity) {
        super(fm, behavior);
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new LoginFrag(activity);
            case 1:
                return new SignupFrag(activity);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
