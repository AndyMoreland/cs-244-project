package server;

import PBFT.*;
import com.google.common.collect.Maps;
import common.CryptoUtil;
import common.TransactionDigest;
import config.GroupConfigProvider;
import config.GroupMember;
import org.apache.log4j.*;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

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

        for (int i = 1; i < 3; i++) {
            testSystem(privateKeyMap, leaderConfigProvider, i);
        }
    }

    private static void testSystem(Map<Integer, PrivateKey> privateKeyMap, GroupConfigProvider<PBFTCohort.Client> leaderConfigProvider, int sequenceNumber) {
        for (int i = 1; i <= 6; i++) {
            int sendingReplicaID = leaderConfigProvider.getLeader().getReplicaID();
            Viewstamp viewstamp = new Viewstamp(sequenceNumber, 0);
            TTransaction transaction = new TTransaction(
                    viewstamp,
                    new TOperation(
                            TChineseCheckersOperation.NO_OP.getValue(),
                            "{}",
                            sendingReplicaID
                    ),
                    sendingReplicaID
            );

            TransactionDigest transactionDigest = CryptoUtil.computeTransactionDigest(common.Transaction.getTransactionForPBFTTransaction(transaction));

            PrePrepareMessage message = new PrePrepareMessage(
                    viewstamp,
                    ByteBuffer.wrap(transactionDigest.getBytes()),
                    sendingReplicaID,
                    ByteBuffer.allocate(0)
            );

            message.setMessageSignature(CryptoUtil.computeMessageSignature(message, privateKeyMap.get(sendingReplicaID)).getBytes());

            GroupMember<PBFTCohort.Client> groupMember = null;
            PBFTCohort.Client secondServer = null;

            try {
                groupMember = leaderConfigProvider.getGroupMember(i);
                secondServer = groupMember.getThriftConnection();
                secondServer.prePrepare(message, transaction);
            } catch (TTransportException e) {
                System.err.println("Failed to send preprepare for server: " + i);
                e.printStackTrace();
            } catch (TException e) {
                System.err.println("Failed to send preprepare for server: " + i);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                groupMember.returnThriftConnection(secondServer);
            }
        }
    }
}
