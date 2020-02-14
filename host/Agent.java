package host;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import fuzzy.Utilz;

public class Agent extends Thread {

    private Logger log;
    private Buffer shared;

    private int port;
    private DataInputStream input;
    private DataOutputStream output;

    public Agent(int id, int port, Buffer buffer) {
        this.port = port;
        this.shared = buffer;

        try {
            log = Utilz.initialLogger("agent-host-" + id);
        } catch (SecurityException | IOException e) {
            System.out.println("error creating or opening agent-host log file");
        }
        Utilz.logIt(log, "Agent-host created");
    }

    @Override
    public void run() {
        Utilz.logIt(log, "Agent-host start running on port = ("+port+")");
        try {
            ServerSocket terminal = new ServerSocket(port);
            Socket connection = terminal.accept();
            input = new DataInputStream(connection.getInputStream());
            output = new DataOutputStream(connection.getOutputStream());

            Utilz.logIt(log, "connection between agents established successfully");

            //start consumming
            jobLoop();

            Utilz.logIt(log, "no more job for agent-host | start terminating connection");
            
            //termination socket and server
            terminal.close();
            connection.close();
        } catch (IOException e) {
            Utilz.logIt(log, "[ERROR] problem making connection", Level.WARNING);
            Utilz.logIt(log, "[EXCEPTION] message = " + e.getMessage(), Level.WARNING);
        }
    }

    public void jobLoop() throws IOException {
        boolean working = true;
        while(working){
            Partition partition;
			try {
                partition = shared.getConsummable();
			} catch (InterruptedException e) {
                // TODO: Report error
                return;
            }
            if(partition == null){
                Utilz.logIt(log, "[FATAL ERROR] partitin is null from buffer");
                return;
            }
            Utilz.logIt(log, "sending partition to other agent (partition index = "
                    +partition.index+") (partition size = "+partition.data.length+")");
            output.writeInt(partition.index);
            output.writeInt(partition.data.length);
            output.write(partition.data);
            working = input.readBoolean();
            Utilz.logIt(log, "receive remaining job signal -> " + working);
        }
    }

}