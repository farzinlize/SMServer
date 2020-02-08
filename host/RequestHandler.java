package host;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class RequestHandler implements Runnable{
    private Server server;
    private ServerSocket requestServerSocket;
    private int requestPort;

    public RequestHandler(Server server, int requestPort) {
        this.server = server;
        this.requestPort = requestPort;
        try {
            requestServerSocket = new ServerSocket(requestPort);
        } catch (IOException e) {
            System.out.println("[ERROR][FetchHandler] IOExeption:" + e.getMessage());
        }
        server.log.log(Level.CONFIG, "Request Handler running");
    }

    @Override
    public void run(){
        while(true){
            server.log.log(Level.INFO, "[RequestHandler] wait on port " + requestPort);
            try {
                Socket request = this.requestServerSocket.accept();
                server.log.log(Level.INFO, "[RequestHandler] connection accepted");
                server.onRequest(request);
            } catch (IOException e) {
                server.log.log(Level.INFO, "[RequestHandler] failed on accept");
            }
        }
    }

    public int getPort(){
        return requestPort;
    }

}