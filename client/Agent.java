package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import client.encryption.NoEncryption;

public class Agent extends Thread {

    private Request master;
    private String serverIP;
    private int port;
    private Decoder decoder;

    private DataInputStream input;
    private DataOutputStream output;

    public Agent(Request master, String ip, int port) {
        this.master = master;
        this.port = port;
        this.serverIP = ip;
        
        this.decoder = new NoEncryption();
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(serverIP, port);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            //start decoding
            jobLoop();

            //end of process
            socket.close();
        } catch (IOException e) {
            // TODO: report error
        }
    }

    private void jobLoop() throws IOException {
        boolean stayAlive = true;
        while(stayAlive){
            int index = input.readInt();
            int blockSize = input.readInt();
            byte[] data = input.readNBytes(blockSize);
            byte[] decrypted = decoder.decode(data);
            stayAlive = master.place(index, decrypted);
            output.writeBoolean(stayAlive);
        }
    }

}