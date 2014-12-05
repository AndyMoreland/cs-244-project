package config;

import com.google.common.base.Optional;
import common.CryptoUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.security.*;

/**
 * Created by andrew on 11/27/14.
 */
public class NetworkedGroupMember<T extends org.apache.thrift.TServiceClient> implements GroupMember<T> {
    public static final int TIMEOUT = 5000;
    private final Constructor<? extends T> clientCtor;
    private InetSocketAddress address;
    private final PublicKey publicKey;
    private final Optional<PrivateKey> privateKey;
    private final int id;
    private final String name;
//    private final GenericObjectPool<T> clientPool;
    
    public NetworkedGroupMember(String name, int id, InetSocketAddress address, Class<? extends T> impl, PublicKey publicKey, Optional<PrivateKey> privateKey) throws NoSuchMethodException {
        this.name = name;
        this.publicKey = publicKey;
        this.id = id;
        this.privateKey = privateKey;
        this.clientCtor = impl.getConstructor(TProtocol.class);
        this.address = address;

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMinIdle(10);
        genericObjectPoolConfig.setMaxIdle(200);
        genericObjectPoolConfig.setBlockWhenExhausted(true);

//        this.clientPool = new GenericObjectPool<T>(new ThriftConnectionFactory<T>(getAddress(), TIMEOUT, clientCtor));
//        clientPool.setConfig(genericObjectPoolConfig);
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey.get();
    }
    @Override
    public int getReplicaID() {
        return id;
    }

    @Override
    public T getThriftConnection() throws Exception {
        TSocket trans = new TSocket(address.getHostName(), address.getPort(), TIMEOUT);
        trans.open();
        return clientCtor.newInstance(new TBinaryProtocol(trans));
    }

    @Override
    public void returnThriftConnection(T connection) {
        connection.getInputProtocol().getTransport().close();
        connection.getOutputProtocol().getTransport().close();
    }

    @Override
    public boolean verifySignature(Object message, byte[] signatureToVerify) {
        try {
            Signature newSig = Signature.getInstance(CryptoUtil.ALGORITHM, CryptoUtil.PROVIDER);
            newSig.initVerify(publicKey);
            byte[] byteRep = CryptoUtil.convertToJsonByteArray(message);
            newSig.update(byteRep);
            newSig.verify(signatureToVerify);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InetSocketAddress getAddress() {
        return address;
    }
}
