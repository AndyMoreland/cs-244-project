package server;

import PBFT.PBFTCohort;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import common.CryptoUtil;
import config.GroupConfigProvider;
import config.GroupMember;
import config.StaticGroupConfigProvider;
import org.apache.log4j.*;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by andrew on 12/1/14.
 */
public class PBFTServerInstance implements Runnable {
    private static Logger LOG = LogManager.getLogger(PBFTServerInstance.class);

    public static final int INITIAL_VIEW_ID = 0;
    public static final String CONFIG_FILE = "cluster_config.json";
    private static final int REPLICA_ID_ARG_POS = 0;
    private static final int PORT_ARG_POS = 1;

    public PBFTCohortHandler handler;

    public PBFTCohort.Processor processor;

    private GroupConfigProvider<PBFTCohort.Client> configProvider;

    private int replicaID;
    private final String[] args;
    private String name;
    private int port;

    public PBFTServerInstance(String[] args) {
        this.args = args;
    }

    private void configureLogging() {
        MDC.put("server-name", "[" + this.replicaID + "] " + this.name + ":" + this.port);
    }

    public void run() {
        System.err.println("calling run method");
        try {
            replicaID = Integer.parseInt(args[REPLICA_ID_ARG_POS]);
            port = Integer.parseInt(args[PORT_ARG_POS]);

            KeyPair keyPair = CryptoUtil.generateNewKeyPair();
            GroupMember<PBFTCohort.Client> me = new GroupMember<PBFTCohort.Client>(replicaID, new InetSocketAddress("localhost", port), PBFTCohort.Client.class, keyPair.getPublic(), Optional.of(keyPair.getPrivate()));

            configProvider = initializeConfigProvider(me, new File(CONFIG_FILE));
            configureLogging();

            LOG.info("Starting server on port: " + port + " with address: " + "localhost");
            LOG.info(configProvider.toString());

            handler = new PBFTCohortHandler(configProvider, replicaID, me);
            processor = new PBFTCohort.Processor(handler);

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor, port);
                }
            };

            new Thread(simple).start();

            me.getThriftConnection().ping();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public void simple(PBFTCohort.Processor processor, int port) {
        try {
            TServerTransport serverTransport = new TServerSocket(port);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private StaticGroupConfigProvider<PBFTCohort.Client> initializeConfigProvider(GroupMember<PBFTCohort.Client> me, File file) throws NoSuchMethodException {
        try {
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

            while (elements.hasNext()) {
                JsonNode server = elements.next();

                GroupMember<PBFTCohort.Client> client = serverNodeToClient(server);
                if (client.getReplicaID() == leaderId) {
                    leader = client;
                }

                if (client.getReplicaID() != me.getReplicaID()) {
                    clients.add(client);
                } else {
                    this.name = server.get("name").getTextValue();
                }
            }

            return new StaticGroupConfigProvider<PBFTCohort.Client>(leader, clients, INITIAL_VIEW_ID);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private GroupMember<PBFTCohort.Client> serverNodeToClient(JsonNode server) throws NoSuchMethodException {
        KeyPair keyPair = CryptoUtil.generateNewKeyPair();

        return new GroupMember<PBFTCohort.Client>(
                server.get("id").getIntValue(),
                new InetSocketAddress(server.get("hostname").getTextValue(), server.get("port").getIntValue()),
                PBFTCohort.Client.class,
                keyPair.getPublic(),
                Optional.of(keyPair.getPrivate()));
    }

}
