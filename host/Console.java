package host;

import java.util.Scanner;

import fuzzy.ServerMode;

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
            System.out.println("commands: (e)xit | (s)erver");
            command = input.next();
            if(command.equals("e") || command.equals("exit")){
                input.close();
                return;
            }
            else if(command.equals("s") || command.equals("server")){
                System.out.println("enter server mode -> 1:making log file | 0:running");
                startServer(input.nextInt());
            }
            else{
                System.out.println("unknown command: " + command);
            }
        }
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