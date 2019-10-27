package server;

import java.io.*;
import java.net.*;


public class server_main_structure extends Thread {
    ServerSocket serverSocket = null;

    public boolean bServerIsRunning = false;
    private final int SERVER_PORT = 8888;

    public server_main_structure() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            bServerIsRunning = true;

            server_frame serverFrame = new server_frame();
            getServerIP();
            System.out.println("Server port is:" + SERVER_PORT);
            serverFrame.taLog.setText("Server started...");
            while (true) {
                Socket socket = serverSocket.accept();
                new server_process(socket, serverFrame);
            }
        } catch (BindException e) {
            System.out.println("Port occupied....");
            System.out.println("Please kill the process and restart it");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("[ERROR] Cound not start server." + e);
        }

        this.start();
    }

    public void getServerIP() {
        try {
            InetAddress serverAddress = InetAddress.getLocalHost();
            Socket s = new Socket("www.google.com", 80);
            //byte[] ipAddress = serverAddress.getAddress();
            //String server_ip = (serverAddress.getLocalHost()).grim();


            server_frame serverFrame = new server_frame();
            serverFrame.txtServerName.setText("Admin's server");
            serverFrame.txtIP.setText(s.getLocalAddress().getHostAddress());
            serverFrame.txtPort.setText("8888");

            System.out.println("Server IP is:" + s.getLocalAddress().getHostAddress());
            s.close();
            //    System.out.println("Server IP is:" + (ipAddress[0] & 0xff) + "."
            //           + (ipAddress[1] & 0xff) + "." + (ipAddress[2] & 0xff) + "."
            //           + (ipAddress[3] & 0xff));
        } catch (Exception e) {
            System.out.println("###Cound not get Server IP." + e);
        }
    }

    public static void main(String args[]) {
        new server_main_structure();
    }
}
