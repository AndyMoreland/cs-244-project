package common;

public interface StateMachineCheckpointListener {
    public void notifyOnCheckpointed(int seqNo, Digest digest);
}
