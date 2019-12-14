package abraham.alvarezcruz.openmarket.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import abraham.alvarezcruz.openmarket.R;
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {

    private View view;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(R.id.root);

        FragmentoListaMonedas fragmentoListaMoneda = new FragmentoListaMonedas();
        escucharMonedaClickada(fragmentoListaMoneda.getMonedaClickeadaSubject());

        fragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmentos, fragmentoListaMoneda)
                .commit();
    }

    private void escucharMonedaClickada(PublishSubject<Moneda> monedaPublishSubject){

        monedaPublishSubject.subscribe(moneda -> {

            // TODO Implementar que hacer cuando clickeen la moneda
            //Snackbar.make(view, "Se ha clickedo la moneda " + moneda.getNombre(), Snackbar.LENGTH_SHORT).show();
            mostrarDatosConGrafica(moneda);

        }, error -> {
            error.printStackTrace();
        });
    }

    private void mostrarDatosConGrafica(Moneda moneda){

        FragmentoGraficaMoneda fragmentoGraficaMoneda = new FragmentoGraficaMoneda(moneda);
        fragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmentos, fragmentoGraficaMoneda, FragmentoGraficaMoneda.TAG_NAME)
                .addToBackStack(FragmentoGraficaMoneda.TAG_NAME)
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
