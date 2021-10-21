package org.goldenglue.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class MessageStore {
    private final int capacity;
    private final List<Message> messages = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public MessageStore(int capacity) {
        this.capacity = capacity;
    }

    public void put(Message message) {
        writeLock.lock();
        try {
            if (messages.size() >= capacity) {
                messages.remove(0);
            }
            messages.add(message);
        } finally {
            writeLock.unlock();
        }
    }

    public void readStore(Consumer<Message> consumer) {
        readLock.lock();
        try {
            for (Message message : messages) {
                consumer.accept(message);
            }
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public String toString() {
        return "MessageStore{" +
                "capacity=" + capacity +
                ", messages=" + messages +
                '}';
    }
}
