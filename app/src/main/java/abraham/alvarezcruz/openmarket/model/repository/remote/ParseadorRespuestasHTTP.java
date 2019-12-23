package abraham.alvarezcruz.openmarket.model.repository.remote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.HashMap;

import abraham.alvarezcruz.openmarket.model.pojo.Exchange;
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.utils.Utils;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

public class ParseadorRespuestasHTTP {

    private static String TAG_NAME = ParseadorRespuestasHTTP.class.getSimpleName();
    private static ParseadorRespuestasHTTP instance = null;
    private ParseadorRespuestasHTTP(){}

    public static ParseadorRespuestasHTTP getInstance() {

        if (instance == null){
            instance = new ParseadorRespuestasHTTP();
        }
        return instance;
    }



    /**
     * Obtenemos todos los datos de una cierta criptomoneda del json (precio, nombre, urlImagen... etc)
     * @param json
     * @return Maybe<Moneda>
     */
    public Maybe<ArrayList<Moneda>> parsearDatosGeneralesDeTodasMonedas(final String json){

        return Maybe.create(new MaybeOnSubscribe<ArrayList<Moneda>>() {
            @Override
            public void subscribe(MaybeEmitter<ArrayList<Moneda>> emitter) {

                ArrayList<Moneda> monedas = new ArrayList<>();

                try {

                    // Elemento raíz
                    JSONArray raiz = new JSONArray(json);

                    for (int i=0; i<raiz.length(); i++){

                        JSONObject datosMoneda = raiz.getJSONObject(i);

                        // Nombree completo y abreviado
                        String idNombreMoneda = datosMoneda.getString("id");
                        String nombreCompleto = datosMoneda.getString("name");
                        String nombreAbreviado = datosMoneda.getString("symbol");

                        // URL de la imagen
                        String urlImagen = datosMoneda.getString("image");

                        // Precio actual en USD y en Bitcoins de la criptomoneda
                        double precioActualUSD = datosMoneda.getDouble("current_price");

                        // Market cap de la criptomoneda en usd
                        double marketCapUSD = datosMoneda.getDouble("market_cap");
                        marketCapUSD = Utils.eliminarNotacionCientificaDouble(marketCapUSD);

                        // Cambios en los precios de 1h, 24h y 7d
                        double porcenCambio7d = datosMoneda.optDouble("price_change_percentage_7d_in_currency", -1);
                        porcenCambio7d = Utils.eliminarNotacionCientificaDouble(porcenCambio7d,2,2);

                        double porcenCambio24h = datosMoneda.optDouble("price_change_percentage_24h_in_currency", -1);
                        porcenCambio24h = Utils.eliminarNotacionCientificaDouble(porcenCambio24h,2,2);

                        double porcenCambio1h = datosMoneda.optDouble("price_change_percentage_1h_in_currency", -1);
                        porcenCambio1h = Utils.eliminarNotacionCientificaDouble(porcenCambio1h,2,2);

                        double volumenTotal = datosMoneda.getDouble("total_volume");

                        // Creamos la moneda
                        Moneda moneda = new Moneda(idNombreMoneda, nombreCompleto, nombreAbreviado, precioActualUSD,
                                porcenCambio1h, porcenCambio24h, porcenCambio7d, marketCapUSD, volumenTotal, urlImagen);

                        // Añadimos la moneda a la lista
                        monedas.add(moneda);
                    }
                } catch (JSONException e) {
                    emitter.onError(e);
                }

                // La emitimos
                emitter.onSuccess(monedas);
            }
        });
    }

    /**
     * Obtenemos los precios de cotización de la moneda en los últimos x días
     *
     * @param json
     * @param dias
     * @return Maybe<Moneda>
     */
    public Maybe<ArrayList<Double>> parsearPreciosCotizacionDeUnaMoneda(final String json, final int dias){
        return Maybe.create(new MaybeOnSubscribe<ArrayList<Double>>() {
            @Override
            public void subscribe(MaybeEmitter<ArrayList<Double>> emitter){

                try {
                    JSONObject raiz = new JSONObject(json);
                    JSONArray preciosArray = raiz.getJSONArray("prices");

                    ArrayList<Double> preciosCotizacion = new ArrayList<>();

                    LocalDate hoy = LocalDate.now();
                    LocalDate hace7dias = LocalDate.now().minusDays(7);

                    // Comprobamos que halla suficientes días
                    if (preciosArray.length() >= dias){

                        int diasRestantes = dias;
                        for (int i=preciosArray.length() - 1; i>=0; i--){

                            JSONArray datosPrecio = preciosArray.getJSONArray(i);
                            long millis = datosPrecio.getLong(0);
                            double precio = datosPrecio.getDouble(1);
                            LocalDate fechaDelPrecio = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();

                            // Hemos recolectado los precios necesarios
                            if (diasRestantes == 0){
                                break;
                            }

                            // La fecha no cumple con los mínimos
                            if (!Utils.fechaEntreRango(hace7dias, hoy, fechaDelPrecio)){
                                preciosCotizacion.clear();
                                break;
                            }

                            // Añadimos cada precio a la lista
                            preciosCotizacion.add(precio);

                            diasRestantes--;
                        }
                    }

                    // Emitimos la lista con los precios de cotización de la moneda
                    emitter.onSuccess(preciosCotizacion);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Obtenemos los datos generales de todos los exchanges (nombre, ranking, volumen... etc)
     * @param json
     * @return
     */
    public Maybe<ArrayList<Exchange>> parsearDatosGeneralesDeTodosExchanges(final String json){

        return Maybe.create(new MaybeOnSubscribe<ArrayList<Exchange>>() {
            @Override
            public void subscribe(MaybeEmitter<ArrayList<Exchange>> emitter) {

                JSONObject raiz = null;
                ArrayList<Exchange> listadoExchanges = new ArrayList<>();
                try {
                    raiz = new JSONObject(json);

                    JSONArray array_exchanges = raiz.getJSONArray("data");

                    for (int i=0; i<array_exchanges.length(); i++){

                        JSONObject exchange = array_exchanges.getJSONObject(i);

                        String id = exchange.getString("exchangeId");
                        String nombre = exchange.getString("name");
                        int ranking = exchange.getInt("rank");
                        double volumen = exchange.optDouble("volumeUsd", 0.0);
                        int paresTradeos = exchange.getInt("tradingPairs");

                        Exchange nuevoExchange = new Exchange(nombre, id, ranking, paresTradeos, volumen);
                        listadoExchanges.add(nuevoExchange);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                emitter.onSuccess(listadoExchanges);
            }
        });
    }

    /**
     * Obtenemos las imagenes de los exchanges solicitados por parámetro
     * @param json
     * @return
     */
    public Maybe<HashMap<String, String>> parsearImagenDeTodosExchanges(final String json, HashMap<String, String> idsExchangesYSuImagen){
        return Maybe.create(new MaybeOnSubscribe<HashMap<String, String>>() {
            @Override
            public void subscribe(MaybeEmitter<HashMap<String, String>> emitter) {

                JSONArray raiz = null;
                try {
                    raiz = new JSONArray(json);

                    for (int i=0; i<raiz.length(); i++){

                        JSONObject exchange = raiz.getJSONObject(i);
                        String id = exchange.getString("id");

                        if (idsExchangesYSuImagen.containsKey(id)){
                            idsExchangesYSuImagen.put(id, exchange.getString("image"));
                        }
                    }

                    emitter.onSuccess(idsExchangesYSuImagen);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}