package common;

/**
 * Created by andrew on 11/30/14.
 */
public class Viewstamp {
    private long timestamp;
    private int viewId;

    public Viewstamp(long timestamp, int viewId) {
        this.timestamp = timestamp;
        this.viewId = viewId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Viewstamp viewstamp = (Viewstamp) o;

        if (timestamp != viewstamp.timestamp) return false;
        if (viewId != viewstamp.viewId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + viewId;
        return result;
    }
}
