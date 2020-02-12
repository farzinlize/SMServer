package host;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import host.database.DataTree;

import java.util.logging.FileHandler;
import java.util.logging.Level;

public class Server extends Thread{

    public static int MAX_ACTIVE_REQUEST = 2;

    public Logger log;
    private int id;

    private Thread fetchThread;
    private Thread requestThread;
    private FetchHandler fetchHandler;
    private RequestHandler requestHandler;
    private DataTree tree;
    private int activeRequest;

    private Schaduler[] schadulers;


    public Server(int id){
        this.id = id;
        activeRequest = 0;
        schadulers = new Schaduler[MAX_ACTIVE_REQUEST];

        log = Logger.getLogger("server#"+id);
        log.setLevel(Level.CONFIG);
        //start logger
        try {
            FileHandler fh = new FileHandler("server#" + id + ".log");
            fh.setFormatter(new SimpleFormatter());
            log.addHandler(fh);
		} catch (SecurityException | IOException e) {
            System.out.println("problem opening log file");
		}
        log.log(Level.CONFIG, "Server running id:" + id);

        //start listening on FETCH and REQUEST ports
        int fetchPort = 9875 + id*2;
        int requestPort = 9876 + id*2;
        fetchHandler = new FetchHandler(this, fetchPort);
        requestHandler = new RequestHandler(this, requestPort);
    }

    @Override
    public void run(){
        requestThread = new Thread(requestHandler);
        fetchThread = new Thread(fetchHandler);
        fetchThread.start();
        log.log(Level.CONFIG, "Fetch handler started on " + fetchHandler.getPort());
        requestThread.start();
        log.log(Level.CONFIG, "Request handler started on " + requestHandler.getPort());
    }

    public void onRequest(Socket requestSocket, int requestedFile){
        if(activeRequest >= MAX_ACTIVE_REQUEST){
            log.log(Level.WARNING, "[Server]["+id+"] maximum number of requests reached");
            //TODO: send back server busy code to client
            return;
        }
        int slot = 0;
        while(slot < MAX_ACTIVE_REQUEST){
            if(schadulers[slot]==null) break;
            slot++;
        }
        Schaduler schaduler;
        try {
            schaduler = new Schaduler(slot, this, requestSocket, 
                        this.tree.getFilePath(requestedFile));
        } catch (IOException e) {
            // TODO: send back server error
            return ;
        }
        schadulers[slot] = schaduler;
        activeRequest++;
        schaduler.start();
    }

    public void onFetch(Socket fetchSocket){
        
    }

    public void onRequestDone(int requestId){
        schadulers[requestId] = null;
        activeRequest--;
    }

}