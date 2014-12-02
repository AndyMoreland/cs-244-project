package config;

import com.google.common.base.Optional;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.thrift.transport.TTransportException;

import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by leo on 12/1/14.
 */
public class StubbedGroupMember<T extends org.apache.thrift.TServiceClient> extends NetworkedGroupMember<T> {

    private static Logger LOG = LogManager.getLogger(StubbedGroupMember.class);

    public StubbedGroupMember(String name, int id, InetSocketAddress address, Class<? extends T> impl, PublicKey publicKey, Optional<PrivateKey> privateKey) throws NoSuchMethodException {
        super(name, id, address, impl, publicKey, privateKey);
    }

    @Override
    public T getThriftConnection() throws TTransportException {
        return null;
    }
}
