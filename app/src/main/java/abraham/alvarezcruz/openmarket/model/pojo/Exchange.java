package abraham.alvarezcruz.openmarket.model.pojo;

public class Exchange {

    private String nombre, id, urlImagen;
    private int ranking, paresTradeos;
    private double volumen;


    public Exchange(){}

    public Exchange(String nombre, String id, int ranking, int paresTradeos, double volumen) {
        this.nombre = nombre;
        this.id = id;
        this.ranking = ranking;
        this.paresTradeos = paresTradeos;
        this.volumen = volumen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getParesTradeos() {
        return paresTradeos;
    }

    public void setParesTradeos(int paresTradeos) {
        this.paresTradeos = paresTradeos;
    }

    public double getVolumen() {
        return volumen;
    }

    public void setVolumen(double volumen) {
        this.volumen = volumen;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }
}
