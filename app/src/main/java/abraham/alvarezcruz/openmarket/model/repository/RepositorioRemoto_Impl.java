package abraham.alvarezcruz.openmarket.model.repository;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.utils.Constantes;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeEmitter;
import io.reactivex.rxjava3.core.MaybeOnSubscribe;
import io.reactivex.rxjava3.functions.Action;

public class RepositorioRemoto_Impl implements RepositorioRemoto {

    private static String TAG_NAME = RepositorioRemoto_Impl.class.getSimpleName();

    private Context context;

    public RepositorioRemoto_Impl(Context context){
        this.context = context;
    }

    @Override
    public Maybe<ArrayList<Moneda>> obtenerDatosTodasCriptomonedas() {

        return Maybe.create(new MaybeOnSubscribe<ArrayList<Moneda>>() {
            @Override
            public void subscribe(final MaybeEmitter<ArrayList<Moneda>> emitter) throws Throwable {

                // Lista con las monedas parseadas
                ArrayList<Moneda> listadoMonedas = new ArrayList<>();

                // Contador de monedas parseadas
                AtomicInteger completados = new AtomicInteger();

                // Obtenemos un listado con todos los ids de todas las criptomonedas disponibles
                Maybe<ArrayList<String>> maybeListadoIdsMonedas = obtenerIdsTodasCriptomonedas();
                maybeListadoIdsMonedas.subscribe(listadoIdMonedas -> {

                    // Por cada id de cada criptomoneda, obtendremos los datos de esta
                    Flowable.fromArray(listadoIdMonedas)
                            .flatMapIterable(listaIdMonedas -> listadoIdMonedas)    // "Aplastamos" la lista de ids
                            .map(idMoneda -> obtenerDatosCriptomoneda(idMoneda)) // Por cada "id" obtenemos un Maybe<Moneda>
                            .forEach(monedaMaybe -> {

                                monedaMaybe.subscribe(moneda -> {

                                    // completados++
                                    completados.getAndIncrement();

                                    // Añadimos la moneda a la lista
                                    synchronized (listadoMonedas) {
                                        listadoMonedas.add(moneda);
                                    }

                                    // Si ya se han procesado todos emitimos la lista de monedas final
                                    if (completados.get() == listadoIdMonedas.size()) {
                                        emitter.onSuccess(listadoMonedas);
                                    }

                                }, error -> {

                                    error.printStackTrace();

                                    // completados++;
                                    completados.getAndIncrement();

                                    // Si ya se han procesado todos emitimos la lista de monedas final
                                    if (completados.get() == listadoIdMonedas.size()) {
                                        emitter.onSuccess(listadoMonedas);
                                    }
                                });
                            });

                }, emitter::onError);
            }
        });
    }

    @Override
    public Maybe<Moneda> obtenerDatosCriptomoneda(String idCriptomoneda) {
        return Maybe.create(new MaybeOnSubscribe<Moneda>() {
            @Override
            public void subscribe(MaybeEmitter<Moneda> emitter) throws Throwable {

                String urlParams = "/" + idCriptomoneda + "?tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false";

                // Creamos la petición para obtener un json con todos los datos dee la criptomoneda
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                String url = Constantes.COINGECKO_BASE_URL + Constantes.COINGECKO_COINS_PATH + urlParams;

                Log.e(TAG_NAME, "Nueva petición: " + url);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.e(TAG_NAME, "Respuesta recibida de la petición: " + url);

                                // Parseamos las respuestas y emitimos los valores
                                ParseadorRespuestasHTTP parseadorRespuestasHTTP = ParseadorRespuestasHTTP.getInstance();
                                Maybe<Moneda> maybeMoneda = parseadorRespuestasHTTP.parsearTodosDatosCriptomoneda(response);

                                maybeMoneda.subscribe(moneda -> {
                                            emitter.onSuccess(moneda);
                                        },
                                        emitter::onError);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Propagamos el error
                        emitter.onError(error);
                    }
                });

                // Añadimos la petición a la cola
                requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public Maybe<ArrayList<String>> obtenerIdsTodasCriptomonedas() {
        return Maybe.create(new MaybeOnSubscribe<ArrayList<String>>() {
            @Override
            public void subscribe(final MaybeEmitter<ArrayList<String>> emitter) throws Throwable {

                // Creamos la petición para obtener un json con los ids de todas las criptomonedas
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                String url = Constantes.COINGECKO_BASE_URL + Constantes.COINGECKO_TODAS_MONEDAS_ENDPOINT;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                // Parseamos las respuestas
                                ParseadorRespuestasHTTP parseadorRespuestasHTTP = ParseadorRespuestasHTTP.getInstance();
                                Maybe<ArrayList<String>> maybeListadoIds = parseadorRespuestasHTTP.parsearIdsTodasCriptomonedas(response);

                                // Nos subscribimos al maybe anterior para recibir el listado de ids
                                maybeListadoIds.subscribe(emitter::onSuccess,
                                        emitter::onError);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                // Propagamos el error
                                emitter.onError(error);
                            }
                });

                // Añadimos la petición a la cola
                requestQueue.add(stringRequest);
            }
        });
    }
}
