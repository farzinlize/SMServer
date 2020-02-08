package host;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Agent extends Thread {

    private Buffer shared;

    private int port;
    private DataInputStream input;
    private DataOutputStream output;

    public Agent(int port, Buffer buffer) {
        this.port = port;
        this.shared = buffer;
    }

    @Override
    public void run() {
        try {
            ServerSocket terminal = new ServerSocket(port);
            Socket connection = terminal.accept();
            input = new DataInputStream(connection.getInputStream());
            output = new DataOutputStream(connection.getOutputStream());
            terminal.close();

            //start consumming
            jobLoop();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void jobLoop() throws IOException {
        boolean working = true;
        while(working){
            shared.waitSemaphoreConsume();
            String data = shared.getConsummable();
            output.writeUTF(data);
            working = input.readBoolean();
        }
    }

}