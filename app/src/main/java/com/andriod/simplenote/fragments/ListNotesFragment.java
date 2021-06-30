package com.andriod.simplenote.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.andriod.simplenote.NoteApplication;
import com.andriod.simplenote.R;
import com.andriod.simplenote.adapter.ListNotesAdapter;
import com.andriod.simplenote.data.BaseDataManager;
import com.andriod.simplenote.entity.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListNotesFragment extends Fragment implements ListNotesAdapter.OnItemClickListener {

    private static final String TAG = "@@@ListNotesFragment@";
    private Map<String, Note> notes;
    private BaseDataManager dataManager;

    private boolean showOnlyFavorites;

    private ListNotesAdapter adapter;

    private final Runnable subscriber = () -> {
        Log.d(TAG, "subscription called");
        loadData();
        showList();
    };

    public ListNotesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        return inflater.inflate(R.layout.fragment_list_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, String.format("onViewCreated() called with: notes.size = [%d]", notes != null ? notes.size() : 0));

        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter = new ListNotesAdapter();
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.button_add_note).setOnClickListener(this::showPopupMenu);

        showList();
    }

    private void showPopupMenu(View v) {
        if (getController() != null) {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            Menu menu = popupMenu.getMenu();
            for (Note.NoteType value : Note.NoteType.values()) {
                MenuItem item = menu.add(value.name());
                item.setOnMenuItemClickListener(item1 -> {
                    getController().changeNote(new Note(value));
                    return true;
                });
            }
            popupMenu.show();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach() called");
        super.onAttach(context);
        if (!(context instanceof Controller)) {
            throw new IllegalStateException("Activity must implement Controller");
        }

        getDataManager().subscribe(subscriber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getDataManager().unSubscribe(subscriber);
    }

    private void loadData() {
        Log.d(TAG, "loadData() called");
        notes = getDataManager().getData();
    }

    private Controller getController() {
        return (Controller) getActivity();
    }

    @Override
    public void onItemClick(Note note) {
        if (getController() != null) {
            getController().changeNote(note);
        }
    }

    @Override
    public void onFavorite(Note note) {
        if (getController() != null) {
            getController().noteSaved(note);
        }
    }

    public interface Controller {
        void changeNote(Note note);
        void noteSaved(Note note);
    }

    private void showList() {
        Log.d(TAG, String.format("showList() called for note.size = [%d]", notes != null ? notes.size() : 0));
        if (adapter == null) return;

        if (notes == null) loadData();

        List<Note> list;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            list = notes.values().stream()
                    .filter(note -> !showOnlyFavorites || note.isFavorite())
                    .collect(Collectors.toList());
        } else {
            list = new ArrayList<>(notes.values());
        }
        adapter.setData(list);
    }

    public void setMode(boolean showOnlyFavorites) {
        this.showOnlyFavorites = showOnlyFavorites;
        showList();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Note note = adapter.getCurrentNote();
        if (note != null) {
            Log.d(TAG, String.format("note [%s] was deleted", note.getHeader()));

            getDataManager().deleteData(note);
        }
        return true;
    }

    private BaseDataManager getDataManager() {
        if (dataManager == null) {
            NoteApplication application = (NoteApplication) requireActivity().getApplication();
            dataManager = application.getDataManager();
        }

        return dataManager;
    }
}
