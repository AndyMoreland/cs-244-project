package common;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import statemachine.StateMachine;

import java.io.IOException;
import java.security.*;

/**
 * Created by sctu on 11/30/14.
 */
public final class CryptoUtil {
    private static final boolean NO_CRYPTO = false;
    private static Logger LOG = LogManager.getLogger(CryptoUtil.class.getName());

    private static final ObjectMapper mapper = new ObjectMapper(); // thread-safe
    private static final String[] IGNORED_FIELD = {"messageSignature"};
    private static final FilterProvider FIELD_FILTER = new SimpleFilterProvider()
            .addFilter("pre-prepare filter",
                    SimpleBeanPropertyFilter.serializeAllExcept(IGNORED_FIELD));
    public static final String ALGORITHM = "DSA";
    public static final String PROVIDER = "SUN";
    public static final String DIGEST_TYPE = "SHA-256";
    public static final String KEY_PAIR_TYPE = "RSA";
    public static final String PRNG_TYPE = "SHA1PRNG";

    private CryptoUtil() {
        // don't instantiate
    }

    private static byte[] computeSignature(byte[] byteRep, Signature sig) {
        if (NO_CRYPTO) return new byte[10];
        try {
            sig.update(byteRep);
            return sig.sign();
        } catch (SignatureException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public static Digest computeDigest(Object value) {
        if (NO_CRYPTO) return new Digest(new byte[10]);
        try {
            ObjectWriter writer = mapper.writer();
            MessageDigest digest = MessageDigest.getInstance(DIGEST_TYPE);
            return new Digest(digest.digest(writer.writeValueAsString(value).getBytes()));
        } catch (NoSuchAlgorithmException | IOException e) {
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
        if (NO_CRYPTO) return new MessageSignature(new byte[10]);
        try {
            signature = Signature.getInstance(ALGORITHM, PROVIDER);
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
        return new MessageSignature(computeSignature(convertToJsonByteArray(message), signature));
    }

    public static KeyPair generateNewKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
            SecureRandom random = SecureRandom.getInstance(PRNG_TYPE, PROVIDER);
            generator.initialize(1024, random);

            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }

        return null;
    }
}
