package abraham.alvarezcruz.openmarket.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import io.reactivex.subjects.PublishSubject;

public class FragmentoListaMonedas extends Fragment implements View.OnClickListener {

    public static String TAG_NAME = FragmentoListaMonedas.class.getSimpleName();

    private View view;
    private AppCompatActivity mainActivity;
    private RecyclerView recyclerView;
    private FloatingActionButton fabExchanges;

    private MonedasAdapter monedasAdapter;
    private MutableLiveData<ArrayList<Moneda>> listaMonedasMutable;
    private MonedasViewModel monedasViewModel;
    private PublishSubject<Moneda> monedaClickeadaSubject;
    private PublishSubject<View> fabExchangesClickeadoSubject;

    public FragmentoListaMonedas(){}

    public FragmentoListaMonedas(PublishSubject<Moneda> monedaClickeadaSubject, PublishSubject<View> fabExchangesClickeadoSubject){
        this.monedaClickeadaSubject = monedaClickeadaSubject;
        this.fabExchangesClickeadoSubject = fabExchangesClickeadoSubject;
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

        fabExchanges = view.findViewById(R.id.fabExchanges);
        fabExchanges.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recyclerListaMonedas);
    }

    private void initRecyclerView(){


        monedasAdapter = new MonedasAdapter(monedaClickeadaSubject);

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

        // Ligamos el "MonedasViewModel" a la activity, así podremos acceder a la información desde cualquier fragment
        monedasViewModel = ViewModelProviders.of(mainActivity).get(MonedasViewModel.class);
        listaMonedasMutable = monedasViewModel.getListadoMonedas();
        listaMonedasMutable.observe(this, new Observer<ArrayList<Moneda>>() {
            @Override
            public void onChanged(ArrayList<Moneda> monedas) {
                monedasAdapter.updateAll(monedas);
            }
        });

        // Si no tenemos valores cacheados, realizaremos una petición
        if (listaMonedasMutable.getValue().size() == 0){
            monedasViewModel.recargarListadoMonedas();
        }
    }

    public void setMonedaClickeadaSubject(PublishSubject<Moneda> monedaClickeadaSubject) {
        this.monedaClickeadaSubject = monedaClickeadaSubject;

        if (monedasAdapter != null){
            monedasAdapter.setOnMonedaClickeadaSubject(monedaClickeadaSubject);
        }
    }

    public void setFabExchangesClickeadoSubject(PublishSubject<View> fabExchangesClickeadoSubject) {
        this.fabExchangesClickeadoSubject = fabExchangesClickeadoSubject;
    }

    @Override
    public void onClick(View v) {

        if (fabExchangesClickeadoSubject != null){

            fabExchangesClickeadoSubject.onNext(v);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mainActivity = (AppCompatActivity) context;
    }
}
