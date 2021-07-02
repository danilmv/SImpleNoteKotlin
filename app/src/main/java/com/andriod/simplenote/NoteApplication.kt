package com.andriod.simplenote

import android.app.Application
import android.content.Context
import com.andriod.simplenote.data.BaseDataManager
import com.andriod.simplenote.data.FirestoreDataManager
import com.andriod.simplenote.data.PreferencesDataManager

class NoteApplication : Application() {
    lateinit var dataManager: BaseDataManager
        private set

    override fun onCreate() {
        super.onCreate()

//        dataManager = PreferencesDataManager(
//            applicationContext
//                .getSharedPreferences(SHARED_PREFERENCES_NOTES, Context.MODE_PRIVATE));
        dataManager = FirestoreDataManager()
    }

    companion object {
        private const val SHARED_PREFERENCES_NOTES = "SHARED_PREFERENCES_NOTES"
    }
}