package abraham.alvarezcruz.openmarket.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import abraham.alvarezcruz.openmarket.view.FragmentViewPagerListener;
import abraham.alvarezcruz.openmarket.view.FragmentoListaMonedas;
import abraham.alvarezcruz.openmarket.view.FragmentoListaMonedasFavoritas;
import abraham.alvarezcruz.openmarket.view.ListadoMonedasListener;

public class TabsAdapter extends FragmentStatePagerAdapter {

    public static String TAG_NAME = TabsAdapter.class.getSimpleName();

    private FragmentViewPagerListener fragmentViewPagerListener;

    public TabsAdapter(@NonNull FragmentManager fm, int comportamiento, FragmentViewPagerListener fragmentViewPagerListener){
        super(fm, comportamiento);
        this.fragmentViewPagerListener = fragmentViewPagerListener;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        ListadoMonedasListener listadoMonedasListener = null;

        switch (position){

            // Todas
            case 0:
                fragment = new FragmentoListaMonedas();
                listadoMonedasListener = (ListadoMonedasListener) fragment;
                break;

            // Favoritas
            case 1:
                fragment = new FragmentoListaMonedasFavoritas();
                listadoMonedasListener = (ListadoMonedasListener) fragment;
                break;
        }

        if (listadoMonedasListener != null && fragmentViewPagerListener != null){
            fragmentViewPagerListener.onFragmentReady(listadoMonedasListener);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
