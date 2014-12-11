package server;

import PBFT.*;
import com.google.common.collect.Maps;
import common.CryptoUtil;
import common.Digest;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by andrew on 11/25/14.
 */
public class PBFTCohortRunner {
    private static int numServers;
    private static Logger LOG = LogManager.getLogger(PBFTCohortRunner.class);
    private static final int CONFIG_FILE_POS = 0;

    private static final int POOL_SIZE = 10;
    private static final ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

    public static void main(final String[] args) throws InterruptedException, IOException {
        ConsoleAppender appender = new ConsoleAppender(new PatternLayout("{ %X{server-name} } " + PatternLayout.TTCC_CONVERSION_PATTERN));
        appender.setThreshold(Priority.WARN);
        BasicConfigurator.configure(appender);

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
            PBFTServerInstance server = new PBFTServerInstance(
                    new String[]{String.valueOf(i), "900" + i},
                    privateKeyMap.get(i),
                    publicKeyMap,
                    args[CONFIG_FILE_POS]
            );
            servers.put(i, server);
            server.run();
        }

        GroupConfigProvider<PBFTCohort.Client> leaderConfigProvider = servers.get(1).getConfigProvider();

//        for (int i = 1; i < 3; i++) {
//            testSystem(privateKeyMap, leaderConfigProvider, i);
//        }

        // make everyone send a message
        testSystem(privateKeyMap, leaderConfigProvider, 0);
        testSystem(privateKeyMap, leaderConfigProvider, 1);
        testSystem(privateKeyMap, leaderConfigProvider, 2);
        testViewChange(leaderConfigProvider);
        Thread.sleep(3000);
        LOG.warn("vc 1 done");
        testSystem(privateKeyMap, leaderConfigProvider, 3);
        testSystem(privateKeyMap, leaderConfigProvider, 4);
        testSystem(privateKeyMap, leaderConfigProvider, 5);
        testViewChange(leaderConfigProvider);
        Thread.sleep(3000);
        LOG.warn("vc 2 done");
        testSystem(privateKeyMap, leaderConfigProvider, 6);
        testSystem(privateKeyMap, leaderConfigProvider, 7);
        testSystem(privateKeyMap, leaderConfigProvider, 8);
        testViewChange(leaderConfigProvider);
        Thread.sleep(3000);
        LOG.warn("vc 3 done");
        testSystem(privateKeyMap, leaderConfigProvider, 9);
        testSystem(privateKeyMap, leaderConfigProvider, 10);
        testSystem(privateKeyMap, leaderConfigProvider, 11);
        testViewChange(leaderConfigProvider);
        Thread.sleep(3000);
        LOG.warn("vc 4 done");
        testSystem(privateKeyMap, leaderConfigProvider, 12);
        testSystem(privateKeyMap, leaderConfigProvider, 13);
        testSystem(privateKeyMap, leaderConfigProvider, 14);
        testViewChange(leaderConfigProvider);
        Thread.sleep(3000);
        LOG.warn("vc 5 done");
        testSystem(privateKeyMap, leaderConfigProvider, 15);
        testSystem(privateKeyMap, leaderConfigProvider, 16);
        testSystem(privateKeyMap, leaderConfigProvider, 17);
        testViewChange(leaderConfigProvider);
        Thread.sleep(3000);
        LOG.warn("vc 6 done");
        // make everyone send a message
    }

    private static void testViewChange(final GroupConfigProvider<PBFTCohort.Client> leaderConfigProvider) {
        // make everyone multicast view-change messages
        for (int i = 1; i <= numServers; i++) {
            final int index = i;
            pool.execute(new Runnable() {
                             @Override
                             public void run() {
                                 GroupMember < PBFTCohort.Client > groupMember = null;
                                 PBFTCohort.Client server = null;

                                 try {
                                     groupMember = leaderConfigProvider.getGroupMember(index);
                                     server = groupMember.getThriftConnection();
                                     server.initiateViewChange();
                                 } catch (TTransportException e) {
                                     System.err.println("Failed to initiate view change from server: " + index);
                                     e.printStackTrace();
                                 } catch (TException e) {
                                     System.err.println("Failed to initiate view change from server: " + index);
                                     e.printStackTrace();
                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 } finally {
                                     groupMember.returnThriftConnection(server);
                                 }
                             }
                         });
        }
    }

    private static void testSystem(
            Map<Integer, PrivateKey> privateKeyMap,
            GroupConfigProvider<PBFTCohort.Client> leaderConfigProvider,
            int sequenceNumber) {

        LOG.warn("Starting.");

        for (int i = 1; i <= numServers; i++) {
            int sendingReplicaID = leaderConfigProvider.getLeader().getReplicaID();
            Viewstamp viewstamp = new Viewstamp(sequenceNumber, leaderConfigProvider.getViewID());
            TTransaction transaction = new TTransaction(
                    viewstamp,
                    new TOperation(
                            TChineseCheckersOperation.NO_OP.getValue(),
                            "{}",
                            sendingReplicaID
                    ),
                    sendingReplicaID
            );

            Digest transactionDigest = CryptoUtil.computeDigest(common.Transaction.getTransactionForPBFTTransaction(transaction));

            PrePrepareMessage prePrepareMessage = new PrePrepareMessage(
                    viewstamp,
                    ByteBuffer.wrap(transactionDigest.getBytes()),
                    sendingReplicaID,
                    ByteBuffer.allocate(0)
            );


            ClientMessage clientMessage = new ClientMessage(
                    transaction.getOperation(), sendingReplicaID, null
            );

            clientMessage.setMessageSignature(CryptoUtil.computeMessageSignature(clientMessage, privateKeyMap.get(sendingReplicaID)).getBytes());
            prePrepareMessage.setMessageSignature(CryptoUtil.computeMessageSignature(prePrepareMessage, privateKeyMap.get(sendingReplicaID)).getBytes());

            GroupMember<PBFTCohort.Client> groupMember = null;
            PBFTCohort.Client secondServer = null;

            try {
                groupMember = leaderConfigProvider.getGroupMember(i);
                secondServer = groupMember.getThriftConnection();
                secondServer.prePrepare(prePrepareMessage, clientMessage, transaction);

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
