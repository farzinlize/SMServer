package host;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

import host.resources.ResourceManager;

public class Schaduler extends Thread {

    private int id;
    private Server server;
    private Producer[] producers;
    private Agent[] agents;
    private Buffer shared;
    private ResourceManager manager;

    public Schaduler(int id, Server server, Socket requestSocket) {
        this.id = id;
        this.server = server;

        producers = new Producer[manager.getProducerNumber()];
        shared = new Buffer(manager.getBufferSize());

        initalAgents(requestSocket);
    }

    @Override
    public void run() {
        for (int i=0;i<agents.length;i++) {
            Agent newAgent = new Agent();
            agents[i] = newAgent;
            agents[i].start();
        }
        for (int i=0;i<producers.length;i++) {
            Producer newProducer = new Producer();
            producers[i] = newProducer;
            producers[i].start();
        }
    }

    public void initalAgents(Socket socket) {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            //first send back requested agents on client (number of producers on server)
            output.writeInt(producers.length);

            //read requested number of agents for consumers on client in server
            int numberOfAgents = input.readInt();
            agents = new Agent[numberOfAgents];
        } catch (IOException e) {
            server.log.log(Level.WARNING, "[Schaduler] socket failed");
        }

    }

}