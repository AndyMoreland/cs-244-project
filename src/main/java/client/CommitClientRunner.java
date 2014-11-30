package client;

import TwoPhaseCommit.CommitClient;
import com.google.common.collect.Sets;
import config.GroupConfigProvider;
import config.GroupMember;
import config.StaticGroupConfigProvider;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import tutorial.Calculator;

/**
 * Created by andrew on 11/25/14.
 */
public class CommitClientRunner {
    public static void main(String [] args) {

        CommitClientHandler handler;

        CommitClient.Processor processor;
        
        GroupConfigProvider configProvider = new StaticGroupConfigProvider(leader, Sets.<GroupMember>newHashSet());

        try {
            handler = new CommitClientHandler();
            processor = new CommitClient.Processor(handler);

            TProtocol protocol = new TBinaryProtocol(transport);

            perform(client);

            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }

    private static void perform(Calculator.Client client) throws TException
    {

    }

}
