package config;

import java.net.InetSocketAddress;
import java.security.PrivateKey;

/**
 * Created by leo on 12/1/14.
 */
public interface GroupMember<T extends org.apache.thrift.TServiceClient> {
    PrivateKey getPrivateKey();

    int getReplicaID();

    T getThriftConnection() throws Exception;
    void returnThriftConnection(T connection);

    boolean verifySignature(Object message, byte[] signatureToVerify);

    String getName();

    InetSocketAddress getAddress();
}
