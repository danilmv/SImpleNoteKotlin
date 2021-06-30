package com.andriod.simplenote.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andriod.simplenote.NoteApplication;
import com.andriod.simplenote.R;
import com.andriod.simplenote.adapter.SearchResultsAdapter;
import com.andriod.simplenote.data.BaseDataManager;
import com.andriod.simplenote.entity.Note;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchResultsDialogFragment extends BottomSheetDialogFragment {

    private static final String ARGUMENTS_SEARCH_QUERY = "search_query";
    private static final String TAG = "@@@SearchResults@";
    private SearchResultsAdapter adapter;
    private Map<String, Note> notes;
    private BaseDataManager dataManager;
    private String searchQuery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated() called");

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SearchResultsAdapter();
        adapter.setListener(note -> {
            if (getController() != null) {
                getController().showSearchNote(note);
            }
        });
        recyclerView.setAdapter(adapter);

        showList();
    }

    private void showList() {
        Log.d(TAG, "showList() called");
        if (getDataManager() != null) {
            notes = getDataManager().getData();
        }

        Log.d(TAG, String.format("showList(): dataManager=[%s]", dataManager));

        Bundle arguments = getArguments();
        if (arguments == null) return;
        searchQuery = arguments.getString(ARGUMENTS_SEARCH_QUERY);

        if (notes == null || searchQuery == null || searchQuery.isEmpty()) return;

        List<Note> list;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list = notes.values().stream()
                    .filter(note -> note.checkSearch(searchQuery))
                    .collect(Collectors.toList());
        } else {
            list = new ArrayList<>(notes.values());
        }

        Log.d(TAG, String.format("showList(): list.size=[%d]", list.size()));

        adapter.setData(list);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach() called");
        super.onAttach(context);
        if (!(context instanceof Controller)) {
            throw new IllegalStateException("Activity must implement Controller");
        }
    }

    private BaseDataManager getDataManager() {
        if (getActivity() == null) return null;
        if (dataManager == null) {
            NoteApplication application = (NoteApplication) requireActivity().getApplication();
            dataManager = application.getDataManager();
        }

        return dataManager;
    }

    public void setSearchQuery(String searchQuery) {
        Log.d(TAG, "setSearchQuery() called with: searchQuery = [" + searchQuery + "]");
        this.searchQuery = searchQuery;
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }

        bundle.clear();
        bundle.putString(ARGUMENTS_SEARCH_QUERY, searchQuery);

        setArguments(bundle);

        showList();
    }

    public interface Controller {
        void showSearchNote(Note note);
    }
    private Controller getController() {
        return (Controller) getActivity();
    }
}
