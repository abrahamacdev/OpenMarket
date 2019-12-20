package abraham.alvarezcruz.openmarket.model.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abraham.alvarezcruz.openmarket.model.pojo.Exchange;
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import io.reactivex.rxjava3.core.Maybe;

public interface IRepositorioRemoto {

    Maybe<ArrayList<Moneda>> obtenerDatosGeneralesTodasCriptomonedas(int pagina, int porPagina);

    Maybe<ArrayList<Double>> obtenerPreciosUlt8DiasDe(String idCriptomoneda);

    Maybe<ArrayList<Exchange>> obtenerDatosGeneralesTodosExchanges();

    Maybe<HashMap<String, String>> obtenerImagenDeLasExchanges(HashMap<String, String> idExchangesYSuImagen);
}
