package host;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

import host.resources.ResourceManager;
import host.utils.Partition;

public class Schaduler extends Thread {

    private int id;
    private Server server;
    private Producer[] producers;
    private Agent[] agents;
    private Buffer shared;
    private ResourceManager manager;
    private File requested;

    public Schaduler(int id, Server server, Socket requestSocket, File requested) {
        this.id = id;
        this.server = server;
        this.requested = requested;

        producers = new Producer[manager.getProducerNumber()];
        shared = new Buffer(manager.getBufferBlockSize(), manager.getBufferBlockNumber());

        initalAgents(requestSocket);
    }

    @Override
    public void run() {
        for (int i=0;i<agents.length;i++) {
            agents[i].start();
        }
        for (int i=0;i<producers.length;i++) {
            Producer newProducer = new Producer(i, this, shared);
            producers[i] = newProducer;
            producers[i].start();
        }
    }

    public void initalAgents(Socket socket) {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            //read requested number of agents for consumers on client in server
            int numberOfAgents = input.readInt();
            agents = new Agent[numberOfAgents];

            //send port numbers for requested agents
            for (int i=0;i<numberOfAgents;i++) {
                int port = 2500 + i + id*50;
                output.writeInt(port);
                Agent newAgent = new Agent(port, shared);
                agents[i] = newAgent;
            }
        } catch (IOException e) {
            server.log.log(Level.WARNING, "[Schaduler] socket failed");
        }

    }

    public Partition getWork(int producer){
        //TODO: job distribution
        return null;
    }

}