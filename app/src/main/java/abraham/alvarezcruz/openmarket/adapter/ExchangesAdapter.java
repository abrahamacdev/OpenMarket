package abraham.alvarezcruz.openmarket.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import abraham.alvarezcruz.openmarket.model.pojo.Exchange;


public class ExchangesAdapter extends RecyclerView.Adapter<ExchangesAdapter.ListadoExchangesVH> {

    public static String TAG_NAME = MonedasAdapter.class.getSimpleName();

    @NonNull
    @Override
    public ListadoExchangesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ListadoExchangesVH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public class ListadoExchangesVH extends RecyclerView.ViewHolder {

        public ListadoExchangesVH(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Exchange exchange){


        }
    }
}
