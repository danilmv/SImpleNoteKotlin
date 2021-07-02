package com.andriod.simplenote.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.andriod.simplenote.NoteApplication
import com.andriod.simplenote.R
import com.andriod.simplenote.adapter.ListNotesAdapter
import com.andriod.simplenote.data.BaseDataManager
import com.andriod.simplenote.entity.Note
import com.andriod.simplenote.entity.Note.NoteType
import java.util.*

class ListNotesFragment : Fragment(), ListNotesAdapter.OnItemClickListener {
    private var notes: Map<String, Note>? = null

    private var dataManager: BaseDataManager? = null
        get() {
            if (field == null) {
                field = (requireActivity().application as NoteApplication).dataManager
            }
            return field
        }
    private var showOnlyFavorites = false
    private var adapter: ListNotesAdapter? = null
    private val subscriber = Runnable {
        Log.d(TAG, "subscription called")
        loadData()
        showList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d(TAG, "onCreateView() called")
        return inflater.inflate(R.layout.fragment_list_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(
            TAG,
            String.format(
                "onViewCreated() called with: notes.size = [%d]",
                if (notes != null) notes?.size else 0
            )
        )
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = ListNotesAdapter()
        adapter?.setOnItemClickListener(this)
        recyclerView.adapter = adapter
        view.findViewById<View>(R.id.button_add_note)
            .setOnClickListener { v: View -> showPopupMenu(v) }
        showList()
    }

    private fun showPopupMenu(v: View) {
        if (controller != null) {
            val popupMenu = PopupMenu(context, v)
            val menu = popupMenu.menu
            for (value in NoteType.values()) {
                menu.add(value.name)
                    .setOnMenuItemClickListener {
                        controller!!.changeNote(Note(value))
                        true
                    }
            }
            popupMenu.show()
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach() called")
        super.onAttach(context)
        check(context is Controller) { "Activity must implement Controller" }
        dataManager?.subscribe(subscriber)
    }

    override fun onDestroy() {
        super.onDestroy()
        dataManager?.unSubscribe(subscriber)
    }

    private fun loadData() {
        Log.d(TAG, "loadData() called")
        notes = dataManager?.data
    }

    private val controller: Controller?
        get() = activity as Controller?

    override fun onItemClick(note: Note) {
        controller?.changeNote(note)
    }

    override fun onFavorite(note: Note) {
        controller?.noteSaved(note)
    }

    interface Controller {
        fun changeNote(note: Note)
        fun noteSaved(note: Note)
    }

    private fun showList() {
        Log.d(
            TAG,
            String.format(
                "showList() called for note.size = [%d]",
                notes?.size ?: 0
            )
        )
        if (adapter == null) return
        if (notes == null) loadData()

        notes?.let {
            val list = it.values.filter { note: Note -> !showOnlyFavorites || note.isFavorite }
                .toCollection(ArrayList<Note>())
            adapter?.setData(list)
        }
    }

    fun setMode(showOnlyFavorites: Boolean) {
        this.showOnlyFavorites = showOnlyFavorites
        showList()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        adapter?.let {
            val note = it.currentNote
            Log.d(TAG, String.format("note [%s] was deleted", note.header))
            dataManager?.deleteData(note)
        }
        return true
    }

    companion object {
        private const val TAG = "@@@ListNotesFragment@"
    }
}