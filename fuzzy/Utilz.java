package fuzzy;

import java.io.File;
import java.io.FilenameFilter;
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

    public static void deleteLogs(){
        FilenameFilter filter = new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return (name.endsWith(".log"));
            }
        };
        File dir = new File(System.getProperty("user.dir"));

        String[] list = dir.list(filter);
        File file;
        if (list.length == 0) return;
        for(int i=0;i<list.length;i++){
            file = new File(System.getProperty("user.dir"), list[i]);
            System.out.println(file + " deleted: " + file.delete());
        }
    }

}