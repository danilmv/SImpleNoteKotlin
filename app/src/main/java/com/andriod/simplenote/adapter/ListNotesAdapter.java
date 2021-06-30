package com.andriod.simplenote.adapter;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andriod.simplenote.R;
import com.andriod.simplenote.entity.Note;

import java.util.List;

public class ListNotesAdapter extends RecyclerView.Adapter<ListNotesAdapter.BaseViewHolder> {
    private final static int VIDEO_TYPE = Note.NoteType.Video.getValue();
    private final static int HTTP_TYPE = Note.NoteType.HTTP.getValue();

    private List<Note> notes;
    private OnItemClickListener listener;

    private Note currentNote;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Note note);
        void onFavorite(Note note);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIDEO_TYPE) {
            return new VideoViewHolder(parent, listener);
        } else if (viewType == HTTP_TYPE) {
            return new HttpViewHolder(parent, listener);
        } else {
            return new TextViewHolder(parent, listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return notes.get(position).getType().getValue();
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public Note getCurrentNote() {
        return currentNote;
    }

    abstract class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        protected Note note;
        protected final ToggleButton toggleFavorite = itemView.findViewById(R.id.toggle_favorite);

        protected final TextView textViewHeader = itemView.findViewById(R.id.text_view_header);
        protected final TextView textViewContent = itemView.findViewById(R.id.text_view_content);
        private boolean isBinding;

        public BaseViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(note);
                }
            });

            toggleFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
                note.setFavorite(isChecked);
                if (listener != null && !isBinding) {
                    listener.onFavorite(note);
                }
            });

            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(Note note) {
            isBinding = true;
            this.note = note;
            toggleFavorite.setChecked(note.isFavorite());
            textViewHeader.setText(note.getHeader());
            textViewContent.setText(note.getShortContent());
            isBinding = false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(R.string.delete_menu_item);
            currentNote = note;
        }
    }

    class TextViewHolder extends BaseViewHolder {

        public TextViewHolder(@NonNull ViewGroup parent, OnItemClickListener listener) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_text, parent, false), listener);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            menu.setHeaderTitle(String.format("%s: %s",
                    v.getContext().getResources().getString(R.string.text_note),
                    currentNote.getHeader()));
        }
    }

    class VideoViewHolder extends BaseViewHolder {

        public VideoViewHolder(@NonNull ViewGroup parent, OnItemClickListener listener) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_video, parent, false), listener);
        }
    }

    class HttpViewHolder extends BaseViewHolder {

        public HttpViewHolder(@NonNull ViewGroup parent, OnItemClickListener listener) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_http, parent, false), listener);
        }
    }

    public void setData(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }
}