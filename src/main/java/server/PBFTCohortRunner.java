package server;

import PBFT.*;
import com.google.common.collect.Maps;
import common.CryptoUtil;
import common.TransactionDigest;
import config.GroupConfigProvider;
import org.apache.log4j.*;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

/**
 * Created by andrew on 11/25/14.
 */
public class PBFTCohortRunner {
    public static final int NUM_SERVERS = 6;
    private static Logger LOG = LogManager.getLogger(PBFTCohortRunner.class);

    public static void main(final String[] args) throws InterruptedException {
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("{ %X{server-name} } " + PatternLayout.TTCC_CONVERSION_PATTERN)));

        Map<Integer, PublicKey> publicKeyMap = Maps.newHashMap();
        Map<Integer, PrivateKey> privateKeyMap = Maps.newHashMap();
        Map<Integer, PBFTServerInstance> servers = Maps.newHashMap();

        for (int i = 1; i <= NUM_SERVERS; i++) {
            KeyPair keyPair = CryptoUtil.generateNewKeyPair();
            publicKeyMap.put(i, keyPair.getPublic());
            privateKeyMap.put(i, keyPair.getPrivate());
        }

        for (int i = 1; i <= NUM_SERVERS; i++) {
            PBFTServerInstance server = new PBFTServerInstance(new String[]{String.valueOf(i), "900" + i}, privateKeyMap.get(i), publicKeyMap);
            servers.put(i, server);
            server.run();
        }

        GroupConfigProvider<PBFTCohort.Client> leaderConfigProvider = servers.get(1).getConfigProvider();

        try {
            testSystem(privateKeyMap, leaderConfigProvider);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    private static void testSystem(Map<Integer, PrivateKey> privateKeyMap, GroupConfigProvider<PBFTCohort.Client> leaderConfigProvider) throws TException {
        for (int i = 2; i <= 6; i++) {
            int sendingReplicaID = leaderConfigProvider.getLeader().getReplicaID();
            TTransaction transaction = new TTransaction(
                    new Viewstamp(1, 0),
                    new TOperation(
                            0,
                            TChineseCheckersOperation.NO_OP.getValue(),
                            "{}",
                            sendingReplicaID
                    ),
                    sendingReplicaID
            );

            TransactionDigest transactionDigest = CryptoUtil.computeTransactionDigest(common.Transaction.getTransactionForPBFTTransaction(transaction));

            PrePrepareMessage message = new PrePrepareMessage(
                    new Viewstamp(1, 0),
                    ByteBuffer.wrap(transactionDigest.getBytes()),
                    sendingReplicaID,
                    ByteBuffer.allocate(0)
            );

            message.setMessageSignature(CryptoUtil.computeMessageSignature(message, privateKeyMap.get(sendingReplicaID)).getBytes());

            PBFTCohort.Client secondServer = leaderConfigProvider.getGroupMember(i).getThriftConnection();
            secondServer.prePrepare(message, transaction);
        }
    }
}
