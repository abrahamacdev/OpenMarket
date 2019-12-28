package abraham.alvarezcruz.openmarket.utils;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import com.airbnb.lottie.L;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import abraham.alvarezcruz.openmarket.R;

public class Utils {

    public static String TAG_NAME = Utils.class.getSimpleName();
    private static DecimalFormat df = obtenerFormateadorDeNotacionCientifica(2,8);
    private static String modo = null;

    private static DecimalFormat obtenerFormateadorDeNotacionCientifica(int minimoDecimales, int maximoDecimales){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(minimoDecimales);
        df.setMaximumFractionDigits(maximoDecimales);
        return df;
    }

    public static long obtenerMediodiaHoyEnMillis(){

        LocalDateTime localDateTime = LocalDateTime.now().withHour(12).withMinute(0);
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static long obtenerMediodiaEnMillisDeHace(int meses){

        LocalDateTime localDateTime = LocalDateTime.now().withHour(12).withMinute(0).minusMonths(meses);
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public static boolean fechaEntreRango(LocalDate inicio, LocalDate fin, LocalDate aComprobar){
        return (aComprobar.isAfter(inicio) || aComprobar.equals(inicio)) && (aComprobar.isBefore(fin) || aComprobar.equals(fin));
    }

    public static double eliminarNotacionCientificaDouble(double numero){
        String numeroSinNotacionCientifica = df.format(numero).replaceAll(",",".");
        return Double.valueOf(numeroSinNotacionCientifica);
    }

    public static double eliminarNotacionCientificaDouble(double numero, int minDecimales, int maxDecimales){

        DecimalFormat decimalFormat = obtenerFormateadorDeNotacionCientifica(minDecimales, maxDecimales);
        String numeroSinNotacionCientifica = decimalFormat.format(numero).replaceAll(",",".");

        return Double.valueOf(numeroSinNotacionCientifica);
    }

    public static String eliminarNotacionCientificaString(double numero){
        return df.format(numero).replaceAll(",",".");
    }

    public static String eliminarNotacionCientificaString(double numero, int minDecimales, int maxDecimales){

        DecimalFormat decimalFormat = obtenerFormateadorDeNotacionCientifica(minDecimales, maxDecimales);
        return decimalFormat.format(numero).replaceAll(",",".");
    }

    public static LocalDate[] obtenerListaDeUltimos(int dias){

        LocalDate[] arrayDias = new LocalDate[dias];

        for (int i=0; i<dias; i++){

            LocalDate temp = LocalDate.now().minusDays(i);
            arrayDias[i] = temp;
        }

        return arrayDias;
    }

    public static String anadirSeparadorDeCientosANumero(double numero){

        String sinNotacionCientifica = eliminarNotacionCientificaString(numero,0,0);

        String numeroConPuntos = "";
        int cuentaHastaPunto = 3;
        for (int i=sinNotacionCientifica.length() - 1; i>=0; i--){

            if (cuentaHastaPunto == 0){

                numeroConPuntos += ".";
                cuentaHastaPunto = 3;
            }

            numeroConPuntos += sinNotacionCientifica.charAt(i);
            cuentaHastaPunto--;
        }

        return new StringBuilder(numeroConPuntos).reverse().toString();
    }

    public static float cargarTamanioLetra(int id, Context context) {
        float scaleRatio = context.getResources().getDisplayMetrics().density;
        float dimenPix = context.getResources().getDimension(id);

        return dimenPix / scaleRatio;
    }

    public static boolean esTablet(Context context){
        return modo.equals(context.getString(R.string.xlarge_port_tag)) || modo.equals(context.getString(R.string.xlarge_land_tag));
    }

    public static String getModo() {
        return modo;
    }

    public static void setModo(String modo) {
        Utils.modo = modo;
    }
}
