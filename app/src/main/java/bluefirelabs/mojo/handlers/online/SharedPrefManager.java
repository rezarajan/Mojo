package bluefirelabs.mojo.handlers.online;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Reza Rajan on 2017-05-28.
 */

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "";
    private static final String KEY_ACCESS_TOKEN = "token";

    private static Context mcontext;
    private static SharedPrefManager mInstance;

    private SharedPrefManager(Context context){
        mcontext = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context){
        if (mInstance == null){
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public boolean storeToken(String token){
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
        return true;
    }

    public String getToken(){
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }
}
