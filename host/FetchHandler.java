package host;

import java.io.IOException;
import java.net.ServerSocket;

public class FetchHandler implements Runnable{
    private Server server;
    private ServerSocket fetchServerSocket;
    private int fetchPort;

    public FetchHandler(Server server, int fetchPort) {
        this.server = server;
        this.fetchPort = fetchPort;
        try {
            fetchServerSocket = new ServerSocket(fetchPort);
        } catch (IOException e) {
            System.out.println("[ERROR][FetchHandler] IOExeption:" + e.getMessage());
        }
    }

    public int getPort(){
        return fetchPort;
    }

    @Override
    public void run(){

    }


}