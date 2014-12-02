package server;

import org.apache.log4j.*;

/**
 * Created by andrew on 11/25/14.
 */
public class PBFTCohortRunner {
    private static Logger LOG = LogManager.getLogger(PBFTCohortRunner.class);

    public static void main(final String[] args) throws InterruptedException {
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("{ %X{server-name} } " + PatternLayout.TTCC_CONVERSION_PATTERN)));
        for (int i = 1; i <= 6; i++) {
            Runnable server = new PBFTServerInstance(new String[]{String.valueOf(i), "900" + i});
            server.run();
            Thread.sleep(1000);
        }
    }
}
