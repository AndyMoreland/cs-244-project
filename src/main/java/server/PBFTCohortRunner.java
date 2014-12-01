package server;

import PBFT.PBFTCohort;
import com.google.common.collect.Sets;
import config.GroupConfigProvider;
import config.GroupMember;
import config.StaticGroupConfigProvider;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import static org.apache.thrift.server.TServer.Args;

/**
 * Created by andrew on 11/25/14.
 */
public class PBFTCohortRunner {

    public static PBFTCohortHandler handler;

    public static PBFTCohort.Processor processor;

    private static GroupConfigProvider configProvider = new StaticGroupConfigProvider(null, Sets.<GroupMember>newHashSet(), 0);

    private static final int REPLICA_ID_ARG_POS = 2;
    private static final int PORT_ARG_POS = 3;

    public static void main(final String [] args) {
        try {
            handler = new PBFTCohortHandler(configProvider, Integer.parseInt(args[REPLICA_ID_ARG_POS]));
            processor = new PBFTCohort.Processor(handler);

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor, Integer.parseInt(args[PORT_ARG_POS]));
                }
            };

            new Thread(simple).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple(PBFTCohort.Processor processor, int port) {
        try {
            TServerTransport serverTransport = new TServerSocket(port);
            TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

            // Use this for a multithreaded server
            // TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
