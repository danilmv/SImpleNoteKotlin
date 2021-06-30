package com.andriod.simplenote.entity

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.firebase.firestore.DocumentId
import java.util.*

class Note : Parcelable {
    @DocumentId
    var id: String = INIT_ID
    var header: String?
    var date: Long
    var isFavorite = false

    enum class NoteType(val value: Int) {
        Text(0), HTTP(1), Video(2);

    }

    var type: NoteType
    var content: String? = null

    @JvmOverloads
    constructor(type: NoteType = NoteType.Text, header: String? = null, date: Long = currentDate) {
        this.type = type
        this.header = header
        this.date = date
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readString() ?: INIT_ID
        header = `in`.readString()
        date = `in`.readLong()
        isFavorite = `in`.readByte().toInt() == 1
        content = `in`.readString()
        type = NoteType.valueOf(`in`.readString()!!)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(header)
        dest.writeLong(date)
        dest.writeByte((if (isFavorite) 1 else 0).toByte())
        dest.writeString(content)
        dest.writeString(type.name)
    }

    override fun describeContents(): Int {
        return 0
    }

    val shortContent: String?
        get() = if (content == null || content!!.isEmpty() || content!!.length <= SHORT_CONTENT_LENGTH) content else String.format(
            "%s...",
            content!!.substring(0, SHORT_CONTENT_LENGTH)
        )

    fun checkSearch(searchQuery: String?): Boolean {
        if (searchQuery == null || searchQuery.isEmpty()) return false
        return if (header != null && header!!.contains(searchQuery)) true else content != null && content!!.contains(
            searchQuery
        )
    }

    companion object {
        private const val SHORT_CONTENT_LENGTH = 30
        private const val TAG = "@@@Note@"
        @JvmField val CREATOR: Creator<Note> = object : Creator<Note> {
            override fun createFromParcel(`in`: Parcel): Note {
                return Note(`in`)
            }

            override fun newArray(size: Int): Array<Note?> {
                return arrayOfNulls(size)
            }
        }
        private val currentDate: Long
            get() = Calendar.getInstance().timeInMillis
        const val INIT_ID: String = "0000000000"
    }
}