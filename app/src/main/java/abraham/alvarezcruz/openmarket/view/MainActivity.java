package abraham.alvarezcruz.openmarket.view;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.jakewharton.threetenabp.AndroidThreeTen;

import abraham.alvarezcruz.openmarket.R;
import abraham.alvarezcruz.openmarket.adapter.TabsAdapter;
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.utils.Utils;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import okhttp3.internal.Util;

public class MainActivity extends AppCompatActivity {

    public static String TAG_NAME = MainActivity.class.getSimpleName();

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(R.id.root);

        // Guardamos la versión del layout que se está utilizando
        Utils.setModo(String.valueOf(view.getTag()));

        // TODO Necesario para poder utilizar ciertas librerías
        AndroidThreeTen.init(this);

        // Iniciamos las vistas
        initViews();
    }

    private void initViews(){

        // Sobreescribiendo el método "onFragmentReady" conseguimos obtener un "subject" que será
        // por el que escucharemos los clicks en una cierta moneda de la lista para mostrarla en detalle
        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, new FragmentViewPagerListener() {
            @Override
            public void onFragmentReady(ListadoMonedasListener listadoMonedasListener) {
                // Cuando se clickee una moneda queremos recibir el evento para mostrar la moneda en detalle
                escucharMonedaClickada(listadoMonedasListener.getMonedaClickeadaSubject());

                // Si es el "FragmentoListaMonedas", también escucharemos el click del "FabAperturaExchange"
                if (listadoMonedasListener instanceof FragmentoListaMonedas){
                    FragmentoListaMonedas fragmentoListaMonedas = (FragmentoListaMonedas) listadoMonedasListener;
                    escucharAperturaExchanges(fragmentoListaMonedas.getFabAperturaExchangesSubject());
                }
            }
        });

        // TabLayout que mostrará los labels: "Todas" y "Favoritas"
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.label_viewpager_todas)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.label_viewpager_favoritas)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // ViewPager que se encargará de cargar el fragmento correspondiente
        final ViewPager viewPager =(ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(tabsAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }



    /**
     * Cada vez que una moneda del {@link FragmentoListaMonedas} sea clickeada, recibiremos un evento
     * para ver en detalle más datos sobre la moneda
     * @param monedaPublishSubject
     */
    private void escucharMonedaClickada(PublishSubject<Moneda> monedaPublishSubject){

        monedaPublishSubject.subscribe(moneda -> {

            mostrarFragmentoDetalleMoneda(moneda);

        }, error -> {
            error.printStackTrace();
        });
    }

    /**
     * Cada vez que se clickee el fab button de {@link FragmentoListaMonedas}, recibiremos un evento
     * para mostrar todos los exchanges disponibles
     */
    private void escucharAperturaExchanges(Observable<View> fabExchangesObservable){
        fabExchangesObservable.subscribe(
                view -> {

                    mostrarFragmentoExchanges();
                },
                error -> {
                    error.printStackTrace();
                });

    }



    private void mostrarFragmentoDetalleMoneda(Moneda moneda){

        FragmentoGraficaMoneda fragmentoGraficaMoneda = new FragmentoGraficaMoneda();
        Bundle extras = new Bundle();
        extras.putSerializable("moneda", moneda);
        fragmentoGraficaMoneda.setArguments(extras);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorFragmentos, fragmentoGraficaMoneda, FragmentoGraficaMoneda.TAG_NAME)
                .addToBackStack(FragmentoGraficaMoneda.TAG_NAME)
                .commit();
    }

    private void mostrarFragmentoExchanges(){

        FragmentoListaExchanges fragmentoListaExchanges = new FragmentoListaExchanges();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorFragmentos, fragmentoListaExchanges, FragmentoListaExchanges.TAG_NAME)
                .addToBackStack(FragmentoListaExchanges.TAG_NAME)
                .commit();
    }
}
