package server;

import PBFT.PBFTCohort;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import config.*;
import gameengine.BenchmarkingGameEngine;
import gameengine.ChineseCheckersGameEngine;
import gameengine.ChineseCheckersState;
import gameengine.GameEngine;
import gameengine.operations.NoOp;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by andrew on 12/1/14.
 */
public class PBFTServerInstance implements Runnable {
    private static Logger LOG = LogManager.getLogger(PBFTServerInstance.class);

    public static final int INITIAL_VIEW_ID = 0;
    private static final int REPLICA_ID_ARG_POS = 0;

    public PBFTCohortHandler handler;
    public PBFTCohort.Processor processor;
    private GroupConfigProvider<PBFTCohort.Client> configProvider;
    public static  String configFile;


    private int replicaID;

    private final String[] args;
    private final PrivateKey privateKey;
    private final Map<Integer, PublicKey> publicKeys;
    private GameEngine<ChineseCheckersState> gameEngine;

    public PBFTServerInstance(String[] args,
                              PrivateKey privateKey, Map<Integer, PublicKey> publicKeys,
                              String configFile) {
        this.args = args;
        this.privateKey = privateKey;
        this.publicKeys = publicKeys;
        this.configFile = configFile;
        this.gameEngine = null;
    }

    private void configureLogging(GroupMember<PBFTCohort.Client> me) {
        MDC.put("server-name", "[" + this.replicaID + "] " + me.getName() + ":" + me.getAddress().getHostName() + "/" + me.getAddress().getPort());
    }

    public void run() {
        try {
            replicaID = Integer.parseInt(args[REPLICA_ID_ARG_POS]);
            configProvider = initializeConfigProvider(new File(configFile));

            final GroupMember<PBFTCohort.Client> me = configProvider.getGroupMember(this.replicaID);
            configProvider.getOtherGroupMembers().remove(me);

            configureLogging(me);

            LOG.info("Starting server on port: " + me.getAddress().getPort() + " with address: " + me.getAddress().getHostName());

            // GameEngine<ChineseCheckersState> engine = new ChineseCheckersGameEngine(configProvider);
            this.gameEngine = new BenchmarkingGameEngine(configProvider, this);

            handler = new PBFTCohortHandler(configProvider, replicaID, me, gameEngine);
            processor = new PBFTCohort.Processor(handler);

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor, me.getAddress());
                }
            };

            new Thread(simple).start();

            gameEngine.requestCommit(new NoOp());
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public void notifyOnNextTurn() {
        LOG.info("next turn");
        gameEngine.requestCommit(new NoOp());
    }

    private void simple(PBFTCohort.Processor processor, InetSocketAddress address) {
        try {
            TServerTransport serverTransport = new TServerSocket(address);
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor).maxWorkerThreads(1000).minWorkerThreads(100));

            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GroupConfigProvider<PBFTCohort.Client> initializeConfigProvider(File file) throws NoSuchMethodException, IOException, FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        JsonFactory factory = new JsonFactory();
        JsonParser jsonParser = factory.createJsonParser(reader);
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(jsonParser);
        JsonNode servers = root.get("servers");

        GroupMember<PBFTCohort.Client> leader = null;
        Set<GroupMember<PBFTCohort.Client>> clients = Sets.newHashSet();

        int leaderId = root.get("primary").getIntValue();
        Iterator<JsonNode> elements = servers.getElements();

        GroupMember<PBFTCohort.Client> me = null;
        while (elements.hasNext()) {
            JsonNode server = elements.next();

            GroupMember<PBFTCohort.Client> client = serverNodeToClient(server);
            if (client.getReplicaID() == leaderId) {
                leader = client;
            }

            if (client.getReplicaID() == replicaID) {
                me = client;
            }

            clients.add(client);
        }

        return new StaticGroupConfigProvider<PBFTCohort.Client>(leader, me, clients, INITIAL_VIEW_ID);
    }

    private GroupMember<PBFTCohort.Client> serverNodeToClient(JsonNode server) throws NoSuchMethodException, UnknownHostException {
        int id = server.get("id").getIntValue();
        JsonNode isMock = server.get("mock");
        if(isMock == null || !isMock.getBooleanValue()) return new NetworkedGroupMember<PBFTCohort.Client>(
                server.get("name").getTextValue(),
                id,
                new InetSocketAddress(server.get("hostname").getTextValue(), server.get("port").getIntValue()),
                PBFTCohort.Client.class,
                publicKeys.get(id),
                Optional.fromNullable(id == this.replicaID ? this.privateKey : null)
            );
        else {
            return new StubbedGroupMember<PBFTCohort.Client>(
                server.get("name").getTextValue(),
                id,
                new InetSocketAddress(server.get("hostname").getTextValue(), server.get("port").getIntValue()),
                PBFTCohort.Client.class,
                publicKeys.get(id),
                Optional.fromNullable(id == this.replicaID ? this.privateKey : null)
            );
        }
    }

    public GroupConfigProvider<PBFTCohort.Client> getConfigProvider() {
        return configProvider;
    }
}
