package host;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import fuzzy.FuzzyException;
import fuzzy.Utilz;

public class Buffer {

    public static final int MAX_TRY_COUNT = 5;

    private Logger log;

    private byte[][] memory;
    private int[] status;
    private Lock[] blockLocks;
    private Semaphore produce;
    private Semaphore consume;

    private int blockSize;
    private int blockCount;

    public Buffer(int blockSize, int blockCount) {
        memory = new byte[blockCount][];
        this.blockCount = blockCount;
        this.blockSize = blockSize;

        try {
            log = Utilz.initialLogger("buffer");
        } catch (SecurityException | IOException e) {
            System.out.println("error opening buffer log file");
        }

        status = new int[blockCount];
        blockLocks = new Lock[blockCount];
        for(int i=0;i<blockCount;i++){
            status[i] = -1;
            blockLocks[i] = new ReentrantLock();
        }

        produce = new Semaphore(blockCount);
        consume = new Semaphore(0);

        Utilz.logIt(log, "buffer successfully created with block size of ["
                +blockSize+"] and block count of ["+blockCount+"]");
    }

    public void putProduced(Partition partition) throws FuzzyException, InterruptedException {
        Utilz.logIt(log, "Adding partition of ["+partition.index+"] to buffer started (partition size = "+partition.data.length+")");
        produce.acquire();
        if(partition.data.length > blockSize) throw new FuzzyException("data oversized fatal error");
        int current=0;
        while(true){
            boolean moreTry = true;
            int tryCount = MAX_TRY_COUNT;
            while(moreTry && tryCount > 0){
                if(blockLocks[current].tryLock()){
                    //lock acquired
                    if(status[current]!=-1){
                        //not empty block
                        blockLocks[current].unlock();
                        moreTry = false;
                    }else{
                        //empty block found
                        status[current] = partition.index;
                        memory[current] = new byte[partition.data.length];
                        memory[current] = Arrays.copyOf(partition.data, partition.data.length);
                        consume.release();
                        Utilz.logIt(log, "partition with index["+partition.index+"] placed at ["+current+"]");
                        Utilz.logIt(log, "semaphores: consume = "
                                +consume.availablePermits()+" | producer = "+produce.availablePermits());
                        Utilz.logIt(log, "memory[cuurent] size = " + memory[current].length);
                        blockLocks[current].unlock();
                        return; //mission compelete
                    }
                }
                //seemed busy - try to lock again
                tryCount--;
            }
            //busy or full block
            current++;
            if(current == blockCount) current = 0;
        }
    }

    public Partition getConsummable() throws InterruptedException {
        Utilz.logIt(log, "a consumer call for a block and cuurent consume semaphore is = "+consume.availablePermits());
        consume.acquire();
        int current = 0;
        while(true){
            boolean moreTry = true;
            int tryCount = MAX_TRY_COUNT;
            while(moreTry && tryCount > 0){
                if(blockLocks[current].tryLock()){
                    //lock acquired
                    if(status[current]==-1){
                        //empty block
                        blockLocks[current].unlock();
                        moreTry = false;
                    }else{
                        //full block found
                        Partition partition = new Partition(status[current], 
                                    Arrays.copyOf(memory[current], memory[current].length));
                        Utilz.logIt(log, "full block found: block size = "+memory[current].length);
                        status[current] = -1;
                        produce.release();
                        Utilz.logIt(log, "partition["+partition.index+"] at buffer["+current+"] removed");
                        Utilz.logIt(log, "semaphores: consume = "
                                +consume.availablePermits()+" | producer = "+produce.availablePermits());
                        blockLocks[current].unlock();
                        return partition;
                    }
                }
                //seemed busy - try to lock again
                tryCount--;
            }
            //busy or empty block
            current++;
            if(current == blockCount) current = 0;
        }
    }

}