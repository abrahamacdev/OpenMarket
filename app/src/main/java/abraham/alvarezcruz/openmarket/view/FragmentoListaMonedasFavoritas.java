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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import abraham.alvarezcruz.openmarket.R;
import abraham.alvarezcruz.openmarket.adapter.MonedasAdapter;
import abraham.alvarezcruz.openmarket.model.livedata.MonedasViewModel;
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import io.reactivex.subjects.PublishSubject;

public class FragmentoListaMonedasFavoritas extends Fragment{

    public static String TAG_NAME = FragmentoListaMonedasFavoritas.class.getSimpleName();

    private View view;
    private AppCompatActivity mainActivity;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private MonedasAdapter monedasAdapter;
    private PublishSubject<Moneda> monedaClickeadaSubject;
    private MutableLiveData<ArrayList<Moneda>> listaMonedasFavoritasMutable;
    private MonedasViewModel monedasViewModel;

    public FragmentoListaMonedasFavoritas(){
        monedaClickeadaSubject = PublishSubject.create();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_listado_favoritos, container, false);

        // Inicializamos las vistas
        initViews();

        // Cargamos el reycler view
        initRecyclerView();

        // Obtenemos el listado de monedas de internet o de la caché (si ya las obtuvimos anteriormente)
        cargarListadoMonedas();

        return view;
    }

    private void initViews(){

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        mainActivity.setSupportActionBar(toolbar);

        recyclerView = view.findViewById(R.id.recyclerListaMonedas);
    }

    private void initRecyclerView(){

        monedasAdapter = new MonedasAdapter();

        monedasAdapter.setOnMonedaClickeadaSubject(monedaClickeadaSubject);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),0));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.clearOnScrollListeners();
        recyclerView.setAdapter(monedasAdapter);
    }

    private void cargarListadoMonedas(){

        // Ligamos el "MonedasViewModel" a la activity, así podremos acceder a la información desde cualquier fragment
        monedasViewModel = ViewModelProviders.of(mainActivity).get(MonedasViewModel.class);
        listaMonedasFavoritasMutable = monedasViewModel.getListadoMonedasFavoritas();
        listaMonedasFavoritasMutable.observe(this, new Observer<ArrayList<Moneda>>() {
            @Override
            public void onChanged(ArrayList<Moneda> monedas) {
                monedasAdapter.updateAll(monedas);
            }
        });

        // Si no tenemos valores cacheados, realizaremos una petición
        if (listaMonedasFavoritasMutable.getValue().size() == 0){
            monedasViewModel.recargarListadoMonedas();
        }
    }

    public PublishSubject<Moneda> getMonedaClickeadaSubject() {
        return monedaClickeadaSubject;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mainActivity = (AppCompatActivity) context;
    }
}
