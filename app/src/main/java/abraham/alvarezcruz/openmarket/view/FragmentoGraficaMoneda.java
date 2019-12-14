package abraham.alvarezcruz.openmarket.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import abraham.alvarezcruz.openmarket.R;
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;

public class FragmentoGraficaMoneda extends Fragment {

    public static String TAG_NAME = FragmentoGraficaMoneda.class.getSimpleName();

    private View view;
    private AppCompatImageView imagenCriptomoneda;
    private AppCompatTextView nombreCriptomoneda, precioActualUSD, precioActualBTC;
    private AppCompatTextView textoCambio1h, textoCambio24h, textoCambio7d;
    private Toolbar toolbar;
    private Moneda moneda;
    private AppCompatActivity parent;

    public FragmentoGraficaMoneda(Moneda moneda){
        this.moneda = moneda;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_detalles_con_grafica_moneda, container, false);

        initViews();

        return view;
    }

    private void initViews(){

        toolbar = view.findViewById(R.id.toolbar);
        parent.setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG_NAME, "Nos pirámos!!");
                // Manejamos nosotros mismos la salida
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        imagenCriptomoneda = view.findViewById(R.id.imagenMonedaGraficaMoneda);
        Picasso.get().load(moneda.getUrlImagen()).into(imagenCriptomoneda);

        nombreCriptomoneda = view.findViewById(R.id.textoNombreCriptomonedaGraficaMoneda);
        nombreCriptomoneda.setText(moneda.getNombre() + " (" + moneda.getAbreviatura() + ")");

        precioActualUSD = view.findViewById(R.id.precioActualUSDGraficaMoneda);
        precioActualUSD.setText(String.format("%.8f", moneda.getPrecioActualUSD()));

        precioActualBTC = view.findViewById(R.id.precioActualBTCGraficaMoneda);
        precioActualBTC.setText(String.format("%.8f", moneda.getPrecioActualBTC()));

        textoCambio1h = view.findViewById(R.id.cambio1hGraficaMoneda);
        textoCambio24h = view.findViewById(R.id.cambio24hGraficaMoneda);
        textoCambio7d = view.findViewById(R.id.cambio7dGraficaMoneda);

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

    @Override
    public void onAttach(@NonNull Context context) {
        parent = (AppCompatActivity) context;
        super.onAttach(context);
    }
}
