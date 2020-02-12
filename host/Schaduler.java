package host;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.logging.Level;

import host.resources.Distributor;
import host.resources.FixedResourceManager;
import host.resources.ResourceManager;
import host.resources.StaticDistributor;

public class Schaduler extends Thread {

    private int id;
    private Server server;
    private Producer[] producers;
    private Agent[] agents;
    private Buffer shared;
    private ResourceManager manager;
    private Distributor distributor;

    public Schaduler(int id, Server server, Socket requestSocket, Path filePath) throws IOException {
        this.id = id;
        this.server = server;
        
        //inital resource manager
        this.manager = new FixedResourceManager(5, 50, 100);
        this.distributor = new StaticDistributor(filePath, 
                manager.getProducerCount(), manager.getBufferBlockSize());

        producers = new Producer[manager.getProducerCount()];
        shared = new Buffer(manager.getBufferBlockSize(), manager.getBufferBlockCount());

        // report client file info
        DataOutputStream output = new DataOutputStream(requestSocket.getOutputStream());
        output.writeInt(distributor.fileByteCount());
        output.writeInt(distributor.blockCount());
        output.writeInt(manager.getBufferBlockSize());

        initalAgents(requestSocket);

        // end of request conversation
        requestSocket.close();
    }

    @Override
    public void run() {
        // start threads
        for (int i = 0; i < agents.length; i++) {
            agents[i].start();
        }
        for (int i = 0; i < producers.length; i++) {
            Producer newProducer = new Producer(i, this, shared);
            producers[i] = newProducer;
            producers[i].start();
        }

        // wait for child thread
        try {
            this.joinChilds();
        } catch (InterruptedException e) {
            // TODO: Report error
        }

        //report to server
        server.onRequestDone(this.id);
    }

    public void initalAgents(Socket socket) {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            //read requested number of agents for consumers on client in server
            int numberOfAgents = input.readInt();
            agents = new Agent[numberOfAgents];

            //send back ip for agents
            output.writeUTF("localhost");

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
        return this.distributor.getPartition(producer);
    }

    private void joinChilds() throws InterruptedException {
        for (Agent agent : agents) {
            agent.join();
        }
        for (Producer producer : producers){
            producer.join();
        }
    }

}