package com.andriod.simplenote.data;

import com.andriod.simplenote.entity.Note;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BaseDataManager {
    protected Set<Runnable> subscribers = new HashSet<>();
    protected final Map<String, Note> notes = new HashMap<>();
    protected String user;

    abstract public Map<String, Note> getData();

    abstract public void updateData(Note note);

    abstract public void deleteData(Note note);

    abstract public void deleteAll();

    protected void notifySubscribers() {
        for (Runnable subscriber : subscribers) {
            subscriber.run();
        }
    }

    public void subscribe(Runnable subscriber) {
        subscribers.add(subscriber);
    }

    public void unSubscribe(Runnable subscriber) {
        subscribers.remove(subscriber);
    }

    public void setUser(String user) {
    }
}
