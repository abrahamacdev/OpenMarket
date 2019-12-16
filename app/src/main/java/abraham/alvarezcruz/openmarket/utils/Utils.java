package abraham.alvarezcruz.openmarket.utils;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    private static DecimalFormat df = new DecimalFormat("#.##");

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
        String tempPorcenCambio1hString = df.format(numero).replaceAll(",",".");
        return Double.valueOf(tempPorcenCambio1hString);
    }

    public static String eliminarNotacionCientificaString(double numero){
        return df.format(numero).replaceAll(",",".");
    }
}
