package chags.ph.sync.droid.service;

import android.content.Context;
import android.content.SharedPreferences;

import chags.ph.sync.droid.R;

public class SharedPrefManager {

    private static String token="";
    private static Context mCon;
    private static SharedPrefManager sharedPrefManager;

    private SharedPrefManager(Context con){
        mCon=con;
    }

    public static synchronized SharedPrefManager getSharedPrefManager(Context con)
    {
        if(sharedPrefManager==null)
        {
            sharedPrefManager = new SharedPrefManager(con);
        }
        return sharedPrefManager;
    }

    public static String getToken(String variable) {
        SharedPreferences sharedPreferences = mCon.getSharedPreferences(String.valueOf(R.string.shared_pref_name),Context.MODE_PRIVATE);

        return  sharedPreferences.getString(variable,null);
    }

    public static boolean setToken(String token,String variable) {
        SharedPreferences sharedPreferences = mCon.getSharedPreferences(String.valueOf(R.string.shared_pref_name),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(variable,token.toString());
        editor.apply();
        return true;
    }

}
