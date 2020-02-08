package host;

import java.util.Scanner;

public class Console implements Runnable{

    public static final int MAX_ACTIVE_SERVER = 1;

    private int activeServers;
    private Server[] servers;
    private Scanner input;

    public Console(){
        activeServers = 0;
        input = new Scanner(System.in);
        servers = new Server[MAX_ACTIVE_SERVER];
    }

    @Override
    public void run() {
        String command = "";
        while(true){
            command = input.next();
            if(command.equals("exit")){
                break;
            }
            else if(command.equals("server")){
                startServer();
            }
            else{
                System.out.println("unknown command: " + command);
            }
        }
    }

    private void startServer(){
        if(activeServers >= MAX_ACTIVE_SERVER){
            System.out.println("reachs maximum number of servers");
            return ;
        }
        Server server = new Server(activeServers);
        servers[activeServers++] = server;
        server.start();
    }

}