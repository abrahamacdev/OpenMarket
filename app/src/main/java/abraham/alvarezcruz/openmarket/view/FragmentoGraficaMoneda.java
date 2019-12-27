package abraham.alvarezcruz.openmarket.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.squareup.picasso.Picasso;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;

import abraham.alvarezcruz.openmarket.R;
import abraham.alvarezcruz.openmarket.model.livedata.MonedasViewModel;
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.model.repository.remote.RepositorioRemotoImpl;
import abraham.alvarezcruz.openmarket.utils.Utils;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.internal.Util;

public class FragmentoGraficaMoneda extends Fragment {

    public static String TAG_NAME = FragmentoGraficaMoneda.class.getSimpleName();

    private View view;
    private AppCompatImageView imagenCriptomoneda;
    private AppCompatTextView textoNombreCriptomoneda, textoPrecioActualUSD;
    private AppCompatTextView textoCambio1h, textoCambio24h, textoCambio7d;
    private LottieAnimationView animacion;
    private AppCompatTextView textoMarketCap, textoVolumen;
    private Toolbar toolbar;
    private Moneda moneda;
    private AppCompatActivity parent;
    private Context context;
    private LineChart grafica;
    private LinearLayout contenedorPrincipalGraficaMoneda;
    private Menu menu;

    private RepositorioRemotoImpl repositorioRemoto_Impl;
    private MonedasViewModel monedasViewModel;
    private String modo;


    public FragmentoGraficaMoneda(Moneda moneda){
        this(moneda, Utils.getModo());
        this.moneda = moneda;
    }

    public FragmentoGraficaMoneda(Moneda moneda, String modo){
        this.moneda = moneda;
        this.modo = modo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.e(TAG_NAME, "Modo -> " + modo);

        view = inflater.inflate(R.layout.activity_detalles_con_grafica_moneda, container, false);
        context = container.getContext();
        setHasOptionsMenu(true);

        // Utilizaremos este view model para guardar la moneda en la base de datos
        monedasViewModel = ViewModelProviders.of(parent).get(MonedasViewModel.class);

        // Inicializamos las vistas
        initViews();

        // Cargamos el reycler view
        cargarGrafica();

        return view;
    }

