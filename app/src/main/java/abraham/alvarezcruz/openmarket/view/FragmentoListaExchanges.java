package abraham.alvarezcruz.openmarket.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;

import abraham.alvarezcruz.openmarket.R;
import abraham.alvarezcruz.openmarket.adapter.ExchangesAdapter;
import abraham.alvarezcruz.openmarket.model.livedata.ExchangesViewModel;
import abraham.alvarezcruz.openmarket.model.pojo.Exchange;
import abraham.alvarezcruz.openmarket.utils.Utils;

public class FragmentoListaExchanges extends Fragment {

    public static String TAG_NAME = FragmentoListaExchanges.class.getSimpleName();

    private View view;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private AppCompatActivity parent;

    private ExchangesAdapter exchangesAdapter;
    private ExchangesViewModel exchangesViewModel;
    private MutableLiveData<ArrayList<Exchange>> listaExchangesMutable;
    private String modo;
    private FragmentListener fragmentListener;

    public FragmentoListaExchanges(){
        this(Utils.getModo());
    }

    public FragmentoListaExchanges(String modo){
        this(modo, null);
    }

    public FragmentoListaExchanges(String modo, FragmentListener fragmentListener){
        this.modo = modo;
        this.fragmentListener = fragmentListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_listado_exchanges, container, false);

        // Inicializamos las vistas
        initViews();

        // Cargamos el reycler view
        initRecyclerView();

        // Obtenemos el listado de exchanges de internet o de la caché (si ya las obtuvimos anteriormente)
        cargarListadoExchanges();

        return view;
    }

    private void initViews(){

        recyclerView = view.findViewById(R.id.recyclerListaExchanges);

        toolbar = view.findViewById(R.id.toolbar);
        parent.setSupportActionBar(toolbar);
        toolbar.setTitle("");

        Fragment f = this;

        // Dependiendo del layout que se muestre añadiremos el botón de navegación o no
        if (modo != null && !modo.equals(getString(R.string.xlarge_port_tag))){
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragmentListener != null){
                        fragmentListener.onFragmentClosed(f);
                    }

                    else {
                        // Manejamos nosotros mismos la salida
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            });
        }
    }

    private void initRecyclerView(){

        exchangesAdapter = new ExchangesAdapter(R.layout.detalle_exchange);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),0));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(exchangesAdapter);
    }

    private void cargarListadoExchanges(){

        // Cargamos el listado de monedas
        // TODO El ViewModel lo ligamos a la actividad principal para así evitar realizar múltiples peticiones
        exchangesViewModel = ViewModelProviders.of(getActivity()).get(ExchangesViewModel.class);
        listaExchangesMutable = exchangesViewModel.getListadoExchanges();
        listaExchangesMutable.observe(this, new Observer<ArrayList<Exchange>>() {
            @Override
            public void onChanged(ArrayList<Exchange> exchanges) {
                exchangesAdapter.updateAll(exchanges);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        parent = (AppCompatActivity) context;

        super.onAttach(context);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}
