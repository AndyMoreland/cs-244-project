import common.JsonThriftSerializationUtil;

import java.lang.System;
import org.junit.Test;
import PBFT.*;
import static org.junit.Assert.*;


public class JsonThriftSerializationUtilTest {

    @Test
    public void testOperationDeserialization() {
        TOperation operation = new TOperation()
                .setOperationType(1).setOperationId(1).setArguments("args").setReplicaId(5);
        String jsonRep = JsonThriftSerializationUtil.serializeToJsonOperation(operation);
        System.out.println(jsonRep);
        operation = JsonThriftSerializationUtil.deserializeToThriftOperation(jsonRep);
        assertEquals(operation.getReplicaId(),5);
    }
}