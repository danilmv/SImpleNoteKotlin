package com.andriod.simplenote.data

import android.util.Log
import com.andriod.simplenote.entity.Note
import com.andriod.simplenote.entity.Note.Companion.INIT_ID
import com.google.firebase.firestore.*

class FirestoreDataManager : BaseDataManager() {
    private val db = FirebaseFirestore.getInstance()
    private var collection: String? = null

    override val data: Map<String, Note>
        get() = notes

    override fun updateData(note: Note) {
        val id = note.id
        if (id == INIT_ID || id.isEmpty()) {
            db.collection(collection!!)
                .add(note)
                .addOnSuccessListener { documentReference: DocumentReference ->
                    note.id = documentReference.id
                    Log.i(TAG, String.format("Note#%s was added", note.id))
                }
        } else {
            db.collection(collection!!)
                .document(id)
                .set(note)
                .addOnSuccessListener { unused: Void? ->
                    Log.i(
                        TAG,
                        String.format("Note#%s was changed", note.id)
                    )
                }
        }
    }

    override fun deleteData(note: Note) {
        db.collection(collection!!)
            .document(note.id)
            .delete()
            .addOnSuccessListener { unused: Void? ->
                Log.d(
                    TAG,
                    String.format("Note#%s was deleted", note.id)
                )
            }
    }

    override fun deleteAll() {
        for (note in notes.values) {
            deleteData(note)
        }
    }

    override fun setUser(user: String?) {
        if (this.user == null || this.user != user) {
            notes.clear()
        } else {
            return
        }
        this.user = user
        collection = String.format("notes/users/%s", user)
        db.collection(collection!!)
            .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (value == null) return@addSnapshotListener
                for (documentChange in value.documentChanges) {
                    val note = documentChange.document.toObject(
                        Note::class.java
                    )
                    Log.d(
                        TAG, String.format(
                            "FirestoreDataManager.addSnapshot() called: header=[%s], type=[%s]",
                            note.header,
                            documentChange.type.name
                        )
                    )
                    when (documentChange.type) {
                        DocumentChange.Type.ADDED -> notes[note.id] = note
                        DocumentChange.Type.REMOVED -> notes.remove(note.id)
                        DocumentChange.Type.MODIFIED -> {
                            notes.remove(note.id)
                            notes[note.id] = note
                        }
                        else -> {
                        }
                    }
                }
                notifySubscribers()
            }
    }

    companion object {
        private const val TAG = "@@@FirestoreDataManager"
    }
}