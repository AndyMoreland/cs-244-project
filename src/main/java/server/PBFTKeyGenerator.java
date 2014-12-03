package server;

import common.CryptoUtil;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.security.KeyPair;
import java.util.Iterator;

/**
 * Created by andrew on 12/3/14.
 */
public class PBFTKeyGenerator {
    public static void main(String args[]) {
        File configFile = new File(args[0]);

        try {
            BufferedReader reader = null;
            reader = new BufferedReader(new FileReader(configFile));
            JsonFactory factory = new JsonFactory();
            JsonParser jsonParser = factory.createJsonParser(reader);
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(jsonParser);
            Iterator<JsonNode> serverIterator = root.get("servers").getElements();

            while (serverIterator.hasNext()) {
                JsonNode server = serverIterator.next();
                KeyPair keyPair = CryptoUtil.generateNewKeyPair();

                File publicKeyFile = new File(server.get("public_key").getTextValue());
                File privateKeyFile = new File(server.get("private_key").getTextValue());

                FileOutputStream publicKeyOutputStream = new FileOutputStream(publicKeyFile);
                FileOutputStream privateKeyOutputStream = new FileOutputStream(privateKeyFile);

                publicKeyOutputStream.write(keyPair.getPublic().getEncoded());
                privateKeyOutputStream.write(keyPair.getPrivate().getEncoded());

                publicKeyOutputStream.close();
                privateKeyOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
