package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import client.resources.ResourceManager;

public class Request extends Thread {

    private int id;
    private int dataTag;
    private String serverIP;
    private int requestPort;
    private ResourceManager manager;
    private Agent[] agents;

    public Request(int id, int requestTag, String ip, int requestPort) {
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

            output.writeInt(dataTag);
            //int fileSize = input.readInt();

            output.writeInt(agents.length);

            for(int i=0;i<agents.length;i++){
                int port = input.readInt();
                agents[i] = new Agent();
            }
        } catch (IOException e) {
            //TODO: report to user
            return ;
        }
    }

}