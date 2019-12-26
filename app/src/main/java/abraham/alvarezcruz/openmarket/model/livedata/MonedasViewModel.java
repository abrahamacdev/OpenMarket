package abraham.alvarezcruz.openmarket.model.livedata;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.airbnb.lottie.L;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.model.repository.dao.FavoritosDAO;
import abraham.alvarezcruz.openmarket.model.repository.local.AppDatabase;
import abraham.alvarezcruz.openmarket.model.repository.remote.RepositorioRemotoImpl;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MonedasViewModel extends AndroidViewModel {

    public static String TAG_NAME = MonedasViewModel.class.getSimpleName();

    private Application application;
    private RequestQueue requestQueue;
    private RepositorioRemotoImpl repositorioRemoto_Impl;
    private AppDatabase appDatabase;

    private MutableLiveData<ArrayList<Moneda>> listadoMonedas;
    private MutableLiveData<ArrayList<String>> listadoIdsMonedasFavoritas;
    private MutableLiveData<ArrayList<Moneda>> listadoMonedasFavoritas;


    public MonedasViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.requestQueue = Volley.newRequestQueue(application.getApplicationContext());
        this.repositorioRemoto_Impl = new RepositorioRemotoImpl(application.getApplicationContext());
        this.appDatabase = AppDatabase.getInstance(application.getApplicationContext());

        init();
    }

    private void init(){
        listadoMonedas = new MutableLiveData<>();
        listadoMonedas.setValue(new ArrayList<>());

        listadoIdsMonedasFavoritas = new MutableLiveData<>();
        listadoIdsMonedasFavoritas.setValue(new ArrayList<>());

        listadoMonedasFavoritas = new MutableLiveData<>();
        listadoMonedasFavoritas.setValue(new ArrayList<>());
    }



    public MutableLiveData<ArrayList<Moneda>> getListadoMonedas() {
        return listadoMonedas;
    }

    public MutableLiveData<ArrayList<String>> getListadoIdsMonedasFavoritas(){
        return listadoIdsMonedasFavoritas;
    }

    public MutableLiveData<ArrayList<Moneda>> getListadoMonedasFavoritas(){
        return listadoMonedasFavoritas;
    }

    @SuppressLint("CheckResult")
    public void recargarListadoMonedas(){

        Log.e(TAG_NAME, "Vamos a realizar una nueva petición!!");

        /*
            Obtenemos la lista de monedas de internet y las seteamos a #listadoMonedas MEDIANTE "setValue()"
            De esta forma evitamos que a los "observadores" se les avise de la actualización de la lista
         */
        Maybe<ArrayList<Moneda>> maybeListaMonedas = repositorioRemoto_Impl.obtenerDatosGeneralesTodasCriptomonedas(1,250);
        maybeListaMonedas = maybeListaMonedas
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(listaDeMonedas -> {
                    Log.e(TAG_NAME, "Establecemos la nueva lista de monedas");

                    listadoMonedas.setValue(listaDeMonedas);
                    //listadoMonedas.postValue(listaDeMonedas);
                });


        /*
            De igual manera, obtenemos el listado de ids de aquellas monedas guardadas en favoritos MEDIANTE "setValue()"
            De esta forma evitamos que a los "observadores" se les avise de la actualización de la lista
         */
        Maybe<List<String>> maybeListaIdsMonedasFavoritas = obtenerIdsTodasMonedasFavs();
        maybeListaIdsMonedasFavoritas = maybeListaIdsMonedasFavoritas
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess((listaIdsMonedasFavs) -> {

                    Log.e(TAG_NAME, "Establecemos la nueva lista de monedas favoritas");

                    listadoIdsMonedasFavoritas.setValue(new ArrayList<>(listaIdsMonedasFavs));
                    //listadoIdsMonedasFavoritas.postValue(new ArrayList<>(idsTodasMonedasFavoritas));
                });

        /*
            Esto puede parecer algo complejo, pero la idea es muy simple.
            Mediante "merge()" conseguimos unificar a los dos "maybe" anteriores, así podremos ejecutar una
            cierta acción SÓLO cuando ambas tareas se hallan completado.
            En este caso, lo que haremos será:
                1. Coger el "listadoMonedas" (ArrayList<Moneda) y crear un "HashMap<String, Moneda>" en el
                que la clave sea el "idNombreMoneda", y como valor el propio objeto
                2. Iterar sobre el "listadoIdsMonedasFavoritas" para ver los ids de todas las monedas favoritas
                del usuario
                3. Buscar en el HashMap del punto 1 aquellas monedas que tengan el "idNombreMoneda" de la iteración actual
                4. Avisar de la actualización de los valores del "listadoMonedas"
         */
        Maybe.merge(maybeListaIdsMonedasFavoritas, maybeListaMonedas)
                .doAfterTerminate(() -> {

                    Log.e(TAG_NAME, "Ya se terminó");

                    HashMap<String, Moneda> idMonedaConSuObjeto = Observable.just(listadoMonedas.getValue())
                            .flatMap(listaMonedas -> Observable.fromIterable(listaMonedas))
                            .reduce(new HashMap<String, Moneda>(), (map, moneda) -> {
                                map.put(moneda.getIdNombreMoneda(), moneda);
                                return map;
                            })
                            .blockingGet();

                    Log.e(TAG_NAME, String.valueOf(listadoIdsMonedasFavoritas.getValue()));

                    // Recorremos los ids que hay en la lista de monedas favoritas
                    for (String idMoneda : listadoIdsMonedasFavoritas.getValue()){

                        // Obtenemos la moneda del listado de monedas y la establecemos como favorita
                        if (idMonedaConSuObjeto.containsKey(idMoneda)){
                            idMonedaConSuObjeto.get(idMoneda).setFavorita(true);
                        }
                    }

                    // Ahora si avisamos de la actualización de la lista de monedas, que ya incluye aquellas
                    // que son favoritas
                    listadoMonedas.postValue(listadoMonedas.getValue());
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Maybe<Boolean> guardarMonedaFavorita(final Moneda moneda){
        return Maybe.create(new MaybeOnSubscribe<Boolean>() {
            @SuppressLint("CheckResult")
            @Override
            public void subscribe(MaybeEmitter<Boolean> emitter) throws Exception {

                // La moneda ya es favorita, no haremos nada
                if (moneda.isFavorita()){
                    Log.e(TAG_NAME, "La moneda ya es una favorita!!");
                    emitter.onSuccess(false);
                }

                // Guardamos la moneda como favorita en la base de datos
                else {

                    FavoritosDAO favoritosDAO = appDatabase.favoritosDAO();

                    long id = favoritosDAO.insertarNuevoFavorito(moneda);

                    if (id != -1){
                        moneda.setFavorita(true);
                        emitter.onSuccess(true);
                    }
                    else {
                        emitter.onSuccess(false);
                    }
                }
            }
        });
    }

    public Maybe<Boolean> eliminarMonedaFavorita(final Moneda moneda){
        return Maybe.create(new MaybeOnSubscribe<Boolean>() {
            @Override
            public void subscribe(MaybeEmitter<Boolean> emitter) throws Exception {

                FavoritosDAO favoritosDAO = appDatabase.favoritosDAO();
                boolean borrado = favoritosDAO.eliminarDeFavoritosA(moneda.getIdNombreMoneda()) > 0;

                if (borrado){
                    moneda.setFavorita(false);
                    eliminarMonedaDeLDConId(moneda.getIdNombreMoneda());
                    emitter.onSuccess(true);
                }

                else {
                    emitter.onSuccess(false);
                }
            }
        });
    }

    public void recargarListadoMonedasFavoritas(){

        Maybe<ArrayList<Moneda>> maybeListaMonedas = null;


        // Si hay monedas en el "listado principal" (todas las monedas), evitamos hacer más peticiones
        if (listadoMonedas.getValue().size() > 0){
            maybeListaMonedas = Maybe.just(listadoMonedas.getValue());
        }

        // No hay monedas, obtendremos nuevas
        else {
            /*
                Obtenemos la lista de monedas de internet y las seteamos a #listadoMonedas MEDIANTE "setValue()"
                De esta forma evitamos que a los "observadores" se les avise de la actualización de la lista
             */
                maybeListaMonedas = repositorioRemoto_Impl.obtenerDatosGeneralesTodasCriptomonedas(1,250);
                maybeListaMonedas = maybeListaMonedas
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess(listaDeMonedas -> {
                            //listadoMonedas.setValue(listaDeMonedas);
                            listadoMonedas.postValue(listaDeMonedas);
                        });
        }



        /*
            De igual manera, obtenemos el listado de ids de aquellas monedas guardadas en favoritos MEDIANTE "setValue()"
            De esta forma evitamos que a los "observadores" se les avise de la actualización de la lista
         */
        Maybe<List<String>> maybeListaIdsMonedasFavoritas = obtenerIdsTodasMonedasFavs();
        maybeListaIdsMonedasFavoritas = maybeListaIdsMonedasFavoritas
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess((listaIdsMonedasFavs) -> {
                    listadoIdsMonedasFavoritas.setValue(new ArrayList<>(listaIdsMonedasFavs));
                });

        /*
            Esto puede parecer algo complejo, pero la idea es muy simple.
            Mediante "merge()" conseguimos unificar a los dos "maybe" anteriores, así podremos ejecutar una
            cierta acción SÓLO cuando ambas tareas se hallan completado.
            En este caso, lo que haremos será:
                1. Coger el "listadoMonedas" (ArrayList<Moneda) y crear un "HashMap<String, Moneda>" en el
                que la clave sea el "idNombreMoneda", y como valor el propio objeto
                2. Iterar sobre el "listadoIdsMonedasFavoritas" para ver los ids de todas las monedas favoritas
                del usuario
                3. Buscar en el HashMap del punto 1 aquellas monedas que tengan el "idNombreMoneda" de la iteración actual
                4. Avisar de la actualización de los valores del "listadoMonedasFavoritas"
         */
        Maybe.merge(maybeListaIdsMonedasFavoritas, maybeListaMonedas)
                .doAfterTerminate(() -> {

                    HashMap<String, Moneda> idMonedaConSuObjeto = Observable.just(listadoMonedas.getValue())
                            .flatMap(listaMonedas -> Observable.fromIterable(listaMonedas))
                            .reduce(new HashMap<String, Moneda>(), (map, moneda) -> {
                                map.put(moneda.getIdNombreMoneda(), moneda);
                                return map;
                            })
                            .blockingGet();

                    ArrayList<Moneda> monedasFavoritas = new ArrayList<>();

                    // Recorremos los ids que hay en la lista de monedas favoritas
                    for (String idMoneda : listadoIdsMonedasFavoritas.getValue()){

                        // Obtenemos la moneda del listado de monedas y la establecemos como favorita
                        if (idMonedaConSuObjeto.containsKey(idMoneda)){

                            Moneda moneda = idMonedaConSuObjeto.get(idMoneda);
                            moneda.setFavorita(true);
                            monedasFavoritas.add(moneda);
                        }
                    }

                    // Ahora si avisamos de la actualización de la lista de monedas, que ya incluye aquellas
                    // que son favoritas
                    listadoMonedasFavoritas.postValue(monedasFavoritas);
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }



    private Maybe<List<String>> obtenerIdsTodasMonedasFavs(){
        return Maybe.create(new MaybeOnSubscribe<List<String>>() {
            @Override
            public void subscribe(MaybeEmitter<List<String>> emitter) throws Exception {
                List<String> idsTodasMonedasFavoritas = appDatabase.favoritosDAO().obtenerIdsTodasMonedasFavoritas();
                emitter.onSuccess(idsTodasMonedasFavoritas);
            }
        });
    }

    private void eliminarMonedaDeLDConId(String idMoneda){

        ArrayList<Moneda> tempListadoMonedasFavoritas = listadoMonedasFavoritas.getValue();

        int indx = -1;

        for (int i = 0; i < tempListadoMonedasFavoritas.size(); i++) {
            if (tempListadoMonedasFavoritas.get(i).getIdNombreMoneda().equals(idMoneda)){
                indx = i;
                break;
            }
        }

        Log.e(TAG_NAME, "Vamos a eliminarlo de la lista. Pos: " + indx);

        if (indx > -1){
            tempListadoMonedasFavoritas.remove(indx);
            listadoMonedasFavoritas.postValue(tempListadoMonedasFavoritas);
        }
    }
}