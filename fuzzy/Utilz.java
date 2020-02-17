package fuzzy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Utilz {

    public static Logger initialLogger(String filename) throws SecurityException, IOException {
        Logger log = Logger.getLogger(filename);
        log.setLevel(Level.ALL);
        FileHandler fh = new FileHandler(filename + ".log");
        fh.setFormatter(new SimpleFormatter());
        log.addHandler(fh);
        return log;
    }

    public static void logIt(Logger log, String msg) {
        if (log == null) {
            System.out.println("log is null");
            return;
        }
        log.log(Level.ALL, msg);
    }

    public static void logIt(Logger log, String msg, Level level) {
        if (log == null) {
            System.out.println("log is null");
            return;
        }
        log.log(level, msg);
    }

    public static void main(String[] args) throws UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost(); 
        System.out.println("System IP Address : " + 
                      (localhost.getHostAddress()).trim()); 
    }

}