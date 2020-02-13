package host;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fuzzy.FuzzyException;
import fuzzy.Utilz;
import host.encryption.NoEncryption;

public class Producer extends Thread {

    private Logger log;

    private int id;
    private Buffer shared;
    private Schaduler boss;
    private Coder coder;

    public Producer(int id, Schaduler schaduler, Buffer buffer) {
        this.id = id;
        this.boss = schaduler;
        this.shared = buffer;

        this.coder = new NoEncryption();

        try {
            log = Utilz.initialLogger("producer#" + id);
        } catch (SecurityException | IOException e) {
            System.out.println("error creating producer log file");
        }
    }

    @Override
    public void run() {
        Utilz.logIt(log, "Producer (worker) start runing");
        while (true) {
            // shared.waitSemaphoreProduce();
            Partition partition = boss.getWork(id);
            if (partition == null) {
                Utilz.logIt(log, "no more job from boss (end of running)");
                return;
            }
            Utilz.logIt(log, "receive work and start working on it (partition idx = " + partition.index);
            byte[] encrypted = code(partition.data);
            Utilz.logIt(log, "job done");
            try {
                shared.putProduced(new Partition(partition.index, encrypted));
                Utilz.logIt(log, "partition ("+partition.index+") successfully placed in shared memory");
            } catch (FuzzyException e) {
                Utilz.logIt(log, "partition ("+partition.index+") data was bigger that block size in buffer", Level.WARNING);
            } catch (InterruptedException e) {
                Utilz.logIt(log, "[ERROR] intruppted!", Level.WARNING);
            }
        }
    }

    private byte[] code(byte[] data){
        return this.coder.code(data);
    }

}