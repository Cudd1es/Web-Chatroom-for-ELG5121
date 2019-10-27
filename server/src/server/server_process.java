package server;

import java.io.*;
import java.net.*;
import java.util.*;

class server_process extends Thread {
    private Socket socket = null;

    private BufferedReader in;
    private PrintWriter out;

    @SuppressWarnings("rawtypes")
    private static Vector onlineUser = new Vector(10, 5);
    @SuppressWarnings("rawtypes")
    private static Vector socketUser = new Vector(10, 5);

    private String strReceive, strKey;
    private StringTokenizer st;

    private final String USERLIST_FILE = "resourse/user.txt";
    private server_frame sFrame = null;

    public server_process(Socket client, server_frame frame) throws IOException {
        socket = client;
        sFrame = frame;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
        this.start();
    }

    public void run() {
        try {
            while (true) {
                strReceive = in.readLine();
                st = new StringTokenizer(strReceive, "|");
                strKey = st.nextToken();
                if (strKey.equals("login")) {
                    login();
                } else if (strKey.equals("talk")) {
                    talk();
                } else if (strKey.equals("init")) {
                    freshClientsOnline();
                } else if (strKey.equals("reg")) {
                    register();
                } else if (strKey.equals("expression")) {
                    sendExpression();
                } else if (strKey.equals("picture")) {
                    sendPicture();
                } else if (strKey.equals("printPort")) {
                    print();
                } else if (strKey.equals("sendPort")) {
                    sendFile();
                }

            }
        } catch (IOException e) {
            String leaveUser = closeSocket();
            log("User " + leaveUser + " has logged out" );
            try {
                freshClientsOnline();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("[SYSTEM] " + leaveUser + " leave chatroom!");
            sendAll("talk|[System]" + leaveUser + " has left the chatroom");
        }
    }

    @SuppressWarnings({ "resource", "deprecation" })
    private boolean isExistUser(String name) {
        String strRead;
        try {
            FileInputStream inputfile = new FileInputStream(USERLIST_FILE);
            DataInputStream inputdata = new DataInputStream(inputfile);
            while ((strRead = inputdata.readLine()) != null) {
                StringTokenizer stUser = new StringTokenizer(strRead, "|");
                if (stUser.nextToken().equals(name)) {
                    return true;
                }
            }
        } catch (FileNotFoundException fn) {
            System.out.println("[ERROR] User File has not exist!" + fn);
            out.println("warning|Error in reading/writing files");
        } catch (IOException ie) {
            System.out.println("[ERROR] " + ie);
            out.println("warning|Error in reading/writing files!");
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private boolean isUserLogin(String name, String password) {
        String strRead;
        try {
            FileInputStream inputfile = new FileInputStream(USERLIST_FILE);
            @SuppressWarnings("resource")
            DataInputStream inputdata = new DataInputStream(inputfile);
            while ((strRead = inputdata.readLine()) != null) {
                if (strRead.equals(name + "|" + password)) {
                    return true;
                }
            }
        } catch (FileNotFoundException fn) {
            System.out.println("[ERROR] User File has not exist!" + fn);
            out.println("warning|Error in reading/writing files!");
        } catch (IOException ie) {
            System.out.println("[ERROR] " + ie);
            out.println("warning|Error in reading/writing files!");
        }
        return false;
    }

    private void register() throws IOException {
        String name = st.nextToken();
        String password = st.nextToken().trim();

        if (isExistUser(name)) {
            System.out.println("[ERROR] " + name + " Register fail!");
            out.println("warning|This username has been taken, please change something else");
        } else {
            @SuppressWarnings("resource")
            RandomAccessFile userFile = new RandomAccessFile(USERLIST_FILE, "rw");
            userFile.seek(userFile.length());
            userFile.writeBytes(name + "|" + password + "\r\n");
            log("User " + name + " successfully created" );
            userLoginSuccess(name);
        }
    }

    private void login() throws IOException {
        String name = st.nextToken();
        String password = st.nextToken().trim();
        boolean succeed = false;

        log("User " + name + " logging..." + "\n" + "Password : " + password );
        System.out.println("[USER LOGIN] " + name + ":" + password + ":" + socket);

        for (int i = 0; i < onlineUser.size(); i++) {
            if (onlineUser.elementAt(i).equals(name)) {
                System.out.println("[ERROR] " + name + " is logined!");
                out.println("warning|" + name + " has logged in");
            }
        }
        if (isUserLogin(name, password)) {
            userLoginSuccess(name);
            succeed = true;
        }
        if (!succeed) {
            out.println("warning|" + name + "Log failed, please check your account or password");
            log("User " + name + " log failed！");
            System.out.println("[SYSTEM] " + name + " login fail!");
        }
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    private void userLoginSuccess(String name) throws IOException {
        Date t = new Date();
        out.println("login|succeed");
        sendAll("online|" + name);

        onlineUser.addElement(name);
        socketUser.addElement(socket);

        log("User " + name + " logged successfully " + "\nTime spent:" + t.toLocaleString());

        freshClientsOnline();
        sendAll("talk|[System] Welcome " + name + "into the room");
        System.out.println("[SYSTEM] " + name + " login succeed!");
    }

    private void talk() throws IOException {
        String strTalkInfo = st.nextToken();
        String strSender = st.nextToken();
        String strReceiver = st.nextToken();
        System.out.println("[TALK_" + strReceiver + "] " + strTalkInfo);
        Socket socketSend;
        PrintWriter outSend;

        String strTime = getTime();

        log("User " + strSender + " said to  " + strReceiver + " : " + strTalkInfo);

        if (strReceiver.equals("All")) {
            sendAll("talk|" + strSender + " " + strTime + " : " + strTalkInfo);
        } else {
            if (strSender.equals(strReceiver)) {
                out.println("talk|[System] Please do not speak to yourself");
            } else {
                for (int i = 0; i < onlineUser.size(); i++) {
                    if (strReceiver.equals(onlineUser.elementAt(i))) {
                        socketSend = (Socket) socketUser.elementAt(i);
                        outSend = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(socketSend.getOutputStream())), true);
                        outSend.println("talk|[secret msg]" + strSender + " " + strTime + "：" + strTalkInfo);
                    } else if (strSender.equals(onlineUser.elementAt(i))) {
                        socketSend = (Socket) socketUser.elementAt(i);
                        outSend = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(socketSend.getOutputStream())), true);
                        outSend.println("talk|[secret msg]" + strReceiver + " " + strTime + "：" + strTalkInfo);
                    }
                }
            }
        }
    }

