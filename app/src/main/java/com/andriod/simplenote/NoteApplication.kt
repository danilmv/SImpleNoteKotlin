package com.andriod.simplenote;

import android.app.Application;
import android.content.Context;

import com.andriod.simplenote.data.BaseDataManager;
import com.andriod.simplenote.data.FirestoreDataManager;
import com.andriod.simplenote.data.PreferencesDataManager;

public class NoteApplication extends Application {

    private static final String SHARED_PREFERENCES_NOTES = "SHARED_PREFERENCES_NOTES";

    private BaseDataManager dataManager;

    @Override
    public void onCreate() {
        super.onCreate();

//        dataManager = new PreferencesDataManager(
//                getApplicationContext()
//                        .getSharedPreferences(SHARED_PREFERENCES_NOTES, Context.MODE_PRIVATE));
        dataManager = new FirestoreDataManager();
    }

    public BaseDataManager getDataManager() {
        return dataManager;
    }
}
