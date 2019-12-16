package abraham.alvarezcruz.openmarket.model.repository;

import java.util.ArrayList;

import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import io.reactivex.rxjava3.core.Maybe;

public interface RepositorioRemoto {

    public Maybe<ArrayList<Moneda>> obtenerDatosGeneralesTodasCriptomonedas(int pagina, int porPagina);
}
