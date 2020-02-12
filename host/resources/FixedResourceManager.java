package host.resources;

public class FixedResourceManager implements ResourceManager{

    private int producerCount;
    private int bufferBlockSize;
    private int bufferBlockCount;

    public FixedResourceManager(int producerCount, int bufferBlockSize, int bufferBlockCount){
        this.bufferBlockCount = bufferBlockCount;
        this.bufferBlockSize = bufferBlockSize;
        this.producerCount = producerCount;
    }

    @Override
    public int getProducerCount() {
        return producerCount;
    }

    @Override
    public int getBufferBlockSize() {
        return bufferBlockSize;
    }

    @Override
    public int getBufferBlockCount() {
        return bufferBlockCount;
    }
    
}