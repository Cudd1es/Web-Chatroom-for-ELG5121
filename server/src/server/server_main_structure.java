package server;

import java.io.*;
import java.net.*;


public class server_main_structure extends Thread {
    ServerSocket serverSocket = null; // 创建服务器端套接字

    public boolean bServerIsRunning = false;
    private final int SERVER_PORT = 443;// 定义服务器端口号

    public server_main_structure() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT); // 启动服务
            bServerIsRunning = true;

            server_frame serverFrame = new server_frame();
            getServerIP(); // 得到并显示服务器端IP
            System.out.println("Server port is:" + SERVER_PORT);
            serverFrame.taLog.setText("Server started...");
            while (true) {
                Socket socket = serverSocket.accept(); // 监听客户端的连接请求，并返回客户端socket
                new server_process(socket, serverFrame); // 创建一个新线程来处理与该客户的通讯
            }
        } catch (BindException e) {
            System.out.println("Port occupied....");
            System.out.println("Please kill the process and restart it");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("[ERROR] Cound not start server." + e);
        }

        this.start(); // 启动线程
    }

    // 获取服务器的主机名和IP地址
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

    // main方法，实例化服务器端程序
    public static void main(String args[]) {
       new server_main_structure();
    }
}
