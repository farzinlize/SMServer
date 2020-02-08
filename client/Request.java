package client;

public class Request extends Thread{

    private int id;
    private int dataTag;
    private int requestPort;

    public Request(int id, int requestTag, int requestPort){
        this.id = id;
        this.dataTag = requestTag;
        this.requestPort = requestPort;
    }

}