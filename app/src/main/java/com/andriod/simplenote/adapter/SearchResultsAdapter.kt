package com.andriod.simplenote.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andriod.simplenote.R
import com.andriod.simplenote.entity.Note
import java.util.*

class SearchResultsAdapter : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {
    private var notes: List<Note> = ArrayList()
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(note: Note)
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder() called with: position = [$position]")
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int {
        Log.d(
            TAG,
            String.format(
                "getItemCount() called with: notes.size = [%d]",
                notes.size
            )
        )
        return notes.size
    }

    fun setData(notes: List<Note>) {
        Log.d(TAG, String.format("setData() called with: notes.size = [%d]", notes.size))
        this.notes = notes
        notifyDataSetChanged()
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
    ) {
        private val textViewHeader = itemView.findViewById<TextView>(R.id.text_view_header)
        private val textViewContent = itemView.findViewById<TextView>(R.id.text_view_content)
        private lateinit var note: Note
        fun bind(note: Note) {
            this.note = note
            textViewHeader.text = note.header
            textViewContent.text = note.shortContent
        }

        init {
            itemView.setOnClickListener {
                if (listener != null) {
                    listener!!.onItemClick(note)
                }
            }
        }
    }

    companion object {
        private const val TAG = "@@@SearchResultsAdapt@"
    }
}