package server;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;


// The Server UI.
public class server_frame extends JFrame implements ActionListener {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    JTabbedPane tpServer;

    // 服务器信息面板
    JPanel pnlServer, pnlServerInfo;
    JLabel lblNumber, lblServerName, lblIP, lblPort, lblLog;
    JTextField txtNumber, txtServerName, txtIP, txtPort;
    JButton btnStop, btnSaveLog;
    TextArea taLog;

    // 用户信息面板
    JPanel pnlUser;
    JLabel lblUser;
    @SuppressWarnings("rawtypes")
    JList lstUser;
    JScrollPane spUser;

    // 关于本软件
    JPanel pnlAbout;
    JLabel lblVersionNo, lblAbout;

    @SuppressWarnings("rawtypes")
    public server_frame() {
        super("Web Chat server");
        setTitle("Chatroom Server");
        setSize(510, 510);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();// 在屏幕居中显示
        Dimension fra = this.getSize();
        if (fra.width > scr.width) {
            fra.width = scr.width;
        }
        if (fra.height > scr.height) {
            fra.height = scr.height;
        }
        this.setLocation((scr.width - fra.width) / 2,
                (scr.height - fra.height) / 2);

        btnStop = new JButton("Close(C)");
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                closeServer();
            }
        });
        btnStop.setBackground(Color.DARK_GRAY);
        btnStop.setFont(new Font("Times New Roman", 0, 12));

        // 服务器面板
        pnlServer = new JPanel();
        pnlServer.setLayout(null);
        pnlServer.setBackground(new Color(175, 175, 180));

        lblLog = new JLabel("[server Log]");
        lblLog.setForeground(new Color(125, 125, 125));
        lblLog.setFont(new Font("Times New Roman", Font.BOLD, 15));
        taLog = new TextArea(20, 50);
        taLog.setFont(new Font("Times New Roman", 0, 12));

        btnSaveLog = new JButton("Save Log(S)");
        btnSaveLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                saveLog();
            }
        });
        btnSaveLog.setBackground(Color.DARK_GRAY);
        btnSaveLog.setFont(new Font("Times New Roman", 0, 12));

        lblLog.setBounds(202, 10, 87, 30);
        taLog.setBounds(22, 41, 454, 364);
        btnStop.setBounds(22, 411, 120, 30);
        btnSaveLog.setBounds(356, 411, 120, 30);
        pnlServer.add(lblLog);
        pnlServer.add(taLog);
        pnlServer.add(btnStop);
        pnlServer.add(btnSaveLog);

        lstUser = new JList();
        lstUser.setFont(new Font("Times New Roman", 0, 12));
        lstUser.setVisibleRowCount(17);
        lstUser.setFixedCellWidth(180);
        lstUser.setFixedCellHeight(18);

        // 软件信息
        pnlAbout = new JPanel();
        pnlAbout.setLayout(null);
        pnlAbout.setBackground(new Color(175, 175, 180));
        pnlAbout.setFont(new Font("Times New Roman", 0, 14));

        lblVersionNo = new JLabel("ELG 5121: Web based chatroom");
        lblVersionNo.setFont(new Font("Times New Roman", Font.BOLD, 20));
        lblVersionNo.setForeground(Color.GRAY);

        lblAbout = new JLabel();
        lblAbout.setFont(new Font("Times New Roman", Font.BOLD, 30));
        //lblAbout.setText("");
        lblAbout.setForeground(Color.GRAY);

        lblVersionNo.setBounds(69, 136, 346, 79);
        lblAbout.setBounds(69, 211, 358, 50);

        pnlAbout.add(lblVersionNo);
        pnlAbout.add(lblAbout);

        // 主标签面板
        tpServer = new JTabbedPane(JTabbedPane.TOP);
        tpServer.setBackground(new Color(100, 125, 125));
        tpServer.setFont(new Font("Times New Roman", 0, 14));

        // 用户面板
        pnlUser = new JPanel();
        pnlUser.setLayout(null);
        pnlUser.setBackground(new Color(175, 175, 180));
        pnlUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        lblUser = new JLabel("[Online Users]");
        lblUser.setFont(new Font("Times New Roman", Font.BOLD, 22));
        lblUser.setForeground(Color.GRAY);

        spUser = new JScrollPane();
        spUser.setBackground(new Color(95, 90, 100));
        spUser.setFont(new Font("Times New Roman", 0, 12));
        spUser.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        spUser.getViewport().setView(lstUser);

        pnlUser.setBounds(50, 5, 300, 400);
        lblUser.setBounds(169, 22, 198, 33);
        spUser.setBounds(169, 65, 298, 362);

        // 服务器信息
        pnlServerInfo = new JPanel(new GridLayout(12, 1));
        pnlServerInfo.setBounds(22, 20, 119, 407);
        pnlUser.add(pnlServerInfo);
        pnlServerInfo.setBackground(new Color(175, 175, 180));
        pnlServerInfo.setFont(new Font("Times New Roman", 0, 12));

        lblNumber = new JLabel("User(s) Online");
        lblNumber.setForeground(new Color(125, 125, 125));
        lblNumber.setFont(new Font("Times New Roman", Font.BOLD, 15));
        txtNumber = new JTextField("0", 10);
        txtNumber.setBackground(Color.decode("#808080"));
        txtNumber.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        txtNumber.setEditable(false);

        lblServerName = new JLabel("Server name:");
        lblServerName.setForeground(new Color(125, 125, 125));
        lblServerName.setFont(new Font("Times New Roman", Font.BOLD, 15));
        txtServerName = new JTextField(10);
        txtServerName.setBackground(Color.decode("#808080"));
        txtServerName.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        txtServerName.setEditable(false);

        lblIP = new JLabel("Server IP:");
        lblIP.setForeground(new Color(125, 125, 125));
        lblIP.setFont(new Font("Times New Roman", Font.BOLD, 15));
        txtIP = new JTextField(10);
        txtIP.setBackground(Color.decode("#808080"));
        txtIP.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        txtIP.setEditable(false);

        lblPort = new JLabel("Server Port:");
        lblPort.setForeground(new Color(125, 125, 125));
        lblPort.setFont(new Font("Times New Roman", Font.BOLD, 15));
        txtPort = new JTextField("8888", 10);
        txtPort.setBackground(Color.decode("#808080"));
        txtPort.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        txtPort.setEditable(false);
        pnlServerInfo.add(lblNumber);
        pnlServerInfo.add(txtNumber);
        pnlServerInfo.add(lblServerName);
        pnlServerInfo.add(txtServerName);
        pnlServerInfo.add(lblIP);
        pnlServerInfo.add(txtIP);
        pnlServerInfo.add(lblPort);
        pnlServerInfo.add(txtPort);

        pnlUser.add(lblUser);
        pnlUser.add(spUser);
        tpServer.add("Online Users", pnlUser);

        tpServer.add("Server Info", pnlServer);
        tpServer.add("About", pnlAbout);

        this.getContentPane().add(tpServer);
        setVisible(true);
    }

    protected void closeServer() {
        this.dispose();
    }

    protected void saveLog() {
        try {
            FileOutputStream fileoutput = new FileOutputStream("resourse/log.txt", true);
            String temp = taLog.getText();
            fileoutput.write(temp.getBytes());
            fileoutput.close();
            JOptionPane.showMessageDialog(null, "Saved in log.txt");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void actionPerformed(ActionEvent evt) {
    }

    // 服务器窗口
    public static void main(String[] args) {
        new server_frame();
    }
}
