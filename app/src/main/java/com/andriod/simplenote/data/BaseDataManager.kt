package com.andriod.simplenote.data

import com.andriod.simplenote.entity.Note
import java.util.*

abstract class BaseDataManager {
    private var subscribers: MutableSet<Runnable> = HashSet()
    protected var notes: MutableMap<String, Note> = HashMap()
    internal var user: String? = null

    //    abstract val data: Map<String, Note>
    val data: Map<String, Note>
        get() = notes

    abstract fun updateData(note: Note)
    abstract fun deleteData(note: Note)
    abstract fun deleteAll()

    protected fun notifySubscribers() {
        for (subscriber in subscribers) {
            subscriber.run()
        }
    }

    fun subscribe(subscriber: Runnable) {
        subscribers.add(subscriber)
    }

    fun unSubscribe(subscriber: Runnable) {
        subscribers.remove(subscriber)
    }

    open fun setUser(user: String?) {}
}