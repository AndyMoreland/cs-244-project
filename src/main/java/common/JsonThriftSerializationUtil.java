package common;

import PBFT.TOperation;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by sctu on 12/1/14.
 */
public final class JsonThriftSerializationUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static TOperation deserializeToThriftOperation(String jsonRep) {
        try {
            return mapper.readValue(jsonRep, TOperation.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String serializeToJsonOperation(TOperation operation) {
        try {
            return mapper.writeValueAsString(operation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
