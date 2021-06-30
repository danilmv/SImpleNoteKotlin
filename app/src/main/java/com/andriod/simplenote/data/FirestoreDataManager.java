package com.andriod.simplenote.data;

import android.util.Log;

import com.andriod.simplenote.entity.Note;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class FirestoreDataManager extends BaseDataManager {
    private static final String TAG = "@@@FirestoreDataManager";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String collection;

    public FirestoreDataManager() {
    }

    @Override
    public Map<String, Note> getData() {
        return notes;
    }

    @Override
    public void updateData(Note note) {
        String id = note.getId();

        if (id == null || id.isEmpty()) {
            db.collection(collection)
                    .add(note)
                    .addOnSuccessListener(documentReference -> {
                        note.setId(documentReference.getId());
                        Log.i(TAG, String.format("Note#%s was added", note.getId()));
                    });
        } else {
            db.collection(collection)
                    .document(id)
                    .set(note)
                    .addOnSuccessListener(unused -> Log.i(TAG, String.format("Note#%s was changed", note.getId())));
        }
    }

    @Override
    public void deleteData(Note note) {
        db.collection(collection)
                .document(note.getId())
                .delete()
                .addOnSuccessListener(unused -> Log.d(TAG, String.format("Note#%s was deleted", note.getId())));
    }

    @Override
    public void deleteAll() {
        for (Note note : notes.values()) {
            deleteData(note);
        }
    }

    @Override
    public void setUser(String user) {

        if (this.user == null || !this.user.equals(user)) {
            notes.clear();
        } else {
            return;
        }

        this.user = user;

        collection = String.format("notes/users/%s", user);

        db.collection(collection)
                .addSnapshotListener((value, error) -> {
                    if (value == null) return;
                    for (DocumentChange documentChange : value.getDocumentChanges()) {
                        Note note = documentChange.getDocument().toObject(Note.class);
                        Log.d(TAG, String.format("FirestoreDataManager.addSnapshot() called: header=[%s], type=[%s]",
                                note.getHeader(),
                                documentChange.getType().name()));
                        switch (documentChange.getType()) {
                            case ADDED:
                                notes.put(note.getId(), note);
                                break;
                            case REMOVED:
                                notes.remove(note.getId());
                                break;
                            case MODIFIED:
                                notes.remove(note.getId());
                                notes.put(note.getId(), note);
                            default:
                                break;
                        }
                    }
                    notifySubscribers();
                });
    }
}
