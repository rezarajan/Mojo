package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Reza Rajan on 2017-05-24.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "checkout.db";
    public static final String TABLE_NAME = "checkout_table";
    public static final String ID = "ID";       //column 1
    public static final String RESTAURANT = "RESTAURANT";       //column 2
    public static final String ITEM = "ITEM";       //column 3
    public static final String COST = "COST";       //column 4


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_String = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + RESTAURANT + " TEXT," + ITEM + " TEXT," + COST + " INTEGER)";
        db.execSQL(SQL_String);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String restaurant, String item, String cost){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RESTAURANT, restaurant);
        contentValues.put(ITEM, item);
        contentValues.put(COST, cost);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1){
            return false;
        } else{
            return true;
        }
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    //The variable ITEM just has to be changed to the column you are interested in deleting items from
    public Integer deleteData(String TODELETE){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, ITEM + " = ?", new String[] {TODELETE});
    }
}
