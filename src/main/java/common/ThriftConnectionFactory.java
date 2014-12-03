package common;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

/**
 * Created by andrew on 12/2/14.
 */
public class ThriftConnectionFactory<T extends org.apache.thrift.TServiceClient> implements PooledObjectFactory<T> {

    private final InetSocketAddress address;
    private final int timeout;
    private final Constructor<? extends T> clientCtor;

    public ThriftConnectionFactory(InetSocketAddress address, int timeout, Constructor<? extends T> clientCtor) {
        this.address = address;
        this.timeout = timeout;
        this.clientCtor = clientCtor;
    }

    @Override
    public PooledObject<T> makeObject() throws Exception {
        TSocket transport = new TSocket(address.getHostName(), address.getPort(), timeout);
        transport.open();

        TProtocol protocol = new TBinaryProtocol(transport);
        T client = null;
        try {
            client = clientCtor.newInstance(protocol);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return new DefaultPooledObject<T>(client);

    }

    @Override
    public void destroyObject(PooledObject<T> pooledObject) throws Exception {
        pooledObject.getObject().getOutputProtocol().getTransport().close();
    }

    @Override
    public boolean validateObject(PooledObject<T> o) {
        return o.getObject().getOutputProtocol().getTransport().isOpen() && o.getObject().getInputProtocol().getTransport().isOpen();
    }

    @Override
    public void activateObject(PooledObject<T> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<T> pooledObject) throws Exception {

    }

}
