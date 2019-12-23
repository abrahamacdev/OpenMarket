package abraham.alvarezcruz.openmarket.view;

import android.app.ActivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.concurrent.TimeUnit;

import abraham.alvarezcruz.openmarket.R;
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {

    public static String TAG_NAME = MainActivity.class.getSimpleName();

    private View view;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(R.id.root);

        // TODO Necesario para poder utilizar ciertas librerías
        AndroidThreeTen.init(this);

        FragmentoListaMonedas fragmentoListaMoneda = new FragmentoListaMonedas();
        escucharMonedaClickada(fragmentoListaMoneda.getMonedaClickeadaSubject());
        escucharAperturaExchanges(fragmentoListaMoneda.getFabAperturaExchangesSubject());

        fragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmentos, fragmentoListaMoneda)
                .commit();

        Runtime runtime = Runtime.getRuntime();

        // Todo Solo descomentar cuando se quiera monitorizar la memoria disponible y usada por la aplicación
        /*
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .subscribe((tick) -> {

                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    activityManager.getMemoryInfo(mi);
                    double availableMegs = mi.availMem / 0x100000L;
                    final long usedMemInMB=(runtime.totalMemory() - runtime.freeMemory()) / 1048576L;

                    String dispo = "Disponible -> " + availableMegs + "Mb";
                    String usada = "Usada -> " + usedMemInMB + "Mb";

                    Log.e(TAG_NAME, dispo + "\n" + usada);
                });
        */
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

        FragmentoGraficaMoneda fragmentoGraficaMoneda = new FragmentoGraficaMoneda(moneda);
        fragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmentos, fragmentoGraficaMoneda, FragmentoGraficaMoneda.TAG_NAME)
                .addToBackStack(FragmentoGraficaMoneda.TAG_NAME)
                .commit();
    }

    private void mostrarFragmentoExchanges(){

        FragmentoListaExchanges fragmentoListaExchanges = new FragmentoListaExchanges();
        fragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmentos, fragmentoListaExchanges, FragmentoListaExchanges.TAG_NAME)
                .addToBackStack(FragmentoListaExchanges.TAG_NAME)
                .commit();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
