package abraham.alvarezcruz.openmarket.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;


public class TabsAdapter extends FragmentStatePagerAdapter {

    public static String TAG_NAME = TabsAdapter.class.getSimpleName();

    private FragmentManager fragmentManager;
    private Fragment[] fragmentos;

    public TabsAdapter(@NonNull FragmentManager fm, int comportamiento, Fragment[] fragmentos){
        super(fm, comportamiento);
        this.fragmentManager = fm;
        this.fragmentos = fragmentos;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position >= 0 && position < fragmentos.length){
            return fragmentos[position];
        }
        return null;
    }

    public void clear() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (Fragment fragment : fragmentos) {
            transaction.remove(fragment);
        }
        fragmentos = null;
        transaction.commitAllowingStateLoss();
    }

    @Override
    public int getCount() {
        return fragmentos.length;
    }
}
