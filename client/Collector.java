package client;

import fuzzy.FuzzyException;

public class Collector{

    private byte[] result;
    private int blockSize;
    private int consumerCount;
    private Integer holes;

    public Collector(int byteCount, int blockSize, int blockCount, int consumerCount){
        this.blockSize = blockSize;
        this.holes = blockCount;
        this.consumerCount = consumerCount;

        this.result = new byte[byteCount];
    }

    public boolean place(int index, byte[] data){
        int i = 0;
        while(i<blockSize && (i+index*blockSize) < result.length){
            result[i+index*blockSize] = data[i];
            i++;
        }
        boolean stillWork;
        synchronized(holes){
            holes--;
            stillWork = (holes >= consumerCount);
        }
        return stillWork;
    }

    public byte[] getResult() throws FuzzyException {
        if(holes != 0) throw new FuzzyException("not ready", holes);
        return result;
    }

    public int getBlockSize(){
        return blockSize;
    }

}