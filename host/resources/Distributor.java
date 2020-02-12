package host.resources;

import host.Partition;

public interface Distributor{

    public Partition getPartition();
    public int blockCount();
    public int fileByteCount();

}