package abraham.alvarezcruz.openmarket.model.pojo;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Moneda {

    private String nombre, abreviatura, idNombreMoneda;
    private String urlImagen;
    private Bitmap imagen;
    private double precioActualUSD, precioActualBTC;
    private double cambio1h;
    private double cambio24h;
    private double cambio7d;
    private double marketCapTotal, volumenTotal;
    private ArrayList<Double> valoresUlt8D = new ArrayList<>();

    public Moneda(){}

    public Moneda(String idNombreMoneda, String nombre, String abreviatura, double precioActualUSD,
                  double cambio1h, double cambio24h, double cambio7d, double marketCapTotal, double volumenTotal, String urlImagen) {

        this.idNombreMoneda = idNombreMoneda;
        this.nombre = nombre;
        this.abreviatura = abreviatura;
        this.precioActualUSD = precioActualUSD;
        this.precioActualBTC = precioActualBTC;
        this.cambio1h = cambio1h;
        this.cambio24h = cambio24h;
        this.cambio7d = cambio7d;
        this.urlImagen = urlImagen;
        this.marketCapTotal = marketCapTotal;
        this.volumenTotal = volumenTotal;
    }

    @NonNull
    @Override
    public String toString() {
        return nombre + " (" + abreviatura + ") está a un valor de " + precioActualUSD + "$";
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public double getPrecioActualUSD() {
        return precioActualUSD;
    }

    public void setPrecioActualUSD(double precioActualUSD) {
        this.precioActualUSD = precioActualUSD;
    }

    public double getCambio1h() {
        return cambio1h;
    }

    public void setCambio1h(double cambio1h) {
        this.cambio1h = cambio1h;
    }

    public double getCambio24h() {
        return cambio24h;
    }

    public void setCambio24h(double cambio24h) {
        this.cambio24h = cambio24h;
    }

    public double getCambio7d() {
        return cambio7d;
    }

    public void setCambio7d(double cambio7d) {
        this.cambio7d = cambio7d;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public double getPrecioActualBTC() {
        return precioActualBTC;
    }

    public void setPrecioActualBTC(double precioActualBTC) {
        this.precioActualBTC = precioActualBTC;
    }

    public String getIdNombreMoneda() {
        return idNombreMoneda;
    }

    public void setIdNombreMoneda(String idNombreMoneda) {
        this.idNombreMoneda = idNombreMoneda;
    }

    public double getMarketCapTotal() {
        return marketCapTotal;
    }

    public void setMarketCapTotal(double marketCapTotal) {
        this.marketCapTotal = marketCapTotal;
    }

    public ArrayList<Double> getValoresUlt8D() {
        return valoresUlt8D;
    }

    public void setValoresUlt8D(ArrayList<Double> valoresUlt8D) {
        this.valoresUlt8D = valoresUlt8D;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public double getVolumenTotal() {
        return volumenTotal;
    }

    public void setVolumenTotal(double volumenTotal) {
        this.volumenTotal = volumenTotal;
    }
}