    private String getTime(){
        GregorianCalendar calendar = new GregorianCalendar();
        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        String strHour = "",strMin = "",strSec = "";

        if(hour>0&&hour<10){
            strHour = "0" + hour;
        }else if(hour==0){
            hour = hour +12;
            strHour = "" + hour;
        }

        if(min<10){
            strMin = "0" + min;
        }else{
            strMin = min + "";
        }

        if(sec<10){
            strSec = "0" + sec;
        }else{
            strSec = sec + "";
        }
        String strTime = "(" + strHour + ":" + strMin + ":" + strSec + ")";
        return strTime;
    }

    private void sendExpression() throws IOException {
        String strExpression = st.nextToken();
        String strSender = st.nextToken();
        String strReceiver = st.nextToken();
        System.out.println("[SendExpression_" + strReceiver + "] " + strExpression);
        Socket socketSend;
        PrintWriter outSend;
        new Date();

        String strTime = getTime();

        log("User " + strSender + " said to  " + strReceiver + " : " + strExpression);

        if (strReceiver.equals("All")) {
            sendAll("expression|" + strSender + " " + strTime + " : " + "|" + strExpression + "|");
        } else {
            if (strSender.equals(strReceiver)) {
                out.println("talk|[System] Do not speak to yourself.");
            } else {
                for (int i = 0; i < onlineUser.size(); i++) {
                    if (strReceiver.equals(onlineUser.elementAt(i))) {
                        socketSend = (Socket) socketUser.elementAt(i);
                        outSend = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(socketSend.getOutputStream())), true);
                        outSend.println(
                        "expression|[secret msg]" + strSender + " " + strTime + "：" + "|" + strExpression + "|");
                    } else if (strSender.equals(onlineUser.elementAt(i))) {
                        socketSend = (Socket) socketUser.elementAt(i);
                        outSend = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(socketSend.getOutputStream())), true);
                        outSend.println(
                        "expression|[secret msg]" + strReceiver + " " + strTime + "：" + "|" + strExpression + "|");
                    }
                }
            }
        }
    }

    private void sendPicture() throws IOException {
        String strPicture = st.nextToken();
        String strSender = st.nextToken();
        String strReceiver = st.nextToken();
        System.out.println("[SendPicture_" + strReceiver + "] Send images");
        Socket socketSend;
        PrintWriter outSend;
        new Date();

        String strTime = getTime();

        log("User " + strSender + " sent  " + strReceiver + " images ");

        if (strReceiver.equals("All")) {
            sendAll("picture|" + strSender + " " + strTime + " : " + "|" + strPicture + "|");
        } else {
            if (strSender.equals(strReceiver)) {
                out.println("talk|[System]Do not speak to yourself.");
            } else {
                for (int i = 0; i < onlineUser.size(); i++) {
                    if (strReceiver.equals(onlineUser.elementAt(i))) {
                        socketSend = (Socket) socketUser.elementAt(i);
                        outSend = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(socketSend.getOutputStream())), true);
                        outSend.println("picture|[secret msg]" + strSender + " " + strTime + "：" + "|" + strPicture + "|");
                    } else if (strSender.equals(onlineUser.elementAt(i))) {
                        socketSend = (Socket) socketUser.elementAt(i);
                        outSend = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(socketSend.getOutputStream())), true);
                        outSend.println("picture|[secret msg]" + strReceiver + " " + strTime + "：" + "|" + strPicture + "|");
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void freshClientsOnline() throws IOException {
        String strOnline = "online";
        String[] userList = new String[20];
        String useName = null;

        for (int i = 0; i < onlineUser.size(); i++) {
            strOnline += "|" + onlineUser.elementAt(i);
            useName = " " + onlineUser.elementAt(i);
            userList[i] = useName;
        }

        sFrame.txtNumber.setText("" + onlineUser.size());
        sFrame.lstUser.setListData(userList);
        System.out.println(strOnline);
        out.println(strOnline);
    }

    private void sendAll(String strSend) {
        Socket socketSend;
        PrintWriter outSend;
        try {
            for (int i = 0; i < socketUser.size(); i++) {
                socketSend = (Socket) socketUser.elementAt(i);
                outSend = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketSend.getOutputStream())),
                true);
                outSend.println(strSend);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] send all fail!");
        }
    }

    public void log(String log) {
        String newlog = sFrame.taLog.getText() + "\n" + log;
        sFrame.taLog.setText(newlog);
    }

    private void print() throws IOException {
        String strTalkInfo = st.nextToken();
        String strSender = st.nextToken();
        String strReceiver = st.nextToken();
        System.out.println("[PRINT_" + strReceiver + "] " + strTalkInfo);
        log("User " + strSender + " said to  " + strReceiver + " : " + strTalkInfo);

        if (strReceiver.equals("All")) {
            sendAll("printPort|" + strTalkInfo);
        } else {
            out.println("talk|[System] Some error happened during the real-time drawing");
        }
    }

    private void sendFile() throws IOException {
        String strTalkInfo = st.nextToken();
        String strSender = st.nextToken();
        String strReceiver = st.nextToken();
        System.out.println("[PRINT_" + strReceiver + "] " + strTalkInfo);
        log("User " + strSender + " said to " + strReceiver + " : " + strTalkInfo);

        if (strReceiver.equals("All")) {
            sendAll("sendPort|" + strTalkInfo);
        } else {
            out.println("talk|[System] Errors happened during real-time drawing");
        }
    }

    private String closeSocket() {
        String strUser = "";
        for (int i = 0; i < socketUser.size(); i++) {
            if (socket.equals((Socket) socketUser.elementAt(i))) {
                strUser = onlineUser.elementAt(i).toString();
                socketUser.removeElementAt(i);
                onlineUser.removeElementAt(i);
                try {
                    freshClientsOnline();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendAll("remove|" + strUser);
            }
        }
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("[ERROR] " + e);
        }

        return strUser;
    }
}
