package abraham.alvarezcruz.openmarket.model.pojo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

import abraham.alvarezcruz.openmarket.model.repository.local.FavoritosContract;

@Entity(tableName = FavoritosContract.FavoritosEntry.TABLE_NAME, indices =
        {@Index(value = {FavoritosContract.FavoritosEntry._ID, FavoritosContract.FavoritosEntry.COLUMNA_ID_CRIPTOMONEDA},
        unique = true)})
public class Moneda {

    // En la práctica, no tendra ningún uso
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = FavoritosContract.FavoritosEntry._ID)
    private int id;

    @ColumnInfo(name = FavoritosContract.FavoritosEntry.COLUMNA_ID_CRIPTOMONEDA)
    @NonNull
    private String idNombreMoneda;

    @Ignore
    private String nombre, abreviatura;

    @Ignore
    private String urlImagen;

    @Ignore
    private double precioActualUSD, precioActualBTC;

    @Ignore
    private double cambio1h, cambio24h, cambio7d;

    @Ignore
    private double marketCapTotal, volumenTotal;

    @Ignore
    private ArrayList<Double> valoresUlt8D = new ArrayList<>();

    @Ignore
    private boolean favorita = false;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getVolumenTotal() {
        return volumenTotal;
    }

    public void setVolumenTotal(double volumenTotal) {
        this.volumenTotal = volumenTotal;
    }

    public boolean isFavorita() {
        return favorita;
    }

    public void setFavorita(boolean favorita) {
        this.favorita = favorita;
    }
}
