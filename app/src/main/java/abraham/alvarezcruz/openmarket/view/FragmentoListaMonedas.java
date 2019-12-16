package abraham.alvarezcruz.openmarket.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import abraham.alvarezcruz.openmarket.R;
import abraham.alvarezcruz.openmarket.adapter.ListadoMonedasAdapter;
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.model.repository.RepositorioRemoto_Impl;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class FragmentoListaMonedas extends Fragment implements OnListadoMonedasListener {

    private View view;
    private AppCompatActivity mainActivity;
    private RecyclerView recyclerView;
    private ListadoMonedasAdapter listadoMonedasAdapter;
    private PublishSubject<Moneda> monedaClickeadaSubject;

    public FragmentoListaMonedas(){
        monedaClickeadaSubject = PublishSubject.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_listado_monedas, container, false);

        initViews();

        cargarListadoMonedas();


        return view;
    }

    private void initViews(){

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        mainActivity.setSupportActionBar(toolbar);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerListaMonedas);
        listadoMonedasAdapter = new ListadoMonedasAdapter();
        listadoMonedasAdapter.setOnListadoMonedasListener(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),0));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listadoMonedasAdapter);
    }

    private void cargarListadoMonedas(){

        // Cargamos el listado de monedas
        RepositorioRemoto_Impl repositorioRemoto_Impl = new RepositorioRemoto_Impl(getContext());
        repositorioRemoto_Impl.obtenerDatosGeneralesTodasCriptomonedas(1,250)
                .subscribeOn(Schedulers.newThread())
                .subscribe(listadoMonedas -> listadoMonedasAdapter.updateAll(listadoMonedas));
    }

    public PublishSubject<Moneda> getMonedaClickeadaSubject() {
        return monedaClickeadaSubject;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mainActivity = (AppCompatActivity) context;
    }

    @Override
    public void onMonedaClicked(Moneda moneda) {

        // Emitimos la moneda clickeada
        if (monedaClickeadaSubject != null && monedaClickeadaSubject.hasObservers()){
            monedaClickeadaSubject.onNext(moneda);
        }
    }
}
