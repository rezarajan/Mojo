package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import bluefirelabs.mojo.handlers.adapters.FirebaseRecyclerAdapterItems;

/**
 * Created by Reza Rajan on 2017-05-24.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "checkout.db";
    public static final String TABLE_NAME = "checkout_table";
    public static final String ID = "ID";       //column 1
    public static final String RESTAURANT = "MENU";       //column 2
    public static final String ITEM = "ITEM";       //column 3
    public static final String COST = "COST";       //column 4
    public static final String QUANTITY = "QUANTITY";       //column 5


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_String = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + RESTAURANT + " TEXT," + ITEM + " TEXT," + COST + " INTEGER," + QUANTITY + " INTEGER)";
        db.execSQL(SQL_String);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String restaurant, String item, String cost, String quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RESTAURANT, restaurant);
        contentValues.put(ITEM, item);
        contentValues.put(COST, cost);
        contentValues.put(QUANTITY, quantity);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
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

    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + ID + " FROM " + TABLE_NAME + " WHERE " + ITEM + " = '" + name + "'";

        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void updateName(String newName, int id, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + ITEM + " = '" + newName + "' WHERE " + ID + " = '" + id + "'" + " AND " + ITEM + " = '" + oldName + "'";

        Log.d("DatabaseHelper: ", "updateName: query: " + query);
        Log.d("DatabaseHelper: ", "updateName: Setting name to: " + newName);
        db.execSQL(query);
    }

    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = '" + id + "'" + " AND " + ITEM + " = '" + name + "'";

        Log.d("DatabaseHelper: ", "deleteName: query: " + query);
        Log.d("DatabaseHelper: ", "deleteName: Deleting: " + name + " from database");
        db.execSQL(query);
    }

    public void updateQuantity(String newQuantity, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        //String query = "UPDATE " + TABLE_NAME + " SET " + QUANTITY + " = '" + newQuantity + "' WHERE " + ID + " = '" + id + "'" + " AND " + QUANTITY + " = '" + oldQuantity + "'";
        String query = "UPDATE " + TABLE_NAME + " SET " + QUANTITY + " = '" + newQuantity + "' WHERE " + ID + " = '" + id + "'";

        Log.d("DatabaseHelper: ", "updateQuantity: query: " + query);
        Log.d("DatabaseHelper: ", "updateQuantity: Setting quantity to: " + newQuantity);
        db.execSQL(query);
    }

    public Cursor orderAlpha(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + RESTAURANT + " ASC", null);
        return res;
    }
}
