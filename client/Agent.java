package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Agent extends Thread {

    private Request master;
    private String serverIP;
    private int port;
    private Consumer decoder;

    private DataInputStream input;
    private DataOutputStream output;

    public Agent(Request master, String ip, int port) {
        this.master = master;
        this.port = port;
        this.serverIP = ip;
        this.decoder = new Consumer();
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(serverIP, port);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            //start decoding
            jobLoop();
        } catch (IOException e) {
            // TODO: report error
        }
    }

    private void jobLoop() throws IOException {
        boolean stayAlive = true;
        while(stayAlive){
            int index = input.readInt();
            byte[] data = input.readNBytes(master.getBlockSize());
            byte[] decrypted = decoder.decode(data);
            stayAlive = master.place(index, decrypted);
            output.writeBoolean(stayAlive);
        }
    }

}