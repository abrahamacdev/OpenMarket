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
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.model.livedata.MonedasViewModel;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class FragmentoListaMonedas extends Fragment implements View.OnClickListener {

    private View view;
    private AppCompatActivity mainActivity;
    private RecyclerView recyclerView;
    private transient FloatingActionButton fabExchanges;
    private Toolbar toolbar;

    private MonedasAdapter monedasAdapter;
    private PublishSubject<Moneda> monedaClickeadaSubject;
    private PublishSubject<View> fabAperturaExchangesSubject;
    private MutableLiveData<ArrayList<Moneda>> listaMonedasMutable;
    private MonedasViewModel monedasViewModel;

    public FragmentoListaMonedas(){
        monedaClickeadaSubject = PublishSubject.create();
        fabAperturaExchangesSubject = PublishSubject.create();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_listado_monedas, container, false);

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

        fabExchanges = view.findViewById(R.id.fabExchanges);
        fabExchanges.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recyclerListaMonedas);
    }

    private void initRecyclerView(){

        monedasAdapter = new MonedasAdapter();

        monedasAdapter.setOnMonedaClickeadaSubject(monedaClickeadaSubject);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),0));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                // Muestra el FAB cuando hagamos scroll entre los primeros 30% elementos
                /*int total = layoutManager.getItemCount();
                int primero = layoutManager.findFirstVisibleItemPosition();
                int tope = (int) (total * 0.3); */

                if (dy > 0 && fabExchanges.isShown()){
                    fabExchanges.hide();
                }
                //else if (dy < 0 && !fabExchanges.isShown() && primero <= tope){
                else if (dy < 0 && !fabExchanges.isShown()){
                    fabExchanges.show();
                }
            }
        });
        recyclerView.setAdapter(monedasAdapter);
    }

    private void cargarListadoMonedas(){

        // Cargamos el listado de monedas
        monedasViewModel = ViewModelProviders.of(this).get(MonedasViewModel.class);
        listaMonedasMutable = monedasViewModel.getListadoMonedas();
        listaMonedasMutable.observe(this, new Observer<ArrayList<Moneda>>() {
            @Override
            public void onChanged(ArrayList<Moneda> monedas) {
                monedasAdapter.updateAll(monedas);
            }
        });
    }

    public PublishSubject<Moneda> getMonedaClickeadaSubject() {
        return monedaClickeadaSubject;
    }

    public PublishSubject<View> getFabAperturaExchangesSubject() {
        return fabAperturaExchangesSubject;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mainActivity = (AppCompatActivity) context;
    }

    @Override
    public void onClick(View v) {

        if (fabAperturaExchangesSubject != null){
            fabAperturaExchangesSubject.onNext(v);
        }
    }
}
