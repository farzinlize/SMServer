package client;

import java.util.Scanner;

public class Browser implements Runnable{

    public static final int MAX_ACTIVE_REQUEST = 5;

    private int activeRequests;
    private Request[] requests;
    private Scanner input;

    public Browser(){
        activeRequests = 0;
        input = new Scanner(System.in);
        requests = new Request[MAX_ACTIVE_REQUEST];
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
                initialRequest(input.nextInt());
            }
            else if(command.equals("fetch")){
                fetch();
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
        Request request = new Request(activeRequests, requestTag, input.next(), input.nextInt());
        requests[activeRequests++] = request;
        request.start();
    }

    private void fetch(){

    }

}