package abraham.alvarezcruz.openmarket.model.repository.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import abraham.alvarezcruz.openmarket.model.pojo.Moneda;
import abraham.alvarezcruz.openmarket.model.repository.dao.FavoritosDAO;

@Database(entities = {Moneda.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "openmarket.db";

    private static AppDatabase instance = null;
    public abstract FavoritosDAO favoritosDAO();

    public static AppDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context,
                    AppDatabase.class,
                    AppDatabase.DATABASE_NAME).build();
        }

        return instance;
    }
}
