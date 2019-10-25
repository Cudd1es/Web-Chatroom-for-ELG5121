//package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.JTextArea;


public class MainchatThread extends Thread{
    private static DataOutputStream dout;
    private InputStream ins;
    private BufferedReader brd;
    private int port;
    private String ip;
    private JTextArea jta;
    private Graphics g;
    private DataInputStream dis;
    private Socket socket;


    public MainchatThread(String ip,int port,JTextArea jta,Graphics g,InputStream ins){
        this.jta=jta;
        this.ip=ip;
        this.port=port;
        this.g=g;
        this.ins=ins;
    }

    public boolean runing(){
        try{
            socket=new Socket(this.ip,this.port);
            System.out.println("���ӳɹ�");
            InputStream ins=socket.getInputStream();
            OutputStream out=socket.getOutputStream();
            dis=new DataInputStream(ins);
            dout=new DataOutputStream(out);
        return true;
        }catch (Exception e){
            e.printStackTrace();
        }return false;
        }
    //ͼƬ�Ķ�ȡ����
public void readPic(){
        int rgb;
    try {
        rgb = dis.readInt();
        int i=dis.readInt();
        int j=dis.readInt();
        Color color =new Color(rgb);
        g.setColor(color);
        g.drawLine(i,j,i,j);

    } catch (IOException e) {

        e.printStackTrace();
    }
    }
    //������Ϣ
    public void sendMsg(String msg){
        try{
            msg+="\r\n";
            dout.write(msg.getBytes());
            dout.flush();
        }catch(Exception ef){}
}
    public static void sendbyte(int i){
        try{
            dout.writeInt(i);
            dout.flush();
        }catch(Exception ef){}
}

    //��ȡ��������Ϣ�ķ���
    public void readMsg(){
        try{
            brd=new BufferedReader(new InputStreamReader(ins));
            String input=brd.readLine();
            jta.append(input +"\r\n");
        }catch(Exception ef){}
}

    public void readline(){
        try{
                int x1=dis.readInt();
                int y1=dis.readInt();
                int x2=dis.readInt();
                int y2=dis.readInt();
                g.drawLine(x1,y1,x2,y2);
        }catch(Exception ef){}
}


    public void processServer(Socket client){
        try{
            ins=client.getInputStream();
            OutputStream out=client.getOutputStream();
            dis = new DataInputStream(ins);
            dout=new DataOutputStream(out);
        int t=dis.readInt();
            while(true){
                //������������1�����ö�ȡ�߶εķ���
            if(t==1){
                readline();
            }
            //��������2�����ý���ͼƬ�ķ���
            if(t==2){
                readPic();
            }
            //������������4�������ý������ֵķ���
            if(t==4){
                readMsg();
            }
            t=dis.readInt();
            }
            }catch(Exception ef){
            ef.printStackTrace();
            }
    }
    public void run(){
        processServer(socket);
}

}
