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
            // TODO: report error
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
                System.out.println("null here");
                return;
            }
            output.writeInt(partition.index);
            output.writeInt(partition.data.length);
            output.write(partition.data);
            working = input.readBoolean();
        }
    }

}