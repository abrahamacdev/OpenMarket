package abraham.alvarezcruz.openmarket.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import abraham.alvarezcruz.openmarket.R;
import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.utils.Utils;
import abraham.alvarezcruz.openmarket.view.OnListadoMonedasListener;

public class ListadoMonedasAdapter extends RecyclerView.Adapter<ListadoMonedasAdapter.ListadoMonedasVH> {

    private static String TAG_NAME = ListadoMonedasAdapter.class.getSimpleName();

    private ArrayList<Moneda> listadoMonedas;
    private OnListadoMonedasListener onListadoMonedasListener;

    public ListadoMonedasAdapter(){
        this(new ArrayList<>(), null);
    }

    public ListadoMonedasAdapter(ArrayList<Moneda> listadoMonedas) {
        this(listadoMonedas, null);
    }

    public ListadoMonedasAdapter(ArrayList<Moneda> listadoMonedas, OnListadoMonedasListener onListadoMonedasListener) {
        this.listadoMonedas = listadoMonedas;
        this.onListadoMonedasListener = onListadoMonedasListener;
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
        holder.setOnListadoMonedasListener(onListadoMonedasListener);
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

        Log.e(TAG_NAME,"Vamos a actualizar todos los datos de la lista con " + listadoMonedas.size() + " nuevos");

        if (listadoMonedas != null && listadoMonedas.size() > 0){
            this.listadoMonedas.clear();
            this.listadoMonedas.addAll(listadoMonedas);
            notifyDataSetChanged();
        }
    }

    public void setOnListadoMonedasListener(OnListadoMonedasListener onListadoMonedasListener) {
        this.onListadoMonedasListener = onListadoMonedasListener;
    }

    public class ListadoMonedasVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View view;
        private AppCompatImageView imagenCriptoMoneda;
        private AppCompatTextView nombreCriptomoneda, precioCriptomoneda, porcentajeCambio1h, porcentajeCambio24h;
        private OnListadoMonedasListener onListadoMonedasListener;
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

            Log.e(TAG_NAME, "La imagen apunta a " + moneda.getUrlImagen());

            // Cargamos la imagen de la moneda
            Picasso.get().load(moneda.getUrlImagen()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    moneda.setImagen(bitmap);
                    imagenCriptoMoneda.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {}

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {}
            });

            // Evitamos la notación científica
            String precio = Utils.eliminarNotacionCientificaDouble(moneda.getPrecioActualUSD()) + "$";

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

        public void setOnListadoMonedasListener(OnListadoMonedasListener onListadoMonedasListener) {
            this.onListadoMonedasListener = onListadoMonedasListener;
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
            if (onListadoMonedasListener != null){
                onListadoMonedasListener.onMonedaClicked(moneda);
            }
        }

        private int numeroDeLineasOcupadas(TextView textView){

            String texto = String.valueOf(textView.getText());

            final Rect bounds = new Rect();
            final Paint paint = new Paint();
            paint.setTextSize(textView.getTextSize());
            paint.getTextBounds(texto, 0, texto.length(), bounds);

            return (int) Math.ceil((float) bounds.width() / textView.getTextSize());
        }
    }
}
