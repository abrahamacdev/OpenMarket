package abraham.alvarezcruz.openmarket.model.repository.local;

import android.provider.BaseColumns;

public class FavoritosContract {

    private FavoritosContract(){}

    public final static class FavoritosEntry implements BaseColumns {

        // Nombre de la tabla
        public final static String TABLE_NAME = "favoritos";

        // Nombre de las columnas
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMNA_ID_CRIPTOMONEDA = "idMoneda";
    }

}
