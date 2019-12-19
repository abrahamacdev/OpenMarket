package abraham.alvarezcruz.openmarket.model.livedata;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.model.repository.RepositorioRemotoImpl;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MonedasViewModel extends AndroidViewModel {

    public static String TAG_NAME = MonedasViewModel.class.getSimpleName();

    private Application application;
    private RequestQueue requestQueue;
    private MutableLiveData<ArrayList<Moneda>> listadoMonedas;
    private RepositorioRemotoImpl repositorioRemoto_Impl;

    public MonedasViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.requestQueue = Volley.newRequestQueue(application.getApplicationContext());
        this.repositorioRemoto_Impl = new RepositorioRemotoImpl(application.getApplicationContext());
    }

    public MutableLiveData<ArrayList<Moneda>> getListadoMonedas() {
        if (listadoMonedas == null){
            listadoMonedas = new MutableLiveData<>();
            recargarListadoMonedas();
        }

        return listadoMonedas;
    }

    public void recargarListadoMonedas(){

        Log.e(TAG_NAME, "Vamos a realizar una nueva petición!!");

        Maybe<ArrayList<Moneda>> maybeListaMonedas = repositorioRemoto_Impl.obtenerDatosGeneralesTodasCriptomonedas(1,250);
        maybeListaMonedas
                .subscribeOn(Schedulers.computation())
                .subscribe(listaDeMonedas -> {
                    listadoMonedas.postValue(listaDeMonedas);
                });
    }
}