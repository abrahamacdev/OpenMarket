package abraham.alvarezcruz.openmarket.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import abraham.alvarezcruz.openmarket.R;
import abraham.alvarezcruz.openmarket.model.pojo.Exchange;
import abraham.alvarezcruz.openmarket.utils.Utils;


public class ExchangesAdapter extends RecyclerView.Adapter<ExchangesAdapter.ListadoExchangesVH> {

    public static String TAG_NAME = MonedasAdapter.class.getSimpleName();

    private int layoutDetalle;
    private ArrayList<Exchange> listaExchanges;


    public ExchangesAdapter(int layoutDetalle){
        this(new ArrayList<>(), layoutDetalle);
    }

    public ExchangesAdapter(ArrayList<Exchange> listaExchanges, int layoutDetalle){
        this.listaExchanges = listaExchanges;
        this.layoutDetalle = layoutDetalle;
    }

    @NonNull
    @Override
    public ListadoExchangesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutDetalle, parent, false);
        ListadoExchangesVH  listadoExchangesVH = new ListadoExchangesVH(view);
        return listadoExchangesVH;

    }

    @Override
    public void onBindViewHolder(@NonNull ListadoExchangesVH holder, int position) {

        Exchange exchange = listaExchanges.get(position);
        holder.bind(exchange);
    }

    @Override
    public int getItemCount() {
        return listaExchanges.size();
    }

    public void addAll(ArrayList<Exchange> listadoExchanges){

        if (listadoExchanges != null && listadoExchanges.size() > 0){
            listaExchanges.addAll(listadoExchanges);
            notifyDataSetChanged();
        }
    }

    public void updateAll(ArrayList<Exchange> listadoExchanges){

        if (listadoExchanges != null && listadoExchanges.size() > 0){
            listaExchanges.clear();
            listaExchanges.addAll(listadoExchanges);
            notifyDataSetChanged();
        }
    }

    public class ListadoExchangesVH extends RecyclerView.ViewHolder {

        private View view;
        private AppCompatTextView nombreExchange, rankingExchange, volumenExchange, paresTradeosExchange;
        private AppCompatImageView imagenExchange;
        private Context context;

        public ListadoExchangesVH(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            context = view.getContext();
            nombreExchange = view.findViewById(R.id.nombreExchange);
            volumenExchange = view.findViewById(R.id.volumenExchange);
            rankingExchange = view.findViewById(R.id.rankingExchange);
            paresTradeosExchange = view.findViewById(R.id.paresTradeosExchange);
            imagenExchange = view.findViewById(R.id.imagenExchange);
        }

        public void bind(Exchange exchange){

            nombreExchange.setText(exchange.getNombre());
            rankingExchange.setText(String.valueOf(exchange.getRanking()));
            paresTradeosExchange.setText(String.valueOf(exchange.getParesTradeos()));

            double volumenSinNotacionCientifica = Utils.eliminarNotacionCientificaDouble(exchange.getVolumen(),0,0);
            String volumenConSeparadorDeCientos = Utils.anadirSeparadorDeCientosANumero(volumenSinNotacionCientifica);
            volumenExchange.setText(volumenConSeparadorDeCientos + "$");

            if (!exchange.getUrlImagen().isEmpty()){
                Picasso.get()
                        .load(exchange.getUrlImagen())
                        .into(imagenExchange);
            }

            else {
                imagenExchange.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_error));
            }
        }
    }
}
