package host.utils;

public class Partition{
    
    public byte[] data;
    public int index;

    public Partition(int index, byte[] data){
        this.data = data;
        this.index = index;
    }

}