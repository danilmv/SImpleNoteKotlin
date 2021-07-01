package com.andriod.simplenote.adapter

import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.andriod.simplenote.R
import com.andriod.simplenote.adapter.ListNotesAdapter.BaseViewHolder
import com.andriod.simplenote.entity.Note
import com.andriod.simplenote.entity.Note.NoteType

class ListNotesAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private var notes: List<Note> = ArrayList()
    private var listener: OnItemClickListener? = null
    lateinit var currentNote: Note
        private set

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(note: Note)
        fun onFavorite(note: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIDEO_TYPE -> {
                VideoViewHolder(parent, listener)
            }
            HTTP_TYPE -> {
                HttpViewHolder(parent, listener)
            }
            else -> {
                TextViewHolder(parent, listener)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemViewType(position: Int): Int {
        return notes[position].type.value
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    abstract inner class BaseViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView), OnCreateContextMenuListener {
        protected lateinit var note: Note
        private val toggleFavorite: ToggleButton = itemView.findViewById(R.id.toggle_favorite)
        private val textViewHeader: TextView = itemView.findViewById(R.id.text_view_header)
        private val textViewContent: TextView = itemView.findViewById(R.id.text_view_content)
        private var isBinding = false

        fun bind(note: Note) {
            isBinding = true
            this.note = note
            toggleFavorite.isChecked = note.isFavorite
            textViewHeader.text = note.header
            textViewContent.text = note.shortContent
            isBinding = false
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
            menu.add(R.string.delete_menu_item)
            currentNote = note
        }

        init {
            itemView.setOnClickListener { listener?.onItemClick(note) }
            toggleFavorite.setOnCheckedChangeListener { _, isChecked: Boolean ->
                note.isFavorite = isChecked
                if (!isBinding) listener?.onFavorite(note)
            }
            itemView.setOnCreateContextMenuListener(this)
        }
    }

    internal inner class TextViewHolder(parent: ViewGroup, listener: OnItemClickListener?) :
        BaseViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_note_text, parent, false),
            listener
        ) {
        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo)
            menu.setHeaderTitle(
                String.format(
                    "%s: %s",
                    v.context.resources.getString(R.string.text_note),
                    currentNote.header
                )
            )
        }
    }

    internal inner class VideoViewHolder(parent: ViewGroup, listener: OnItemClickListener?) :
        BaseViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_note_video, parent, false),
            listener
        )

    internal inner class HttpViewHolder(parent: ViewGroup, listener: OnItemClickListener?) :
        BaseViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_note_http, parent, false),
            listener
        )

    fun setData(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    companion object {
        private val VIDEO_TYPE = NoteType.Video.value
        private val HTTP_TYPE = NoteType.HTTP.value
    }
}