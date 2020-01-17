package abraham.alvarezcruz.openmarket.view;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.List;

import abraham.alvarezcruz.openmarket.R;
import abraham.alvarezcruz.openmarket.adapter.TabsAdapter;
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.utils.Utils;
import io.reactivex.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity implements FragmentListener{

    public static String TAG_NAME = MainActivity.class.getSimpleName();

    private View view;
    private ViewPager viewPager;

    private PublishSubject<Moneda> monedaClickeadaSubject;
    private PublishSubject<View> fabExchangesClickeadoSubject;
    private Fragment[] fragmentosListadosMonedas;
    public TabsAdapter tabsAdapter;
    public TabLayout tabLayout;

    private Fragment fragmentoActivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(R.id.root);

        // Atendemos los clicks en los fragments
        escucharMonedaClickada();
        escucharAperturaExchanges();

        // Creamos los fragmentos que le pasaremos al TabAdapter
        crearFragmentos();

        // Guardamos la versión del layout que se está utilizando
        Utils.setModo(String.valueOf(view.getTag()));

        // TODO Necesario para poder utilizar ciertas librerías
        AndroidThreeTen.init(this);

        // Iniciamos las vistas
        initViews();

        // Cargamos los tabs del viewpager
        cargarTabs();
    }

    private void initViews(){
        // TabLayout que mostrará los labels: "Todas" y "Favoritas"
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.label_viewpager_todas)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.label_viewpager_favoritas)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // ViewPager que se encargará de cargar el fragmento correspondiente
        viewPager =(ViewPager)findViewById(R.id.viewPager);
    }

    private void cargarTabs(){
        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragmentosListadosMonedas);

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

    public void crearFragmentos(){

        fragmentosListadosMonedas = new Fragment[2];
        fragmentosListadosMonedas[0] = new FragmentoListaMonedas(monedaClickeadaSubject, fabExchangesClickeadoSubject);
        fragmentosListadosMonedas[1] = new FragmentoListaMonedasFavoritas(monedaClickeadaSubject);

        if (fragmentoActivo == null){
            fragmentoActivo = fragmentosListadosMonedas[0];
        }
    }

    /**
     * Cada vez que una moneda del {@link FragmentoListaMonedas} sea clickeada, recibiremos un evento
     * para ver en detalle más datos sobre la moneda
     */
    @SuppressLint("CheckResult")
    private void escucharMonedaClickada(){

        monedaClickeadaSubject = PublishSubject.create();
        monedaClickeadaSubject.subscribe(this::mostrarFragmentoDetalleMoneda, Throwable::printStackTrace);
    }

    /**
     * Cada vez que se clickee el fab button de {@link FragmentoListaMonedas}, recibiremos un evento
     * para mostrar todos los exchanges disponibles
     */
    @SuppressLint("CheckResult")
    private void escucharAperturaExchanges(){

        fabExchangesClickeadoSubject = PublishSubject.create();
        fabExchangesClickeadoSubject.subscribe(
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

        FragmentoListaExchanges fragmentoListaExchanges = new FragmentoListaExchanges(Utils.getModo(), this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorFragmentos, fragmentoListaExchanges, FragmentoListaExchanges.TAG_NAME)
                .addToBackStack(FragmentoListaExchanges.TAG_NAME)
                .commit();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //boolean estaApaisado = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        //setContentView(R.layout.activity_main);

        // Atendemos los clicks en los fragments
        escucharMonedaClickada();
        escucharAperturaExchanges();

        // Creamos los fragmentos que le pasaremos al TabAdapter
        crearFragmentos();

        // Guardamos la versión del layout que se está utilizando
        Utils.setModo(String.valueOf(view.getTag()));

        //initViews();

        // Iniciamos las vistas
        cargarTabs();

        // En caso de que se esté mostrando la lista de exchanges o una moneda en detalle,
        // evitamos que desaparezca de la pantalla
        if (getSupportFragmentManager().getBackStackEntryCount() > 0){

            FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            Fragment currentFragment = fragmentManager.findFragmentByTag(fragmentTag);

            fragmentManager.popBackStackImmediate();

            fragmentManager.beginTransaction()
                    .replace(R.id.contenedorFragmentos, currentFragment, FragmentoListaExchanges.TAG_NAME)
                    .addToBackStack(FragmentoListaExchanges.TAG_NAME)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onFragmentClosed(Fragment fragment) {
        // Manejamos nosotros mismos la salida
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().executePendingTransactions();
    }
}
