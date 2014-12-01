package common;

/**
 * Created by andrew on 11/30/14.
 */
public class Viewstamp {
    private final int sequenceNumber;
    private final long timestamp;
    private final int viewId;

    public Viewstamp(int sequenceNumber, int viewId, long timestamp) {
        this.sequenceNumber = sequenceNumber;
        this.timestamp = timestamp;
        this.viewId = viewId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Viewstamp viewstamp = (Viewstamp) o;

        if (sequenceNumber != viewstamp.sequenceNumber) return false;
        if (timestamp != viewstamp.timestamp) return false;
        if (viewId != viewstamp.viewId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sequenceNumber;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + viewId;
        return result;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }
}
