package common;

import PBFT.CommitMessage;
import PBFT.PrepareMessage;
import PBFT.Viewstamp;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by andrew on 11/27/14.
 */
public class Log<T> {
    // {SequenceNumber => { ViewStamp => Transaction }}
    Map<Integer, Map<Viewstamp, Transaction<T>>> tentativeLogEntries = Maps.newHashMap();
    Map<Integer, Transaction<T>> committedLogEntries = Maps.newHashMap();
    Map<Viewstamp, Transaction<T>> transactions = Maps.newConcurrentMap();
    Map<Viewstamp, CommitMessage> commitMessages = Maps.newConcurrentMap();
    Map<Viewstamp, PrepareMessage> prepareMessages = Maps.newConcurrentMap();
    ReadWriteLock logLock = new ReentrantReadWriteLock();
    int lastCommited = -1;

    public void addEntry(Transaction<T> value) throws IllegalLogEntryException {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();

        int sequenceNumber = value.getViewstamp().getSequenceNumber();
        if (tentativeLogEntries.containsKey(sequenceNumber)) {
            Map<Viewstamp, Transaction<T>> sequenceNumberTransactions = tentativeLogEntries.get(value.getViewstamp());
            if (sequenceNumberTransactions.containsKey(value.getViewstamp()) && sequenceNumberTransactions.get(value.getViewstamp()) != value) {
                throw new IllegalLogEntryException();
            }
            tentativeLogEntries.get(sequenceNumber).put(value.getViewstamp(), value);
        } else {
            Map<Viewstamp, Transaction<T>> map = Maps.newHashMap();
            map.put(value.getViewstamp(), value);
            tentativeLogEntries.put(sequenceNumber, map);
        }

        transactions.put(value.getViewstamp(), value);

        writeLock.unlock();
    }

    public Transaction<T> getTransaction(Viewstamp viewstamp) {
        Lock readLock = logLock.readLock();
        readLock.lock();
        Transaction<T> t = transactions.get(viewstamp);
        readLock.unlock();

        return t;
    }

    public Collection<Transaction<T>> getTentativeEntries(int index) {
        Lock readLock = logLock.readLock();
        readLock.lock();
        Map<Viewstamp, Transaction<T>> val = tentativeLogEntries.get(index);
        readLock.unlock();

        if (val != null) {
            return val.values();
        } else {
            return Sets.newHashSet();
        }
    }

    public Optional<Transaction<T>> getEntry(int index) {
        Lock readLock = logLock.readLock();
        readLock.lock();
        Transaction<T> val = committedLogEntries.get(index);
        readLock.unlock();

        return Optional.of(val);
    }

    public void commitEntry(Viewstamp id) {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();

        assert (tentativeLogEntries.containsKey(id));

        Transaction<T> entry = transactions.get(id);
        tentativeLogEntries.remove(id.getSequenceNumber());
        committedLogEntries.put(entry.getViewstamp().getSequenceNumber(), entry);

        if (id.getSequenceNumber() == lastCommited + 1) {
            lastCommited++;
        }

        writeLock.unlock();
    }

    public void addPrepareMessage(PrepareMessage message) {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();
        prepareMessages.put(message.getViewstamp(), message);
        writeLock.unlock();
    }

    public void addCommitMessage(CommitMessage message) {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();
        commitMessages.put(message.getViewstamp(), message);
        writeLock.unlock();
    }

    public Map<Viewstamp, PrepareMessage> getPrepareMessages() {
        return prepareMessages;
    }

    public Map<Viewstamp, CommitMessage> getCommitMessages() {
        return commitMessages;
    }
}
