package common;

import PBFT.CommitMessage;
import PBFT.PrepareMessage;
import PBFT.Viewstamp;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by andrew on 11/27/14.
 */
public class Transaction<T> {
    private Viewstamp id;
    private final int targetIndex;
    private final T value;
    private boolean committed;
    private Viewstamp viewStamp;
    private final Set<PrepareMessage> prepareMessages = Sets.newHashSet();
    private final Set<CommitMessage> commitMessages = Sets.newHashSet();

    public Transaction(Viewstamp id, int targetIndex, T value) {
        this.id = id;
        this.targetIndex = targetIndex;
        this.value = value;
        this.committed = false;
    }

    public void commit() {
        committed = true;
    }

    public Viewstamp getViewstamp() {
        return id;
    }

    public Set<CommitMessage> getCommitMessages() {
        return commitMessages;
    }

    public Set<PrepareMessage> getPrepareMessages() {
        return prepareMessages;
    }
}
