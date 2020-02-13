package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import client.encryption.NoEncryption;
import fuzzy.Utilz;

public class Agent extends Thread {

    private Logger log;

    private Request master;
    private String serverIP;
    private int port;
    private Decoder decoder;

    private DataInputStream input;
    private DataOutputStream output;

    public Agent(int id, Request master, String ip, int port) {
        this.master = master;
        this.port = port;
        this.serverIP = ip;

        try {
            log = Utilz.initialLogger("agent-client-" + id);
        } catch (SecurityException | IOException e) {
            System.out.println("error openning agent-client log file");
        }

        Utilz.logIt(log, "agent-client created with NoEncryption decoder");
        this.decoder = new NoEncryption();
    }

    @Override
    public void run() {
        try {
            Utilz.logIt(log, "start establishing agent-to-agent connection");
            Socket socket = new Socket(serverIP, port);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            //start decoding
            jobLoop();

            //end of process
            socket.close();
            Utilz.logIt(log, "end of agent-client life | socket closed");
        } catch (IOException e) {
            Utilz.logIt(log, "[ERROR] agent-to-agent socket issues a problem");
        }
    }

    private void jobLoop() throws IOException {
        boolean stayAlive = true;
        while(stayAlive){
            int index = input.readInt();
            int blockSize = input.readInt();
            Utilz.logIt(log, "start recieveing partition with index = "
                    +index+" and block size of:"+blockSize);
            byte[] data = input.readNBytes(blockSize);
            byte[] decrypted = decoder.decode(data);
            stayAlive = master.place(index, decrypted);
            output.writeBoolean(stayAlive);
            Utilz.logIt(log, "place partition and check for remaining work signal -> "+stayAlive);
        }
    }

}