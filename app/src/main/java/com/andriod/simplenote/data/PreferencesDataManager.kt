package com.andriod.simplenote.data

import android.content.SharedPreferences
import com.andriod.simplenote.entity.Note
import com.andriod.simplenote.entity.Note.Companion.INIT_ID
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class PreferencesDataManager(preferences: SharedPreferences) : BaseDataManager() {
    private val gson = Gson()
    private lateinit var sharedPreferences: SharedPreferences

    fun setSharedPreferences(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
    }

    override val data: Map<String, Note>
        get() = notes

    override fun updateData(note: Note) {
        val id = note.id
        if (id == INIT_ID || id.isEmpty()) note.id = UUID.randomUUID().toString()
        notes[note.id] = note
        saveData()
    }

    override fun deleteData(note: Note) {
        notes.remove(note.id)
        saveData()
    }

    override fun deleteAll() {
        notes.clear()
        saveData()
    }

    private fun saveData() {
        sharedPreferences
            .edit()
            .putString(LIST_NOTES_KEY, gson.toJson(notes))
            .apply()
        notifySubscribers()
    }

    companion object {
        private const val LIST_NOTES_KEY = "LIST_NOTES_KEY"
    }

    init {
        setSharedPreferences(preferences)
        val stringData = sharedPreferences.getString(LIST_NOTES_KEY, null)
        if (stringData != null && !stringData.isEmpty()) {
            val setType = object : TypeToken<HashMap<String?, Note?>?>() {}.type
            notes.putAll(gson.fromJson(stringData, setType))
        }
    }
}