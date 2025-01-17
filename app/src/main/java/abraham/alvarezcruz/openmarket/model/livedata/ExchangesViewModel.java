package abraham.alvarezcruz.openmarket.model.livedata;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import abraham.alvarezcruz.openmarket.model.pojo.Exchange;
import abraham.alvarezcruz.openmarket.model.repository.remote.RepositorioRemotoImpl;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

public class ExchangesViewModel extends AndroidViewModel {

    public static String TAG_NAME = ExchangesViewModel.class.getSimpleName();

    private Application application;
    private RequestQueue requestQueue;
    private MutableLiveData<ArrayList<Exchange>> listadoExchanges;
    private RepositorioRemotoImpl repositorioRemoto_Impl;


    public ExchangesViewModel(@NonNull Application application) {
        super(application);

        this.application = application;
        this.requestQueue = Volley.newRequestQueue(application.getApplicationContext());
        this.repositorioRemoto_Impl = new RepositorioRemotoImpl(application.getApplicationContext());
    }

    public MutableLiveData<ArrayList<Exchange>> getListadoExchanges() {
        if (listadoExchanges == null){
            listadoExchanges = new MutableLiveData<>();
            recargarListadoExchanges();
        }

        return listadoExchanges;
    }

    @SuppressLint("CheckResult")
    public void recargarListadoExchanges(){

        Maybe<ArrayList<Exchange>> maybeListaExchanges = repositorioRemoto_Impl.obtenerDatosGeneralesTodosExchanges();
        maybeListaExchanges
                .subscribeOn(Schedulers.computation())
                .subscribe(listaDeExchanges -> {
                    listadoExchanges.postValue(listaDeExchanges);
                });
    }

}
