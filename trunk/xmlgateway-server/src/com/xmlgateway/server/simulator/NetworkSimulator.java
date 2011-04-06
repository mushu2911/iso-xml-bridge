/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xmlgateway.server.simulator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author ifnu
 */
public class NetworkSimulator {
   public static void main(String[] args) throws IOException {
       while(true){
        int port = 9090;
        ServerSocket server = new ServerSocket(port);

        System.out.println("Starting debugger");

        Socket socket = server.accept();

        InputStreamReader reader = new InputStreamReader(socket.getInputStream());

        int data;
        int counter = 1;
        StringBuffer buffer = new StringBuffer();
        while((data = reader.read()) != -1) {
            System.out.println("Byte "+ (counter++) +" : " + (char)data );
            buffer.append((char)data);
        }
        System.out.println(buffer.toString());
        reader.close();
        socket.close();
       }
    }
}