    private void initViews(){

        toolbar = view.findViewById(R.id.toolbar);
        parent.setSupportActionBar(toolbar);
        toolbar.setTitle("");

        // Dependiendo del layout que se muestre añadiremos el botón de navegación o no
        if (modo != null && !modo.equals(getString(R.string.xlarge_port_tag))){
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Manejamos nosotros mismos la salida
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

        grafica = view.findViewById(R.id.grafica);
        animacion = view.findViewById(R.id.animacion_bitcoin);
        animacion.setMaxFrame(120);

        contenedorPrincipalGraficaMoneda = view.findViewById(R.id.contenedorPrincipalGraficaMoneda);

        imagenCriptomoneda = view.findViewById(R.id.imagenMonedaGraficaMoneda);
        Picasso.get()
                .load(moneda.getUrlImagen())
                .into(imagenCriptomoneda);

        textoNombreCriptomoneda = view.findViewById(R.id.textoNombreCriptomonedaGraficaMoneda);
        textoNombreCriptomoneda.setText(moneda.getNombre() + " (" + moneda.getAbreviatura() + ")");

        textoPrecioActualUSD = view.findViewById(R.id.precioActualUSDGraficaMoneda);
        String precioActualUSD = Utils.eliminarNotacionCientificaString(moneda.getPrecioActualUSD());
        textoPrecioActualUSD.setText(precioActualUSD.replace(".", ","));

        textoCambio1h = view.findViewById(R.id.cambio1hGraficaMoneda);
        textoCambio24h = view.findViewById(R.id.cambio24hGraficaMoneda);
        textoCambio7d = view.findViewById(R.id.cambio7dGraficaMoneda);

        textoMarketCap = view.findViewById(R.id.marketCapGraficaMoneda);
        String marketCapConSeparadorDeCientos = Utils.anadirSeparadorDeCientosANumero(moneda.getMarketCapTotal());
        textoMarketCap.setText(marketCapConSeparadorDeCientos + "$");

        textoVolumen = view.findViewById(R.id.volumen24hGraficaMoneda);
        String volumenConSeparadorDeCientos = Utils.anadirSeparadorDeCientosANumero(moneda.getVolumenTotal());
        textoVolumen.setText(volumenConSeparadorDeCientos + "$");

        setearCambiosEnXPeriodo();
        colorSegunCambioXPeriodo();
    }

    private void setearCambiosEnXPeriodo(){

        // Cambio 1h
        double cambio1h = moneda.getCambio1h();
        if (Double.isNaN(cambio1h)){
            textoCambio1h.setText("-");
        } else {
            textoCambio1h.setText(String.valueOf(cambio1h) + "% (1" + getContext().getString(R.string.sigla_hora) + ")");
        }

        // Cambioo 24h
        double cambio24h = moneda.getCambio24h();
        if (Double.isNaN(cambio24h)){
            textoCambio24h.setText("-");
        } else {
            textoCambio24h.setText(String.valueOf(cambio24h) + "% (24" + getContext().getString(R.string.sigla_hora) + ")");
        }

        // Cambioo 7d
        double cambio7d = moneda.getCambio7d();
        if (Double.isNaN(cambio7d)){
            textoCambio7d.setText("-");
        } else {
            textoCambio7d.setText(String.valueOf(cambio7d) + "% (7" + getContext().getString(R.string.sigla_dia) + ")");
        }
    }

    private void colorSegunCambioXPeriodo(){

        // No hay cambio en 1h -> Blanco
        if (Double.isNaN(moneda.getCambio1h())){
            textoCambio1h.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        }else {
            // Positivo -> Verde | Negativo -> Rosa
            boolean cambio1hNegativo = moneda.getCambio1h() < 0.0;
            if (cambio1hNegativo){
                textoCambio1h.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            }
            else {
                textoCambio1h.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            }
        }

        // No hay cambio en 24h -> Blanco
        if (Double.isNaN(moneda.getCambio24h())){
            textoCambio24h.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        }else {
            // Positivo -> Verde | Negativo -> Rosa
            boolean cambio24hNegativo = moneda.getCambio24h() < 0.0;
            if (cambio24hNegativo){
                textoCambio24h.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            }
            else {
                textoCambio24h.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            }
        }

        // No hay cambio en 7d -> Blanco
        if (Double.isNaN(moneda.getCambio7d())){
            textoCambio7d.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        }else {
            // Positivo -> Verde | Negativo -> Rosa
            boolean cambio7dNegativo = moneda.getCambio7d() < 0.0;
            if (cambio7dNegativo){
                textoCambio7d.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            }
            else {
                textoCambio7d.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            }
        }
    }

    private void esconderAnimacion(){
        if (animacion.getVisibility() == View.VISIBLE){
            animacion.setVisibility(View.GONE);
            animacion.cancelAnimation();
            animacion.clearAnimation();
        }
    }

    @SuppressLint("CheckResult")
    private void cargarGrafica(){

        if (moneda.getValoresUlt8D().size() == 0){
            repositorioRemoto_Impl = new RepositorioRemotoImpl(getContext());
            repositorioRemoto_Impl.obtenerPreciosUlt8DiasDe(moneda.getIdNombreMoneda())
                    .subscribeOn(Schedulers.computation())
                    .subscribe(preciosMoneda -> {
                        moneda.setValoresUlt8D(preciosMoneda);
                        Log.e(TAG_NAME, "Hemos recibido " + moneda.getValoresUlt8D().size() + " valores");
                        anadirDatosAGrafica();
                    });
        }

        else {
            anadirDatosAGrafica();
        }
    }

    private void anadirDatosAGrafica(){

        grafica.setPinchZoom(false);
        grafica.setScaleEnabled(false);

        LocalDate[] ultimos8dias = Utils.obtenerListaDeUltimos(8);

        ArrayList<Double> valores = moneda.getValoresUlt8D();
        ArrayList<Entry> puntos = new ArrayList<>();

        // Creamos los puntos de la gráfica
        int j = 0;
        for (int i = valores.size() - 1; i>=0; i--){
            double precio = valores.get(i);

            puntos.add(new Entry(j, (float) precio));
            j++;
        }

        int colorBlanco = ContextCompat.getColor(context, android.R.color.white);
        int colorLineaPrecios = ContextCompat.getColor(context, R.color.lineaPreciosGrafica);

        float tamanioTextoGrafica = Utils.cargarTamanioLetra(R.dimen.tamanio_texto_precios_grafica_grafica_moneda, context);

        // Legenda del eje x
        XAxis xAxis = grafica.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(tamanioTextoGrafica);
        xAxis.setGranularity(1);
        xAxis.setTextColor(colorBlanco);
        xAxis.setValueFormatter(new ValueFormatter() {

            private boolean mostrado = false;

            @Override
            public String getAxisLabel(float value, AxisBase axis) {

                if (value == 0.0 || mostrado){
                    mostrado = false;
                    return "";
                }

                LocalDate localDate = ultimos8dias[ultimos8dias.length - 1 - (int) value];
                mostrado = true;
                return  localDate.getDayOfMonth() + "/" + localDate.getMonthValue();
            }
        });

        // Legenda del eje "y" derecho
        YAxis yAxisRight = grafica.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxisRight.setDrawGridLines(false);

        // Legenda del eje "y" izquierdo
        YAxis yAxisLeft = grafica.getAxisLeft();
        yAxisLeft.setEnabled(true);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setTextColor(colorBlanco);
        yAxisLeft.setTextSize(tamanioTextoGrafica);
        yAxisLeft.setAxisLineColor(ContextCompat.getColor(context, android.R.color.transparent));

        grafica.getLegend().setEnabled(false);
        grafica.getDescription().setText("");
        grafica.setViewPortOffsets(110,30,80,50);
        grafica.setHighlightPerTapEnabled(false);
        grafica.setHighlightPerDragEnabled(false);

        LineDataSet lineDataSet = new LineDataSet(puntos, null);
        lineDataSet.setDrawFilled(true);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.degradado_sombra_grafica_precio_moneda);
        lineDataSet.setFillDrawable(drawable);
        lineDataSet.setDrawValues(true);
        lineDataSet.setValueTextColor(colorBlanco);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setColor(colorLineaPrecios);
        lineDataSet.setCircleColor(colorLineaPrecios);
        lineDataSet.setValueTextSize(tamanioTextoGrafica);
        lineDataSet.setCircleHoleColor(colorLineaPrecios);

        final float primerValorDeLaGrafica = (float) ((double)valores.get(valores.size() - 1));
        lineDataSet.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {

                if (value == primerValorDeLaGrafica){
                    return "";
                }

                String valor = Utils.eliminarNotacionCientificaString(value,2,6);
                return valor.replace(".",",");
            }
        });

        // Añadimos los datos a la gráfica
        LineData data = new LineData(lineDataSet);
        grafica.setData(data);
        grafica.invalidate();
        // Escondemos la animación de carga y mostramos la gráfica
        esconderAnimacion();

        AlphaAnimation fadeIn = new AlphaAnimation(0f,1f);
        fadeIn.setDuration(1000);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                grafica.setVisibility(View.VISIBLE);
                grafica.animateY(1500, Easing.EaseInOutCirc);
            }

            @Override
            public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        grafica.setAnimation(fadeIn);

    }

    private void setearIconoFavoritos(int idDrawable){
        Drawable drawable = ContextCompat.getDrawable(context, idDrawable);
        menu.findItem(R.id.favorita).setIcon(drawable);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Guardamos la instancia del menú y lo inflamos
        this.menu = menu;
        inflater.inflate(R.menu.menu_detalle_moneda, menu);

        // Cambiamos el icono "favcoritos" dependiendo de si la moneda está guardada como favorita
        int idDrawable = moneda.isFavorita() ? R.drawable.ic_favorite_red_24dp : R.drawable.ic_favorite_border_red_24dp;
        setearIconoFavoritos(idDrawable);
    }

    @SuppressLint("CheckResult")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.favorita:

                // Agregamos|Borramos la moneda a|de la lista de favoritos
                if (!moneda.isFavorita()){
                    Maybe<Boolean> maybeGuardadoEnFavs = monedasViewModel.guardarMonedaFavorita(moneda);
                    maybeGuardadoEnFavs
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSuccess(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean resultado) throws Exception {

                                    if (resultado){
                                        // Cambiamos el icono de "favoritos"
                                        setearIconoFavoritos(R.drawable.ic_favorite_red_24dp);
                                    }
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .subscribe(
                            (guardada) -> {},
                            Throwable::printStackTrace);
                }

                else {
                    Maybe<Boolean> maybeEliminadoDeFavs = monedasViewModel.eliminarMonedaFavorita(moneda);
                    maybeEliminadoDeFavs
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSuccess(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean resultado) throws Exception {

                                    if (resultado){
                                        // Cambiamos el icono de "favoritos"
                                        setearIconoFavoritos(R.drawable.ic_favorite_border_red_24dp);
                                    }
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .subscribe(
                                    (guardada) -> {},
                                    Throwable::printStackTrace);
                }
                break;
        }

        return false;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        parent = (AppCompatActivity) context;
        super.onAttach(context);
    }
}
