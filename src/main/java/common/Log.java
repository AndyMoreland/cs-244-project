package common;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by andrew on 11/27/14.
 */
public class Log<T> {
    Map<Viewstamp, Transaction<T>> tentativeLogEntries = Maps.newHashMap();
    Map<Integer, Transaction<T>> committedLogEntries = Maps.newHashMap();
    ReadWriteLock logLock = new ReentrantReadWriteLock();

    void addEntry(Transaction<T> value) {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();

        tentativeLogEntries.put(value.getId(), value);

        writeLock.unlock();
    }

    Transaction<T> getEntry(int index) {
        Lock readLock = logLock.readLock();
        readLock.lock();
        Transaction<T> val = committedLogEntries.get(index);
        readLock.unlock();

        return val;
    }

    void commitEntry(Viewstamp id) {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();

        assert(tentativeLogEntries.containsKey(id));

        Transaction<T> entry = tentativeLogEntries.get(id);
        tentativeLogEntries.remove(id);
        committedLogEntries.put(entry.getId().getSequenceNumber(), entry);

        writeLock.unlock();

    }

}
