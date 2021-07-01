package com.andriod.simplenote.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andriod.simplenote.NoteApplication
import com.andriod.simplenote.R
import com.andriod.simplenote.adapter.SearchResultsAdapter
import com.andriod.simplenote.data.BaseDataManager
import com.andriod.simplenote.entity.Note
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class SearchResultsDialogFragment : BottomSheetDialogFragment() {
    private var adapter: SearchResultsAdapter? = null
    private var notes: Map<String, Note>? = null
    private var dataManager: BaseDataManager? = null
    private var searchQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = SearchResultsAdapter()

        adapter?.setListener { note ->
            controller?.showSearchNote(note)
        }
        recyclerView.adapter = adapter
        showList()
    }

    private fun showList() {
        Log.d(TAG, "showList() called")
        val dataManager = getDataManager()
        dataManager?.let {
            notes = it.data
        }
        Log.d(TAG, String.format("showList(): dataManager=[%s]", dataManager))
        val arguments = arguments ?: return
        searchQuery = arguments.getString(ARGUMENTS_SEARCH_QUERY)
        if (notes == null || searchQuery == null || searchQuery!!.isEmpty() || adapter == null) return

        notes?.let {
            val list = it.values.filter { note: Note -> note.checkSearch(searchQuery) }
                .toCollection(ArrayList<Note>())
            Log.d(TAG, String.format("showList(): list.size=[%d]", list.size))
            adapter?.setData(list)
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach() called")
        super.onAttach(context)
        check(context is Controller) { "Activity must implement Controller" }
    }

    private fun getDataManager(): BaseDataManager? {
        if (activity == null) return null
        if (dataManager == null) {
            val application = requireActivity().application as NoteApplication
            dataManager = application.dataManager
        }
        return dataManager
    }

    fun setSearchQuery(searchQuery: String) {
        Log.d(TAG, "setSearchQuery() called with: searchQuery = [$searchQuery]")
        this.searchQuery = searchQuery

        val bundle = arguments ?: Bundle()
        bundle.clear()
        bundle.putString(ARGUMENTS_SEARCH_QUERY, searchQuery)
        arguments = bundle

        showList()
    }

    fun interface Controller {
        fun showSearchNote(note: Note)
    }

    private val controller: Controller?
        get() = activity as Controller?

    companion object {
        private const val ARGUMENTS_SEARCH_QUERY = "search_query"
        private const val TAG = "@@@SearchResults@"
    }
}