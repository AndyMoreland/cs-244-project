package config;

import com.google.common.base.Optional;
import common.CryptoUtil;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.security.*;

/**
 * Created by andrew on 11/27/14.
 */
public class GroupMember<T extends org.apache.thrift.TServiceClient> {
    public static final String ALGORITHM = "SHA1withDSA";
    public static final String PROVIDER = "SUN";
    private final Constructor<? extends T> clientCtor;
    private InetSocketAddress address;
    private final PublicKey publicKey;
    private final Optional<PrivateKey> privateKey;
    private final int id;
    
    public GroupMember(int id, InetSocketAddress address, Class<? extends T> impl, PublicKey publicKey, PrivateKey privateKey) throws NoSuchMethodException {
        this.publicKey = publicKey;
        this.id = id;
        this.privateKey = Optional.fromNullable(privateKey);
        this.clientCtor = impl.getConstructor();
        this.address = address;
    }

    public PrivateKey getPrivateKey() {
        return privateKey.get();
    }
    public int getReplicaID() {
        return id;
    }

    public T getThriftConnection() throws TTransportException {
        TSocket transport = new TSocket(address.getHostName(), address.getPort());
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

    public boolean verifySignature(Object message, byte[] signatureToVerify) {
        try {
            Signature newSig = Signature.getInstance(ALGORITHM, PROVIDER);
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
}
