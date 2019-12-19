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

import abraham.alvarezcruz.openmarket.model.pojo.Exchange;
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.utils.Constantes;
import abraham.alvarezcruz.openmarket.utils.Utils;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeEmitter;
import io.reactivex.rxjava3.core.MaybeOnSubscribe;

public class RepositorioRemotoImpl implements IRepositorioRemoto {

    private static String TAG_NAME = RepositorioRemotoImpl.class.getSimpleName();

    private Context context;
    private RequestQueue requestQueue;

    public RepositorioRemotoImpl(Context context){
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public Maybe<ArrayList<Moneda>> obtenerDatosGeneralesTodasCriptomonedas(int pagina, int porPagina) {

        return Maybe.create(new MaybeOnSubscribe<ArrayList<Moneda>>() {
            @Override
            public void subscribe(final MaybeEmitter<ArrayList<Moneda>> emitter) throws Throwable {

                // Lista con las monedas parseadas
                ArrayList<Moneda> listadoMonedas = new ArrayList<>();

                String params = "?vs_currency=usd&order=market_cap_desc&per_page=" + porPagina +
                        "&page=" + pagina + "&sparkline=false&price_change_percentage=1h%2C24h%2C7d";
                String url = Constantes.COINGECKO_BASE_URL + Constantes.COINGECKO_MARKETS_ENDPOINT;

                Log.e(TAG_NAME, url + params);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url + params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                ParseadorRespuestasHTTP parseadorRespuestasHTTP = ParseadorRespuestasHTTP.getInstance();
                                Maybe<ArrayList<Moneda>> maybeMonedas = parseadorRespuestasHTTP.parsearDatosGeneralesDeTodasMonedas(response);

                                maybeMonedas.subscribe(monedas -> {
                                    emitter.onSuccess(monedas);
                                }, error -> {
                                    emitter.onError(error);
                                });

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        emitter.onError(error);
                    }
                });

                // Add the request to the RequestQueue.
                requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public Maybe<ArrayList<Double>> obtenerPreciosUlt8DiasDe(String idCriptomoneda) {
        return Maybe.create(new MaybeOnSubscribe<ArrayList<Double>>() {
            @Override
            public void subscribe(MaybeEmitter<ArrayList<Double>> emitter) throws Throwable {

                // {@see https://www.coingecko.com/api/documentations/v3#/coins/get_coins__id__market_chart_range}
                long hace4meses = Utils.obtenerMediodiaEnMillisDeHace(4);
                long hoy = Utils.obtenerMediodiaHoyEnMillis();

                String params = "?vs_currency=usd&from=" + hace4meses + "&to=" + hoy;
                String url = Constantes.COINGECKO_BASE_URL + Constantes.COINGECKO_API_VERSION + Constantes.COINGECKO_COINS_PATH +
                        "/" + idCriptomoneda + Constantes.COINGECKO_MARKET_CHART_RANGE_ENDPOINT;

                Log.e(TAG_NAME, "Petición a: " + url + params);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url + params,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                ParseadorRespuestasHTTP parseadorRespuestasHTTP = ParseadorRespuestasHTTP.getInstance();
                                Maybe<ArrayList<Double>> maybePreciosCotizacion = parseadorRespuestasHTTP.parsearPreciosCotizacionDeUnaMoneda(response, 8);

                                maybePreciosCotizacion.subscribe(precios -> {
                                    emitter.onSuccess(precios);
                                }, error -> {
                                    emitter.onError(error);
                                });

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        emitter.onError(error);
                    }
                });

                // Add the request to the RequestQueue.
                requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public Maybe<ArrayList<Exchange>> obtenerDatosGeneralesTodosExchanges() {
        return Maybe.create(new MaybeOnSubscribe<ArrayList<Exchange>>() {
            @Override
            public void subscribe(MaybeEmitter<ArrayList<Exchange>> emitter) throws Throwable {

                String url = Constantes.COINCAP_BASE_URL + Constantes.COINCAP_API_VERSION + Constantes.COINCAP_EXCHANGES_ENDPOINT;

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                ParseadorRespuestasHTTP parseadorRespuestasHTTP = ParseadorRespuestasHTTP.getInstance();
                                Maybe<ArrayList<Moneda>> maybeMonedas = parseadorRespuestasHTTP.parsearDatosGeneralesDeTodasMonedas(response);

                                maybeMonedas.subscribe(monedas -> {
                                    emitter.onSuccess(monedas);
                                }, error -> {
                                    emitter.onError(error);
                                });

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        emitter.onError(error);
                    }
                });

                // Add the request to the RequestQueue.
                requestQueue.add(stringRequest);

            }
        });
    }
}
