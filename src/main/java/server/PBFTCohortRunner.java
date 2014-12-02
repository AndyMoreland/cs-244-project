package server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by andrew on 11/25/14.
 */
public class PBFTCohortRunner {
    private static Logger LOG = LogManager.getLogger(PBFTCohortRunner.class);

    public static void main(final String[] args) throws InterruptedException {
        for (int i = 1; i <= 6; i++) {
            Runnable server = new PBFTServerInstance(new String[]{String.valueOf(i), "900" + i});
            server.run();
            Thread.sleep(1000);
        }
    }
}
