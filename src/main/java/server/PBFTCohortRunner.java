package server;

import PBFT.PBFTCohort;
import com.google.common.collect.Maps;
import common.CryptoUtil;
import config.GroupConfigProvider;
import org.apache.log4j.*;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

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

        for(int i = 1; i <= NUM_SERVERS; i++) {
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
            System.err.println("Attempting to get leader ocnnection");
            PBFTCohort.Client leaderConnection = leaderConfigProvider.getLeader().getThriftConnection();
            System.err.println("Attempting to ping");
            leaderConnection.ping();
            System.err.println("Pinged");
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
