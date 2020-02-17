package host;

import java.util.Scanner;

import fuzzy.ServerMode;
import fuzzy.Utilz;

public class Console implements Runnable{

    public static final String APP_VERSION = "0.1";
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
        System.out.println("Welcome to (S)ecure (M)ultiproccessed Server "+
            "| SMServer version "+APP_VERSION+
            " | max number of active server = "+MAX_ACTIVE_SERVER);
        String command = "";
        while(true){
            System.out.println("commands: (e)xit | (s)erver | (t)erminate | terminate-(a)ll | (c)lear-log");
            command = input.next();
            if(command.equals("e") || command.equals("exit")){
                input.close();
                terminateAll();
                return;
            }
            else if(command.equals("s") || command.equals("server")){
                System.out.println("enter server mode -> 1:making log file | 0:running");
                startServer(input.nextInt());
            }
            else if(command.equals("t") || command.equals("terminate")){
                terminate(input.nextInt());
            }
            else if(command.equals("a") || command.equals("terminate-all")){
                terminateAll();
            }
            else if(command.equals("c") || command.equals("clear-log")){
                Utilz.deleteLogs();
            }
            else{
                System.out.println("unknown command: " + command);
            }
        }
    }

    private void terminate(int slot){
        if(servers[slot]==null) return;
        servers[slot].onStop();
    }

    private void terminateAll(){
        for(int slot=0;slot<servers.length;slot++)
            terminate(slot);
    }

    private void startServer(int modeId){
        if(activeServers >= MAX_ACTIVE_SERVER){
            System.out.println("reachs maximum number of servers");
            return ;
        }
        ServerMode mode = ServerMode.DEBUG;
        if(modeId == 0) mode = ServerMode.LUANCH;
        Server server = new Server(mode, activeServers);
        servers[activeServers++] = server;
        server.start();
    }

}