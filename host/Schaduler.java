package host;

import java.net.Socket;

import host.resources.ResourceManager;

public class Schaduler extends Thread{

    private int id;
    private Server server;
    private Producer[] producers;
    private Agent[] agents;
    private Buffer shared;
    private ResourceManager manager;

    public Schaduler(int id, Server server, Socket requestSocket){
        this.id = id;
        this.server = server;
        
        int producersNumber = manager.getProducerNumber();
        int agentsNumber = manager.getAgentNumber();

        producers = new Producer[producersNumber];
        agents = new Agent[agentsNumber];
    }
    
    @Override
    public void run(){
        
    }

    public void initalProcess(){

    }

}