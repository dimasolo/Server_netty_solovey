package com.dimasolovey.start_server;


import com.dimasolovey.server_netty.ServerNetty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Dmitry on 7/18/2015.
 */

/*
This class starts the server. Contains main() method
 */

public class StartServer extends Thread {
    // Port, instance of server class and flag
    private static int port;
    private static ServerNetty server;
    private static boolean flag = true;

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader((new InputStreamReader(System.in)));
        while(flag) {
            System.out.println("Enter the port number:");
            try {
                port = Integer.parseInt(reader.readLine());
                flag = false;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid port...Try again");
                flag = true;
            }
        }
        new StartServer().start();
        System.out.println("Server is running.... Port " + port);
        System.out.println("To stop the server, enter \"stop\"");
        try {
            // Creating server
            server = new ServerNetty(port);
            // Starting the server
            server.start();
        } catch (Exception ex) {
            System.out.println("Error! The port is busy");
            System.exit(1);
        }
    }

    @Override
    public void run() {
            while(true) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String input = null;
                try {
                    input = reader.readLine();
                } catch(IOException ex) {
                    ex.printStackTrace();
                }
                if (input != null) {
                    if (input.equals("stop")) {
                        System.out.println("Shutting down the server!");
                        server.stop();
                        System.out.println("Server is stopped");
                        System.exit(0);
                    }
                }
           }
    }
}