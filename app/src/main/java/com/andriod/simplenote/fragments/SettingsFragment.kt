package com.andriod.simplenote.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.andriod.simplenote.R;


public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userName = null;

        if (getController() != null) {
            userName = getController().getUserName();
        }

        Button buttonDelete = view.findViewById(R.id.button_delete_all);
        buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.delete_dialog_header)
                    .setMessage(R.string.delete_dialog_message)
                    .setPositiveButton(R.string.Yes, (dialog, which) -> {
                        if (getController() != null) {
                            getController().deleteAll();
                        }
                    })
                    .setNegativeButton(R.string.Cancel, null)
                    .show();
        });
        buttonDelete.setEnabled(userName != null && !userName.isEmpty());

        Button buttonSignIn = view.findViewById(R.id.button_sign_in);
        buttonSignIn.setOnClickListener(v -> {
            if (getController() != null) {
                getController().signIn();
            }
        });
        buttonSignIn.setEnabled(userName == null || userName.isEmpty());

        Button buttonSignOut = view.findViewById(R.id.button_sign_out);
        buttonSignOut.setOnClickListener(v -> {
            if (getController() != null) {
                getController().signOut();
            }
        });
        buttonSignOut.setEnabled(userName != null && !userName.isEmpty());

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (!(context instanceof ListNotesFragment.Controller)) {
            throw new IllegalStateException("Activity must implement Controller");
        }
    }

    private SettingsFragment.Controller getController() {
        return (SettingsFragment.Controller) getActivity();
    }

    public interface Controller {
        void deleteAll();

        void signIn();

        void signOut();

        String getUserName();
    }
}
