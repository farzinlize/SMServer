package host;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import fuzzy.ServerMode;
import fuzzy.Utilz;
import host.database.DataTree;
import host.database.SimpleDataTree;

public class Server extends Thread {

    public static int MAX_ACTIVE_REQUEST = 2;

    public Logger log;
    public ServerMode mode;
    public int id;

    private Thread fetchThread;
    private Thread requestThread;
    private FetchHandler fetchHandler;
    private RequestHandler requestHandler;
    private DataTree tree;
    private int activeRequest;

    private Schaduler[] schadulers;

    public Server(ServerMode mode, int id) {
        this.id = id;
        this.mode = mode;
        activeRequest = 0;
        schadulers = new Schaduler[MAX_ACTIVE_REQUEST];

        // start data tree
        this.tree = new SimpleDataTree();

        if(mode.equals(ServerMode.DEBUG)){
            try {
                log = Utilz.initialLogger("server#" + id);
            } catch (SecurityException | IOException e) {
                System.out.println("problem creating or opening log file");
            }
            Utilz.logIt(log, "Server running id:" + id);
        }

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
        Utilz.logIt(log, "Fetch handler started on " + fetchHandler.getPort());
        requestThread.start();
        Utilz.logIt(log, "Request handler started on " + requestHandler.getPort());
    }

    public void onRequest(Socket requestSocket, int requestedFile){
        Utilz.logIt(log, "[Server] request initialized for file id = " + requestedFile);
        if(activeRequest >= MAX_ACTIVE_REQUEST){
            Utilz.logIt(log, "[Server]["+id+"] maximum number of requests reached");
            //TODO: send back server busy code to client
            return;
        }
        int slot = 0;
        while(slot < MAX_ACTIVE_REQUEST){
            if(schadulers[slot]==null) break;
            slot++;
        }
        Utilz.logIt(log, "[Server] choosen slot in server schaduler list is " + slot);
        Schaduler schaduler;
        try {
            schaduler = new Schaduler(slot, this, requestSocket, 
                        this.tree.getFilePath(requestedFile));
        } catch (IOException e) {
            Utilz.logIt(log, "[Server]["+id+"] Could't initail Schaduler - IOExeption Happend");
            // TODO: send back server error
            return ;
        }
        Utilz.logIt(log, "[Server] schaduler successfully created and statring");
        schadulers[slot] = schaduler;
        activeRequest++;
        schaduler.start();
    }

    public void onFetch(Socket fetchSocket){
        
    }

    public void onRequestDone(int requestId){
        Utilz.logIt(log, "[Server] request done successfully with request id = " + requestId);
        schadulers[requestId] = null;
        activeRequest--;
    }

}