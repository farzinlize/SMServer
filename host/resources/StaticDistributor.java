package host.resources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import host.Partition;

public class StaticDistributor implements Distributor {

    private byte[] data;
    private int blockCount;
    private int[] workersJobsIndex;
    private int blockSize;

    public StaticDistributor(Path filePath, int workerCount, int blockSize) throws IOException {
        data = Files.readAllBytes(filePath);
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
        int endIndex = (blockIndex+1)*blockSize;
        if(endIndex >= data.length) endIndex = data.length;
        return new Partition(blockIndex, 
                Arrays.copyOfRange(data, blockIndex*blockSize, endIndex));
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