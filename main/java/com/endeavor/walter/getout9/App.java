package com.endeavor.walter.getout9;

import android.app.Application;
import android.content.Context;


public class App extends Application {
    private static Context context;
    private static com.endeavor.walter.getout9.DBHelper dbHelper;

//wvs hint:    https://stackoverflow.com/questions/1944656/android-global-variable
//    https://stackoverflow.com/questions/4391720/how-can-i-get-a-resource-content-from-a-static-context
//    private String phoneBrand, phoneModel;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this.getApplicationContext();
        dbHelper = new com.endeavor.walter.getout9.DBHelper();
        com.endeavor.walter.getout9.DatabaseManager.initializeInstance(dbHelper);


    }

    public static Context getContext(){
        return context;
    }

//    public String getPhoneBrand() { return phoneBrand;}
//    public void setPhoneBrand(String brand){this.phoneBrand = brand;}
//
//    public String getPhoneModel() { return phoneModel;}
//    public void setPhoneModel(String model){this.phoneModel = model;}

}