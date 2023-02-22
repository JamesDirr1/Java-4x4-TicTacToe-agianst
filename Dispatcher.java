//James Dirr CSC-460-001
//Dispatcher that hands off connections to a new server thread to allow for multiple games at once

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Dispatcher {

    static ServerSocket port;

    public static void main(String[] args) throws IOException {
        Server_thread game;
        Socket s;

        try{
            port = new ServerSocket(8787);
            System.out.println("Waiting for connection");

        } catch (IOException e) {
            System.out.println("failed to connect");
        }
        while(true){
            s = port.accept();
            System.out.println("RECEIVED REQUEST");
            Server_thread server = new Server_thread(s);
            server.start();
            System.out.println("game started");
        }
    }
}
