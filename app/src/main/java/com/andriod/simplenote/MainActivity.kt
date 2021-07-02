package com.andriod.simplenote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.andriod.simplenote.data.BaseDataManager
import com.andriod.simplenote.entity.Note
import com.andriod.simplenote.fragments.ListNotesFragment
import com.andriod.simplenote.fragments.NoteFragment
import com.andriod.simplenote.fragments.SearchResultsDialogFragment
import com.andriod.simplenote.fragments.SettingsFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), ListNotesFragment.Controller, NoteFragment.Controller,
    SettingsFragment.Controller, SearchResultsDialogFragment.Controller {
    private var hasSecondContainer = false
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var googleSignInClient: GoogleSignInClient
    override var userName: String? = null
        private set
    private var searchResultsDialogFragment: SearchResultsDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate() called with: savedInstanceState = [$savedInstanceState]")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hasSecondContainer = findViewById<View?>(R.id.second_fragment_container) != null
        bottomNavigationView = findViewById(R.id.bottom_view)
        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            Log.d(
                TAG,
                String.format("setOnNavigationItemSelectedListener() called: %s", item.title)
            )
            when (item.itemId) {
                R.id.menu_bottom_item_list -> {
                    showList(false)
                }
                R.id.menu_bottom_item_favorites -> {
                    showList(true)
                }
                R.id.menu_bottom_item_settings -> {
                    showSettings()
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
            true
        }
    }

    private fun showList(showOnlyFavorites: Boolean) {
        if (userName?.isEmpty() != false) {
            signIn()
            return
        }
        Log.d(TAG, "showList() called with: showOnlyFavorites = [$showOnlyFavorites]")
        val fragment =
            supportFragmentManager.findFragmentByTag(FRAGMENT_LIST_NOTES) as? ListNotesFragment
                ?: ListNotesFragment()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container, fragment, FRAGMENT_LIST_NOTES)
            .commit()
        fragment.setMode(showOnlyFavorites)
        setBottomView(if (showOnlyFavorites) R.id.menu_bottom_item_favorites else R.id.menu_bottom_item_list)
    }

    private fun showNote(note: Note?) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                if (hasSecondContainer) R.id.second_fragment_container else R.id.main_fragment_container,
                NoteFragment.newInstance(note), FRAGMENT_NOTE
            )
            .commit()
    }

    private fun showSettings() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                if (hasSecondContainer) R.id.second_fragment_container else R.id.main_fragment_container,
                SettingsFragment(), FRAGMENT_SETTINGS
            )
            .commit()
    }

    override fun changeNote(note: Note) {
        showNote(note)
    }

    override fun noteSaved(note: Note) {
        dataManager.updateData(note)
        showList(false)
    }

    private val dataManager: BaseDataManager
        get() = (application as NoteApplication).dataManager

    override fun deleteAll() {
        dataManager.deleteAll()
        showList(false)
    }

    private fun setBottomView(bottomItemId: Int) {
        Log.d(TAG, "setBottomView() called with: bottomItemId = [$bottomItemId]")
        bottomNavigationView.menu.findItem(bottomItemId)?.isChecked = true
    }

    override fun onResume() {
        Log.d(TAG, "onResume() called")
        super.onResume()
        setBottomView(R.id.menu_bottom_item_list)
    }

    override fun onStart() {
        Log.d(TAG, "onStart() called")
        super.onStart()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        userName = (gso.account ?: GoogleSignIn.getLastSignedInAccount(this)?.account)?.name
        dataManager.setUser(userName)
        Log.d(TAG, String.format("AUTHORIZED as [%s]", userName))
        showList(false)
    }

    override fun signIn() {
        startActivityForResult(googleSignInClient.signInIntent, CODE_SIGN_IN)
    }

    override fun signOut() {
        Log.d(TAG, String.format("signOut() called: [%s sign out]", userName))
        userName = null
        googleSignInClient.signOut().addOnCompleteListener { showSettings() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_SIGN_IN && resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                userName = task.getResult(ApiException::class.java)?.account?.name
                Log.d(TAG, String.format("AUTHORIZED as [%s]", userName))
            } catch (e: ApiException) {
                Log.d(TAG,
                    String.format("onActivityResult() called with error: [%s]", e.message))
            }
            if (userName?.isEmpty() == false) {
                dataManager.setUser(userName)
                showList(false)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchView = menu.findItem(R.id.menu_main_item_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                showQueryResults(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
        return true
    }

    private fun showQueryResults(newText: String?) {
        if (newText?.isEmpty() != false) return
        if (searchResultsDialogFragment == null) {
            searchResultsDialogFragment = SearchResultsDialogFragment()
        }
        searchResultsDialogFragment?.let {
            it.show(supportFragmentManager, FRAGMENT_SEARCH_RESULTS)
            it.setSearchQuery(newText)
        }
    }

    override fun showSearchNote(note: Note) {
        searchResultsDialogFragment?.dismiss()
        changeNote(note)
    }

    companion object {
        private const val FRAGMENT_LIST_NOTES = "FRAGMENT_LIST_NOTES"
        private const val FRAGMENT_NOTE = "FRAGMENT_NOTE"
        private const val FRAGMENT_SETTINGS = "FRAGMENT_SETTINGS"
        private const val FRAGMENT_SEARCH_RESULTS = "FRAGMENT_SEARCH_RESULTS"
        private const val TAG = "@@@MainActivity@"
        private const val CODE_SIGN_IN = 1111
    }
}