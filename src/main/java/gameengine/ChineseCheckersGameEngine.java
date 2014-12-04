package gameengine;

import PBFT.ClientMessage;
import PBFT.PBFTCohort;
import common.CryptoUtil;
import common.Transaction;
import config.GroupConfigProvider;
import config.GroupMember;
import statemachine.InvalidStateMachineOperationException;
import statemachine.Operation;

import java.nio.ByteBuffer;

/**
 * Created by leo on 12/3/14.
 */
public class ChineseCheckersGameEngine implements GameEngine<ChineseCheckersState> {

    private final GroupConfigProvider configProvider;
    private ChineseCheckersStateMachine stateMachine;

    public ChineseCheckersGameEngine(GroupConfigProvider configProvider){
        this.configProvider = configProvider;
        this.stateMachine = new ChineseCheckersStateMachine(ChineseCheckersState.buildGameForGroupMembers(configProvider.getGroupMembers()));
    }
    @Override
    public void notifyOnCommit(Transaction<Operation<ChineseCheckersState>> transaction) throws InvalidStateMachineOperationException {
        this.stateMachine.applyOperation(transaction.getValue());
    }

    @Override
    public void requestCommit(Operation<ChineseCheckersState> operation) {
        GroupMember<PBFTCohort.Client> leader = this.configProvider.getLeader();
        GroupMember<PBFTCohort.Client> me = this.configProvider.getMe();

        ClientMessage message = new ClientMessage();
        message.operation = operation.serialize();
        message.replicaId = me.getReplicaID();
        message.messageSignature = ByteBuffer.wrap(CryptoUtil.computeMessageSignature(message, me.getPrivateKey()).getBytes());

        PBFTCohort.Client thriftConnection = null;
        try {
            thriftConnection = leader.getThriftConnection();
            thriftConnection.clientMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            leader.returnThriftConnection(thriftConnection);
        }
    }

}
