package gameengine;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import common.CryptoUtil;
import common.Digest;
import common.StateMachineListener;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import statemachine.InvalidStateMachineOperationException;
import statemachine.Operation;
import statemachine.StateMachine;

import java.util.List;

/**
 * Created by andrew on 11/30/14.
 */
public class ChineseCheckersStateMachine implements StateMachine<ChineseCheckersState, Operation<ChineseCheckersState>> {
    private static Logger LOG = LogManager.getLogger(ChineseCheckersStateMachine.class);

    private ChineseCheckersState state;
    private int numOperationsApplied;

    private static final int CHECKPOINT_INTERVAL = 20;
    private int lastCheckpointed;
    private Optional<Digest> checkpointDigest;
    private List<StateMachineListener> listeners;


    public ChineseCheckersStateMachine(ChineseCheckersState state) {
        this.state = state;
        this.numOperationsApplied = 0;
        this.lastCheckpointed = -1; // never checkpointed
        this.checkpointDigest = Optional.absent();
        this.listeners = Lists.newArrayList();
    }

    @Override
    synchronized public void applyOperation(Operation<ChineseCheckersState> op) throws InvalidStateMachineOperationException {
        LOG.info("Applying operation: " + op);

        if (!op.isValid(this.getState())) { throw new InvalidStateMachineOperationException(); }

        op.apply(this.getState());
        numOperationsApplied++;
        // checkpointing currently blocks application of moves
        if ((numOperationsApplied % CHECKPOINT_INTERVAL) == 0) {
            LOG.info("Checkpointing after operation " + numOperationsApplied);
            checkpointDigest = Optional.of(CryptoUtil.computeDigest(this));
            lastCheckpointed = numOperationsApplied;
            for (StateMachineListener listener : listeners) {
                listener.notifyOnCheckpointed(lastCheckpointed, checkpointDigest.get());
            }
        }
    }

    synchronized public ChineseCheckersState getState() {
        return state;
    }

    synchronized public void setState(ChineseCheckersState newState) {
        state = newState;
    }

    @Override
    public void addCheckpointListener(StateMachineListener listener) {
        listeners.add(listener);
    }
}
