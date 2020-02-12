package client;

import java.util.Scanner;

import client.database.DataTree;
import client.database.SimpleDataTree;

public class Browser implements Runnable{

    public static final int MAX_ACTIVE_REQUEST = 5;

    private DataTree tree;
    private int activeRequests;
    private Request[] requests;
    private Scanner input;

    public Browser(){
        activeRequests = 0;
        input = new Scanner(System.in);
        requests = new Request[MAX_ACTIVE_REQUEST];

        //TODO: inital data tree
        this.tree = new SimpleDataTree();
    }

    @Override
    public void run() {
        String command = "";
        while(true){
            command = input.next();
            if(command.equals("exit")){
                break;
            }
            else if(command.equals("request")){
                System.out.println("fileTag - ip - port (9876)");
                initialRequest(input.nextInt());
            }
            else if(command.equals("fetch")){
                fetch();
            }
            else if(command.equals("activeCount")){
                System.out.println(activeRequests);
            }
            else{
                System.out.println("unknown command: " + command);
            }
        }
    }

    private void initialRequest(int requestTag){
        if(activeRequests >= MAX_ACTIVE_REQUEST){
            System.out.println("reachs maximum number of requests");
            return;
        }
        int slot = 0;
        while(slot < MAX_ACTIVE_REQUEST){
            if(requests[slot]==null) break;
            slot++;
        }
        Request request = new Request(this, slot, requestTag, input.next(), input.nextInt());
        requests[slot] = request;
        activeRequests++;
        request.start();
    }

    private void fetch(){

    }

    public synchronized void onRespond(int requestIndex, int dataID, byte[] data){
        this.tree.update(dataID, data);
        requests[requestIndex] = null;
        activeRequests--;
    }

}