package host.resources;

import host.Partition;

public interface Distributor{

    public Partition getPartition(int workerID);
    public int blockCount();
    public int fileByteCount();

}