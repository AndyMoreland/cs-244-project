package common;

public interface StateMachineListener {
    public void notifyOnCheckpointed(int seqNo, Digest digest);
}
