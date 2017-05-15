package database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Reza Rajan on 2017-05-15.
 */

public class Restaurants_Helper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "restaurants";
    private static final String TABLE_NAME = "RESTAURANTS";
    private static final String UID = "_ID";
    private static final String NAME = "Name";
    private static final String CREATE_TABLE =  "CREATE TABLE "+TABLE_NAME+" ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+NAME+" VARCHAR(255));";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public Restaurants_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(CREATE_TABLE);
        } catch (SQLException e){

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL(DROP_TABLE);
            onCreate(db);
        } catch (SQLException e){

        }
    }
}
