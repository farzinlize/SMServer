package client;

public class Collector{

    private byte[] result;
    private int byteCount;
    private int blockSize;
    private int blockCount;
    private int consumerCount;
    private Integer holes;

    public Collector(int byteCount, int blockSize, int blockCount, int consumerCount){
        this.blockCount = blockCount;
        this.blockSize = blockSize;
        this.byteCount = byteCount;
        this.holes = blockCount;
        this.consumerCount = consumerCount;

        this.result = new byte[byteCount];
    }

    public boolean place(int index, byte[] data){
        for(int i=0;i<blockSize;i++){
            result[i+index*blockSize] = data[i];
        }
        boolean stillWork;
        synchronized(holes){
            holes--;
            stillWork = (holes >= consumerCount);
        }
        return stillWork;
    }

    public byte[] getResult() throws Exception {
        if(holes != 0) throw new Exception("not ready");
        return result;
    }

    public int getBlockSize(){
        return blockSize;
    }

}