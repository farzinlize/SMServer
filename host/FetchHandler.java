package host;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import fuzzy.Utilz;

public class FetchHandler implements Runnable{

    private boolean alive;

    private Server server;
    private ServerSocket fetchServerSocket;
    private int fetchPort;

    public FetchHandler(Server server, int fetchPort) {
        this.server = server;
        this.fetchPort = fetchPort;
        try {
            fetchServerSocket = new ServerSocket(fetchPort);
        } catch (IOException e) {
            Utilz.logIt(server.log, "[ERROR][FetchHandler] IOExeption:" + e.getMessage());
        }
        Utilz.logIt(server.log, "[FetchHandler] fetch handler running on port: "+ fetchPort);
        this.alive = true;
    }

    public int getPort(){
        return fetchPort;
    }

    @Override
    public void run(){
        while(alive){
            Utilz.logIt(server.log, "[FetchHandler] wait on port " + fetchPort);
            try {
                Socket fetch = this.fetchServerSocket.accept();
                Utilz.logIt(server.log, "[FetchHandler] connection accepted");
                server.onFetch(fetch);
            } catch (IOException e) {
                Utilz.logIt(server.log, "[RequestHandler] failed on accept or terminated");
            }
        }
        Utilz.logIt(server.log, "[FetchHandler] terminated");
    }

    public void stop() throws IOException {
        this.alive = false;
        fetchServerSocket.close();
    }

}