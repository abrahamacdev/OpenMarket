package abraham.alvarezcruz.openmarket.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import abraham.alvarezcruz.openmarket.R;
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.utils.Utils;
import io.reactivex.subjects.PublishSubject;

public class MonedasAdapter extends RecyclerView.Adapter<MonedasAdapter.ListadoMonedasVH> {

    private static String TAG_NAME = MonedasAdapter.class.getSimpleName();

    private ArrayList<Moneda> listadoMonedas;
    private PublishSubject<Moneda> onMonedaClickeadaSubject;

    public MonedasAdapter(){
        this(new ArrayList<>(), null);
    }

    public MonedasAdapter(ArrayList<Moneda> listadoMonedas) {
        this(listadoMonedas, null);
    }

    public MonedasAdapter(ArrayList<Moneda> listadoMonedas, PublishSubject<Moneda> onMonedaClickeadaSubject) {
        this.listadoMonedas = listadoMonedas;
        this.onMonedaClickeadaSubject = onMonedaClickeadaSubject;
    }

    @NonNull
    @Override
    public ListadoMonedasVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detalle_moneda,parent,false);
        ListadoMonedasVH listadoMonedasVH = new ListadoMonedasVH(view);

        return listadoMonedasVH;
    }

    @Override
    public void onBindViewHolder(@NonNull ListadoMonedasVH holder, int position) {

        Moneda moneda = listadoMonedas.get(position);
        holder.setOnMonedaClickeadaSubject(onMonedaClickeadaSubject);
        holder.bind(listadoMonedas.get(position));
    }

    @Override
    public int getItemCount() {
        return listadoMonedas.size();
    }

    public void addAll(ArrayList<Moneda> listadoMonedas){

        if (listadoMonedas != null && listadoMonedas.size() > 0){
            listadoMonedas.addAll(listadoMonedas);
            notifyDataSetChanged();
        }
    }

    public void updateAll(ArrayList<Moneda> listadoMonedas){

        if (listadoMonedas != null){
            this.listadoMonedas.clear();
            this.listadoMonedas.addAll(listadoMonedas);
            notifyDataSetChanged();
        }
    }

    public void setOnMonedaClickeadaSubject(PublishSubject<Moneda> onMonedaClickeadaSubject) {
        this.onMonedaClickeadaSubject = onMonedaClickeadaSubject;
    }



    public class ListadoMonedasVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View view;
        private AppCompatImageView imagenCriptoMoneda;
        private AppCompatTextView nombreCriptomoneda, precioCriptomoneda, porcentajeCambio1h, porcentajeCambio24h;
        private PublishSubject<Moneda> onMonedaClickeadaSubject;
        private Moneda moneda;
        private Context context;

        public ListadoMonedasVH(@NonNull View itemView) {
            super(itemView);

            this.view = itemView;
            this.context = view.getContext();
            this.imagenCriptoMoneda = itemView.findViewById(R.id.imagenCriptomoneda);
            this.nombreCriptomoneda = itemView.findViewById(R.id.textoNombreCriptomoneda);
            this.precioCriptomoneda = itemView.findViewById(R.id.textoPrecioCriptomoneda);
            this.porcentajeCambio1h = itemView.findViewById(R.id.textoPorcentajeCambio1horaCriptomoneda);
            this.porcentajeCambio24h = itemView.findViewById(R.id.textoPorcentajeCambio24horaCriptomoneda);

            view.setOnClickListener(this);
        }

        public void bind(Moneda moneda){

            this.moneda = moneda;

            // Cargamos la imagen de la moneda
            Picasso.get()
                    .load(moneda.getUrlImagen())
                    .into(imagenCriptoMoneda);

            // Evitamos la notación científica
            String precio = Utils.eliminarNotacionCientificaString(moneda.getPrecioActualUSD()) + "$";

            // Cargamos los demás datos de la moneda
            nombreCriptomoneda.setText(moneda.getAbreviatura().toUpperCase());
            precioCriptomoneda.setText(precio);

            // Cambioo 24h
            double cambio24h = moneda.getCambio24h();
            if (Double.isNaN(cambio24h)){
                porcentajeCambio24h.setText("-");
            } else {
                porcentajeCambio24h.setText(String.valueOf(moneda.getCambio24h()) + "% (24" + context.getString(R.string.sigla_hora) + ")");
            }

            // Cambio 1h
            double cambio1h = moneda.getCambio1h();
            if (Double.isNaN(cambio1h)){
                porcentajeCambio1h.setText("-");
            } else {
                porcentajeCambio1h.setText(String.valueOf(moneda.getCambio1h()) + "% (1" + context.getString(R.string.sigla_hora) + ")");
            }

            // Cambiamos el color de los textViews según el porcentaje de cambio
            determinarColorPorcentajesCambio();

        }

        public void setOnMonedaClickeadaSubject(PublishSubject<Moneda> onMonedaClickeadaSubject) {
            this.onMonedaClickeadaSubject = onMonedaClickeadaSubject;
        }

        private void determinarColorPorcentajesCambio(){

            // No hay cambio en 1h -> Blanco
            if (Double.isNaN(moneda.getCambio1h())){
                porcentajeCambio1h.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            }else {
                // Positivo -> Verde | Negativo -> Rosa
                boolean cambio1hNegativo = moneda.getCambio1h() < 0.0;
                if (cambio1hNegativo){
                    porcentajeCambio1h.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                else {
                    porcentajeCambio1h.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
                }
            }

            // No hay cambio en 24h -> Blanco
            if (Double.isNaN(moneda.getCambio24h())){
                porcentajeCambio24h.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            }else {
                // Positivo -> Verde | Negativo -> Rosa
                boolean cambio24hNegativo = moneda.getCambio24h() < 0.0;
                if (cambio24hNegativo){
                    porcentajeCambio24h.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                else {
                    porcentajeCambio24h.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
                }
            }

        }

        @Override
        public void onClick(View v) {

            if (onMonedaClickeadaSubject != null){

                Log.e(TAG_NAME, "Transmitimos el click a: " + onMonedaClickeadaSubject);

                onMonedaClickeadaSubject.onNext(moneda);
            }
        }
    }
}
