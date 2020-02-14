package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.resources.FixedResourceManager;
import client.resources.ResourceManager;
import fuzzy.FuzzyException;
import fuzzy.Utilz;

public class Request extends Thread {

    private Logger log;

    private int id;
    private Browser browser;
    private int dataTag;
    private String serverIP;
    private int requestPort;
    private ResourceManager manager;
    private Agent[] agents;
    private Collector collector;

    public Request(Browser browser, int id, int requestTag, String ip, int requestPort) {
        this.browser = browser;
        this.id = id;
        this.dataTag = requestTag;
        this.serverIP = ip;
        this.requestPort = requestPort;

        try {
            log = Utilz.initialLogger("request#" + id);
        } catch (SecurityException | IOException e) {
            System.out.println("error creating or opening log file");
        }

        Utilz.logIt(log, "initiate request with FixedResource manager with 1 consumers");
        this.manager = new FixedResourceManager(1);

        agents = new Agent[manager.getConsumerNumber()];
    }

    @Override
    public void run() {
        try {
            //inital request
            Utilz.logIt(log, "requst start making connection");
            Socket socket = new Socket(serverIP, requestPort);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            //request file
            Utilz.logIt(log, "connection established - sending requested data tag to server");
            output.writeInt(dataTag);

            //file info as answer
            int byteCount = input.readInt();
            int blockCount = input.readInt();
            int blockSize = input.readInt();
            Utilz.logIt(log, "get information from server (file size = "
                    +byteCount+" and block count = "+blockCount+")");

            collector = new Collector(byteCount, blockSize, blockCount, agents.length);

            //request agents
            output.writeInt(agents.length);

            //ip and port for each agent as answer
            String ip = input.readUTF();
            for(int i=0;i<agents.length;i++){
                int port = input.readInt();
                agents[i] = new Agent(i, this, ip, port);
                agents[i].start();
            }
            
            //end of conversation
            Utilz.logIt(log, "receive connection information for each agent and terminate current socket");
            socket.close();
            
            //wait for agents to finish their job
            this.joinAgents();
            Utilz.logIt(log, "agents finished their jobs");

            //return file to browser as result
            browser.onRespond(id, dataTag, collector.getResult());
        } catch (IOException e) {
            //TODO: report to user
            return ;
        } catch (InterruptedException e) {
            //TODO: report error in log
        } catch (FuzzyException e){
            Utilz.logIt(log, "collector reports there is still holes in result (holes="+e.usefulInt+")", Level.WARNING);
        }
    }

    public boolean place(int index, byte[] data){
        Utilz.logIt(log, "partition ("+index+") is ready to place");
        return collector.place(index, data);
    }

    public int getBlockSize(){
        return collector.getBlockSize();
    }

    private void joinAgents() throws InterruptedException {
        for (Agent agent : agents) {
            agent.join();
        }
    }

}