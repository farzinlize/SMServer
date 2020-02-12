package host;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer{
    
    public static final int MAX_TRY_COUNT = 5;

    private byte[][] memory;
    private int[] status;
    private Lock[] blockLocks;
    private Semaphore produce;
    private Semaphore consume;

    private int blockSize;
    private int blockNumber;

    public Buffer(int blockSize, int blockNumber){
        memory = new byte[blockSize][blockNumber];
        this.blockNumber = blockNumber;
        this.blockSize = blockSize;

        status = new int[blockNumber];
        blockLocks = new Lock[blockNumber];
        for(int i=0;i<blockNumber;i++){
            status[i] = -1;
            blockLocks[i] = new ReentrantLock();
        }

        produce = new Semaphore(blockNumber);
        consume = new Semaphore(0);
    }

    public void putProduced(Partition partition) throws Exception {
        produce.acquire();
        if(partition.data.length > blockSize) throw new Exception("data oversized fatal error");
        int current=0;
        while(current<blockNumber){
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
                        memory[current] = Arrays.copyOf(partition.data, partition.data.length);
                        consume.release();
                        return; //mission compelete
                    }
                }
                //seemed busy - try to lock again
                tryCount--;
            }
            //busy or full block
            current++;
        }
        //empty block not found
        //TODO: report error (semaphore)
    }

    public Partition getConsummable() throws InterruptedException {
        consume.acquire();
        int current = 0;
        while(current<blockNumber){
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
                        status[current] = -1;
                        produce.release();
                        return partition;
                    }
                }
                //seemed busy - try to lock again
                tryCount--;
            }
            //busy or empty block
            current++;
        }
        //full block not found
        //TODO: report error (semaphore)
        return null;
    }

}