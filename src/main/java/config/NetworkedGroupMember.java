package config;

import com.google.common.base.Optional;
import common.CryptoUtil;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.security.*;

/**
 * Created by andrew on 11/27/14.
 */
public class NetworkedGroupMember<T extends org.apache.thrift.TServiceClient> implements GroupMember<T> {
    private final Constructor<? extends T> clientCtor;
    private InetSocketAddress address;
    private final PublicKey publicKey;
    private final Optional<PrivateKey> privateKey;
    private final int id;
    private final String name;
    
    public NetworkedGroupMember(String name, int id, InetSocketAddress address, Class<? extends T> impl, PublicKey publicKey, Optional<PrivateKey> privateKey) throws NoSuchMethodException {
        this.name = name;
        this.publicKey = publicKey;
        this.id = id;
        this.privateKey = privateKey;
        this.clientCtor = impl.getConstructor(TProtocol.class);
        this.address = address;
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
    public T getThriftConnection() throws TTransportException {
        TSocket transport = new TSocket(address.getHostName(), address.getPort(), 5000);
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

        return client;
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
