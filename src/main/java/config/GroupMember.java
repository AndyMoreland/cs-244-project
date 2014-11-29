package config;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

/**
 * Created by andrew on 11/27/14.
 */
public class GroupMember<T extends org.apache.thrift.TServiceClient> {
    private final Constructor<? extends T> clientCtor;
    private InetSocketAddress address;

    public GroupMember(Class<? extends T> impl, InetSocketAddress address) throws NoSuchMethodException {
        this.clientCtor = impl.getConstructor();
        this.address = address;
    }

    public T getThriftConnection() throws IllegalAccessException, InvocationTargetException, InstantiationException, TTransportException {
        TSocket transport = new TSocket(address.getHostName(), address.getPort());
        transport.open();

        TProtocol protocol = new TBinaryProtocol(transport);
        T client = clientCtor.newInstance(protocol);

        return client;
    }
}
