package host.resources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Logger;

import fuzzy.Utilz;
import host.Partition;

public class StaticDistributor implements Distributor {

    private Logger log;

    private byte[] data;
    private int blockCount;
    private int[] workersJobsIndex;
    private int blockSize;

    public StaticDistributor(Logger log, Path filePath, int workerCount, int blockSize) throws IOException {
        this.log = log;
        this.blockSize = blockSize;
        data = Files.readAllBytes(filePath);

        Utilz.logIt(log, "[DISTRIBUTOR] readed file size (byte count) = " + data.length);

        blockCount = (data.length + blockSize - 1) / blockSize;
        workersJobsIndex = new int[workerCount];

        int jobPerWorker = (blockCount + workerCount - 1) / workerCount;
        for(int worker = 0;worker < workerCount;worker++){
            workersJobsIndex[worker] = worker * jobPerWorker;
        }
    }

    @Override
    public Partition getPartition(int workerID) {
        int blockIndex = workersJobsIndex[workerID]++;
        if(blockIndex >= blockCount) return null; //no job remained for this worker
        int startIndex = blockIndex*blockSize;
        int endIndex = (blockIndex+1)*blockSize;
        if(endIndex >= data.length) {endIndex = data.length;}
        byte[] partitionData = new byte[endIndex - startIndex];
        partitionData = Arrays.copyOfRange(data, startIndex, endIndex);
        Utilz.logIt(log, "[DISTRIBUTOR] send partition with size = "+partitionData.length+" and index = " + blockIndex);
        return new Partition(blockIndex, partitionData);
    }

    @Override
    public int blockCount() {
        return blockCount;
    }

    @Override
    public int fileByteCount() {
        return data.length;
    }

    

}