package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import client.resources.ResourceManager;

public class Request extends Thread {

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

        agents = new Agent[manager.getConsumerNumber()];
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(serverIP, requestPort);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            //request file
            output.writeInt(dataTag);

            //file info as answer
            int byteCount = input.readInt();
            int blockCount = input.readInt();
            int blockSize = input.readInt();

            collector = new Collector(byteCount, blockSize, blockCount, agents.length);

            //request agents
            output.writeInt(agents.length);

            //ip and port for each agent as answer
            String ip = input.readUTF();
            for(int i=0;i<agents.length;i++){
                int port = input.readInt();
                agents[i] = new Agent(this, ip, port);
                agents[i].start();
            }
            
            socket.close();
            
            //wait for agents to finish their job
            this.joinAgents();

            //return file to browser as result
            browser.onRespond(dataTag, collector.getResult());
        } catch (IOException e) {
            //TODO: report to user
            return ;
        } catch (InterruptedException e) {
            //TODO: report error in log
        } catch (Exception e){
            //TODO: report error
        }
    }

    public boolean place(int index, byte[] data){
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