package config;

import org.apache.thrift.transport.TTransportException;

import java.net.InetSocketAddress;
import java.security.PrivateKey;

/**
 * Created by leo on 12/1/14.
 */
public interface GroupMember<T extends org.apache.thrift.TServiceClient> {
    PrivateKey getPrivateKey();

    int getReplicaID();

    T getThriftConnection() throws TTransportException;

    boolean verifySignature(Object message, byte[] signatureToVerify);

    String getName();

    InetSocketAddress getAddress();
}
