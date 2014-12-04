package server;

import com.google.common.collect.Maps;
import common.CryptoUtil;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by andrew on 12/3/14.
 */
public class PBFTServerInstanceRunner {

    private static final int PRIVATE_KEY_FILE = 0;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final int CONFIG_SERVER_ARG_POS = 1;
    private static final int CONFIG_SERVER_ID_POS = 2;
    private static final int LOG_FILE_POS = 3;

    public static void main(String args[]) throws IOException {
        if (args.length == 3) {
            BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("{ %X{server-name} } " + PatternLayout.TTCC_CONVERSION_PATTERN)));
        } else if (args.length == 4) {
            BasicConfigurator.configure(new FileAppender(new PatternLayout("{ %X{server-name} } " + PatternLayout.TTCC_CONVERSION_PATTERN), args[LOG_FILE_POS]));
        }

        PBFTServerInstance instance = null;

        try {
            Map<Integer, File> publicKeyMap = readServerConfigForPublicKeys(new File(args[CONFIG_SERVER_ARG_POS]));
            instance = new PBFTServerInstance(
                    new String[]{args[CONFIG_SERVER_ID_POS]},
                    readPrivateKeyFromFile(new File(args[PRIVATE_KEY_FILE])),
                    readPublicKeysFromFile(publicKeyMap)
            );
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        instance.run();
    }

    private static Map<Integer, File> readServerConfigForPublicKeys(File file) throws IOException {
        Map<Integer, File> publicKeyFileMap = Maps.newHashMap();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        JsonFactory factory = new JsonFactory();
        JsonParser jsonParser = factory.createJsonParser(reader);
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(jsonParser);
        Iterator<JsonNode> serverIterator = root.get("servers").getElements();

        while (serverIterator.hasNext()) {
            JsonNode server = serverIterator.next();

            publicKeyFileMap.put(server.get("id").getIntValue(), new File(server.get("public_key").getTextValue()));
        }

        return publicKeyFileMap;
    }

    private static PrivateKey readPrivateKeyFromFile(File file) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtil.ALGORITHM);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] keyBytes = new byte[(int) file.length()];
        fileInputStream.read(keyBytes);

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private static Map<Integer, PublicKey> readPublicKeysFromFile(Map<Integer, File> publicKeyMap) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        Map<Integer,PublicKey> map = Maps.newHashMap();

        for (Map.Entry<Integer, File> entry : publicKeyMap.entrySet()) {
            KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtil.ALGORITHM);
            byte[] keyBytes = new byte[(int) entry.getValue().length()];
            FileInputStream fileInputStream = new FileInputStream(entry.getValue());
            fileInputStream.read(keyBytes);

            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));

            map.put(entry.getKey(), publicKey);
        }

        return map;
    }
}
