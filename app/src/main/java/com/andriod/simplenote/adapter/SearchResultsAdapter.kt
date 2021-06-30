package com.andriod.simplenote.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andriod.simplenote.R;
import com.andriod.simplenote.entity.Note;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private static final String TAG = "@@@SearchResultsAdapt@";
    private List<Note> notes = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() called with: position = [" + position + "]");
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, String.format("getItemCount() called with: notes.size = [%d]", notes == null ? 0 : notes.size()));
        return (notes == null ? 0 : notes.size());
    }

    public void setData(List<Note> notes) {
        Log.d(TAG, String.format("setData() called with: notes.size = [%d]", notes == null ? 0 : notes.size()));
        this.notes = notes;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewHeader = itemView.findViewById(R.id.text_view_header);
        private final TextView textViewContent = itemView.findViewById(R.id.text_view_content);
        private Note note;

        public ViewHolder(@NonNull ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_result, parent, false));
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(note);
                }
            });
        }

        public void bind(Note note) {
            this.note = note;
            textViewHeader.setText(note.getHeader());
            textViewContent.setText(note.getShortContent());
        }
    }
}
