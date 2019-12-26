package abraham.alvarezcruz.openmarket.view;

import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import io.reactivex.subjects.PublishSubject;

public interface ListadoMonedasListener {

    public PublishSubject<Moneda> getMonedaClickeadaSubject();

}
