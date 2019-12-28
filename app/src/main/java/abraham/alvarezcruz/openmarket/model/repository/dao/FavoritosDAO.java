package abraham.alvarezcruz.openmarket.model.repository.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.model.repository.local.FavoritosContract;

@Dao
public interface FavoritosDAO {

    @Insert
    long insertarNuevoFavorito(Moneda moneda);

    @Query("SELECT " + FavoritosContract.FavoritosEntry.COLUMNA_ID_CRIPTOMONEDA + " FROM " + FavoritosContract.FavoritosEntry.TABLE_NAME)
    List<String> obtenerIdsTodasMonedasFavoritas();

    @Query("DELETE FROM " + FavoritosContract.FavoritosEntry.TABLE_NAME + " WHERE " + FavoritosContract.FavoritosEntry.COLUMNA_ID_CRIPTOMONEDA + "=:idMoneda")
    int eliminarDeFavoritosA(String idMoneda);
}
