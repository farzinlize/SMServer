package host;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import fuzzy.Utilz;

public class RequestHandler implements Runnable{

    private boolean alive;

    private Server server;
    private ServerSocket requestServerSocket;
    private int requestPort;

    public RequestHandler(Server server, int requestPort) {
        this.server = server;
        this.requestPort = requestPort;
        try {
            requestServerSocket = new ServerSocket(requestPort);
        } catch (IOException e) {
            System.out.println("[ERROR][RequestHandler] IOExeption:" + e.getMessage());
        }
        Utilz.logIt(server.log, "[RequestHandler] Request Handler running on port" + requestPort);
        this.alive = true;
    }

    @Override
    public void run(){
        while(alive){
            Utilz.logIt(server.log, "[RequestHandler] wait on port " + requestPort);
            try {
                Socket request = this.requestServerSocket.accept();
                Utilz.logIt(server.log, "[RequestHandler] connection accepted");
                DataInputStream input = new DataInputStream(request.getInputStream());
                server.onRequest(request, input.readInt());
            } catch (IOException e) {
                Utilz.logIt(server.log, "[RequestHandler] failed on accept or terminated");
            }
        }
        Utilz.logIt(server.log, "[RequestHandler] terminated");
    }

    public int getPort(){
        return requestPort;
    }

    public void stop() throws IOException {
        this.alive = false;
        requestServerSocket.close();
    }

}