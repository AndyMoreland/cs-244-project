package common;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;

import java.io.IOException;
import java.security.*;

/**
 * Created by sctu on 11/30/14.
 */
public final class CryptoUtil {
    private static final ObjectMapper mapper = new ObjectMapper(); // thread-safe
    private static final String[] IGNORED_FIELD = {"messageSignature"};
    private static final FilterProvider FIELD_FILTER = new SimpleFilterProvider()
            .addFilter("pre-prepare filter",
                    SimpleBeanPropertyFilter.serializeAllExcept(IGNORED_FIELD));

    private CryptoUtil() {
        // don't instantiate
    }

    private static byte[] computeSignature(byte[] byteRep, Signature sig) {
        try {
            sig.update(byteRep);
            return sig.sign();
        } catch (SignatureException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public static TransactionDigest computeTransactionDigest(Transaction transaction) {
        try {
            ObjectWriter writer = mapper.writer();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return new TransactionDigest(digest.digest(writer.writeValueAsString(transaction).getBytes()));
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static byte[] convertToJsonByteArray(Object message) {
        ObjectWriter writer = mapper.writer(FIELD_FILTER);
        try {
            return writer.writeValueAsString(message).getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MessageSignature computeMessageSignature(Object message, PrivateKey privateKey) {
        Signature signature = null;
        try {
            signature = Signature.getInstance("SHA1withDSA", "SUN");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            signature.initSign(privateKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        ObjectWriter writer = mapper.writer(FIELD_FILTER);
        return new MessageSignature(computeSignature(convertToJsonByteArray(message), signature));
    }
}
