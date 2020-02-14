package host;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.logging.Logger;

import fuzzy.Utilz;
import host.resources.Distributor;
import host.resources.FixedResourceManager;
import host.resources.ResourceManager;
import host.resources.StaticDistributor;

public class Schaduler extends Thread {

    private Logger log;

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
        
        log = Utilz.initialLogger("Schaduler#"+id);

        //inital resource manager
        this.manager = new FixedResourceManager(1, 50, 20);
        this.distributor = new StaticDistributor(log, filePath, 
                manager.getProducerCount(), manager.getBufferBlockSize());
        Utilz.logIt(log, "[Schaduler]["+id+"] initializing manager and distributor: FixedResourceManager & StaticDistributor");

        producers = new Producer[manager.getProducerCount()];
        shared = new Buffer(manager.getBufferBlockSize(), manager.getBufferBlockCount());
        Utilz.logIt(log, "[Schaduler]["+id+"] run with ["+producers.length+"] producers and and buffer block count of:"
                 + manager.getBufferBlockCount() + " (block size = "+manager.getBufferBlockSize()+")");

        // report client file info
        DataOutputStream output = new DataOutputStream(requestSocket.getOutputStream());
        output.writeInt(distributor.fileByteCount());
        output.writeInt(distributor.blockCount());
        output.writeInt(manager.getBufferBlockSize());
        Utilz.logIt(log, "[Schaduler]["+id+"] report file info to client: file byte size = "+distributor.fileByteCount()
                + " | block count = "+distributor.blockCount());

        initalAgents(requestSocket);

        // end of request conversation
        requestSocket.close();
        Utilz.logIt(log, "[Schaduler]["+id+"] request Socket terminated");
    }

    @Override
    public void run() {
        // start threads (agents started before in initalAgents)
        for (int i = 0; i < producers.length; i++) {
            Producer newProducer = new Producer(i, this, shared);
            producers[i] = newProducer;
            producers[i].start();
        }
        
        Utilz.logIt(log, "[Schaduler]["+id+"] workers started their job - join and wait for them");
        // wait for child thread
        try {
            this.joinChilds();
        } catch (InterruptedException e) {
            // TODO: Report error
        }

        //report to server
        Utilz.logIt(log, "[Schaduler]["+id+"] all workers terminated - calling Server request done");
        server.onRequestDone(this.id);
    }

    public void initalAgents(Socket socket) {
        try {
            Utilz.logIt(log, "[Schaduler]["+id+"] initializing agents with request socket");
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            //read requested number of agents for consumers on client in server
            int numberOfAgents = input.readInt();
            agents = new Agent[numberOfAgents];
            Utilz.logIt(log, "[Schaduler]["+id+"] requested number of agents for consumer from client: "+numberOfAgents);

            //send back ip for agents
            output.writeUTF("localhost");
            Utilz.logIt(log, "[Schaduler]["+id+"] send back private ip address for agents");

            //send port numbers for requested agents
            for (int i=0;i<numberOfAgents;i++) {
                int port = 2500 + i + id*50;
                output.writeInt(port);
                Agent newAgent = new Agent(i ,port, shared);
                newAgent.start();
                agents[i] = newAgent;
            }
        } catch (IOException e) {
            Utilz.logIt(log, "[Schaduler]["+id+"] initializeig agents failed with IOException");
        }

    }

    public Partition getWork(int producer){
        Utilz.logIt(log, "[Schaduler]["+id+"] Producer#"+producer+" request for work");
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