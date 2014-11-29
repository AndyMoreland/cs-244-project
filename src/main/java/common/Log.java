package common;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by andrew on 11/27/14.
 */
public class Log<T> {
    List<Transaction<T>> logEntries = Lists.newArrayList();
    ReadWriteLock logLock = new ReentrantReadWriteLock();

    void addEntry(int index, Transaction<T> value) {
        Lock writeLock = logLock.writeLock();

        if (logEntries.size() <= index) {
            for (int i = logEntries.size(); i <= index; i++) {
                logEntries.add(null);
            }
        }

        logEntries.add(index, value);

        writeLock.unlock();
    }

    Transaction<T> getEntry(int index) {
        Lock readLock = logLock.readLock();
        Transaction<T> val = logEntries.get(index);
        readLock.unlock();

        return val;
    }

}
