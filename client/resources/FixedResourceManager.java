package client.resources;

public class FixedResourceManager implements ResourceManager{

    private int fixed;

    public FixedResourceManager(int fixedConsumer){
        this.fixed = fixedConsumer;
    }

    @Override
    public int getConsumerNumber() {
        return fixed;
    }

}