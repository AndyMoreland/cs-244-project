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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

/**
 * Created by andrew on 11/25/14.
 */
public class PBFTCohortRunner {
    private static int numServers;
    private static Logger LOG = LogManager.getLogger(PBFTCohortRunner.class);
    private static final int CONFIG_FILE_POS = 0;

    public static void main(final String[] args) throws InterruptedException, IOException {
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("{ %X{server-name} } " + PatternLayout.TTCC_CONVERSION_PATTERN)));

        Map<Integer, PublicKey> publicKeyMap = Maps.newHashMap();
        Map<Integer, PrivateKey> privateKeyMap = Maps.newHashMap();
        Map<Integer, PBFTServerInstance> servers = Maps.newHashMap();

        numServers = PBFTServerInstanceRunner.readServerConfigForNumServers(new File(args[CONFIG_FILE_POS]));

        for (int i = 1; i <= numServers; i++) {
            KeyPair keyPair = CryptoUtil.generateNewKeyPair();
            publicKeyMap.put(i, keyPair.getPublic());
            privateKeyMap.put(i, keyPair.getPrivate());
        }

        for (int i = 1; i <= numServers; i++) {
            PBFTServerInstance server = new PBFTServerInstance(new String[]{String.valueOf(i), "900" + i}, privateKeyMap.get(i), publicKeyMap, args[CONFIG_FILE_POS]);
            servers.put(i, server);
            server.run();
        }

        GroupConfigProvider<PBFTCohort.Client> leaderConfigProvider = servers.get(1).getConfigProvider();

        for (int i = 1; i < 3; i++) {
            testSystem(privateKeyMap, leaderConfigProvider, i);
        }

        testViewChange(leaderConfigProvider);
    }

    private static void testViewChange(GroupConfigProvider<PBFTCohort.Client> leaderConfigProvider) {

        // make everyone multicast view-change messages
        for (int i = 1; i <= numServers-1; i++) {
            GroupMember<PBFTCohort.Client> groupMember = null;
            PBFTCohort.Client server = null;

            try {
                groupMember = leaderConfigProvider.getGroupMember(i);
                server = groupMember.getThriftConnection();
                server.initiateViewChange();
            } catch (TTransportException e) {
                System.err.println("Failed to initiate view change from server: " + i);
                e.printStackTrace();
            } catch (TException e) {
                System.err.println("Failed to initiate view change from server: " + i);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                groupMember.returnThriftConnection(server);
            }
        }
    }

    private static void testSystem(
            Map<Integer, PrivateKey> privateKeyMap,
            GroupConfigProvider<PBFTCohort.Client> leaderConfigProvider,
            int sequenceNumber) {

        for (int i = 1; i <= numServers; i++) {
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
