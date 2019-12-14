package abraham.alvarezcruz.openmarket.model.repository;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.utils.Constantes;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeEmitter;
import io.reactivex.rxjava3.core.MaybeOnSubscribe;

public class ParseadorRespuestasHTTP {

    private static String TAG_NAME = ParseadorRespuestasHTTP.class.getSimpleName();
    private static ParseadorRespuestasHTTP instance = null;

    private DecimalFormat df = new DecimalFormat("#.##");

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
    public Maybe<Moneda> parsearTodosDatosCriptomoneda(final String json){

        return Maybe.create(new MaybeOnSubscribe<Moneda>() {
            @Override
            public void subscribe(MaybeEmitter<Moneda> emitter) throws Throwable {
                try {

                    Log.e(TAG_NAME,"Vamos a parsear los datos de la moneda");

                    // Elemento raíz
                    JSONObject raiz = new JSONObject(json);

                    // Nombree completo y abreviado
                    String idNombreMoneda = raiz.getString("id");
                    String nombreCompleto = raiz.getString("name");
                    String nombreAbreviado = raiz.getString("symbol");

                    // URL de la imagen
                    JSONObject image = raiz.getJSONObject("image");
                    String urlImagen = image.getString("large");

                    // Precio actual en USD y en Bitcoins de la criptomoneda
                    JSONObject datos_mercado = raiz.getJSONObject("market_data");
                    JSONObject precios_actuales = datos_mercado.getJSONObject("current_price");
                    double precioActualUSD = precios_actuales.getDouble("usd");
                    double precioActualBTC = precios_actuales.getDouble("btc");

                    // Cambios en los precios de 1h, 24h y 7d
                    double tempPorcenCambio7d = datos_mercado.getDouble("price_change_percentage_7d");
                    String tempPorcenCambio7dString = df.format(tempPorcenCambio7d).replaceAll(",",".");
                    double porcenCambio7d = Double.valueOf(tempPorcenCambio7dString);
                    if (porcenCambio7d== 0.0){
                        porcenCambio7d = Double.NaN;
                    }

                    double tempPorcenCambio24h = datos_mercado.getDouble("price_change_percentage_24h");
                    String tempPorcenCambio24hString = df.format(tempPorcenCambio24h).replaceAll(",",".");
                    double porcenCambio24h = Double.valueOf(tempPorcenCambio24hString);
                    if (porcenCambio24h == 0.0){
                        porcenCambio24h = Double.NaN;
                    }

                    JSONObject porcenCambio1hObject = datos_mercado.getJSONObject("price_change_percentage_1h_in_currency");
                    Log.e(TAG_NAME, "(" + nombreCompleto + ") " + porcenCambio1hObject.toString());
                    double porcenCambio1h = Double.NaN;
                    if (porcenCambio1hObject.length() > 0){
                        // Obtención del porcentaje
                        double tempPorcenCambio1h = porcenCambio1hObject.getDouble("btc");
                        String tempPorcenCambio1hString = df.format(tempPorcenCambio1h).replaceAll(",",".");
                        tempPorcenCambio1h = Double.valueOf(tempPorcenCambio1hString);

                        if (tempPorcenCambio1h > 0.0){
                            porcenCambio1h = tempPorcenCambio1h;
                        }
                    }

                    // Creamos la moneda
                    Moneda moneda = new Moneda(idNombreMoneda, nombreCompleto, nombreAbreviado, precioActualUSD, precioActualBTC,
                            porcenCambio1h, porcenCambio24h, porcenCambio7d, urlImagen);

                    // La emitimos
                    emitter.onSuccess(moneda);

                } catch (JSONException e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * Extraemos los ids de todas las criptomonedas del json
     * @param json
     * @return Maybe<ArrayList<String>>
     */
    public Maybe<ArrayList<String>> parsearIdsTodasCriptomonedas(final String json){

        return Maybe.create(new MaybeOnSubscribe<ArrayList<String>>() {
            @Override
            public void subscribe(MaybeEmitter<ArrayList<String>> emitter) throws Throwable {

                try {
                    JSONArray raiz = new JSONArray(json);

                    ArrayList<String> listadoIds = new ArrayList<>();

                    // TODO Evitamos abusar de la API
                    int size = Constantes.NUM_MAX_CRIPTOMONEDAS == -1 ? raiz.length() : Constantes.NUM_MAX_CRIPTOMONEDAS;

                    // Recorremos cada conjunto de datos, extraemos el id de la moneda y lo añadimos
                    // al listado
                    for (int i=0; i<size; i++){

                        JSONObject conjunto = raiz.getJSONObject(i);
                        String id = conjunto.getString("id");
                        listadoIds.add(id);
                    }

                    Log.e(TAG_NAME,"Se han obtenido los ids de todas las criptomonedas, hay " + listadoIds.size()
                    + " en total");

                    // Emitimos el listado
                    emitter.onSuccess(listadoIds);
                }catch (JSONException e){
                    emitter.onError(e);
                }
            }
        });
    }


}
