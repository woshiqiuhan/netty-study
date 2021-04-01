package com.hznu.main;

import com.hznu.displayUI.ClientUI;
import com.hznu.displayUI.ConnectUI;
import com.hznu.displayUI.LoginUI;
import com.hznu.displayUI.MyButton;
import com.hznu.entity.User;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 客户端启动程序
 */
public class ClientMain {
    /**
     * 程序路口，启动一个客户端
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //创建用户窗体界面
                    ClientMain clientMain = new ClientMain();
                    clientMain.clientUI.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static final String ROOT_PATH = "C:\\Users\\12061\\Desktop\\chatroom\\client\\";
    private String MY_FILE_PATH;

    public ClientUI clientUI;

    private JTextField messageTextField;

    //消息列表
    private JTextPane messageList;

    //顶部菜单按钮
    private MyButton editUserInfoButton;
    private MyButton getConnectInfoButton;
    private MyButton loginButton;
    private MyButton logoutButton;
    private MyButton exitButton;

    private MyButton privateChatButton;

    private MyButton sendMessageButton;


    //选择将消息发给全体还是某个用户
    private JComboBox messagesToWhoComboBox;

    private boolean isConnect = false;
    private BufferedReader read;
    private PrintWriter write;
    private Socket socket;
    private Map<String, User> onLineUser = new HashMap<>(); // 所有在线的用户
    private MessageThread messageThread;

    private String userName;
    private String userPassword;
    private int port;
    private String hostIp;

    /**
     * 构造方法，创建用户的界面并为各个组件绑定事件
     */
    public ClientMain() {
        clientUI = new ClientUI();
        //获取客户端界面中的组件
        messageTextField = clientUI.getMessageTextField();
        messageList = clientUI.getMessageList();
        editUserInfoButton = clientUI.getEditUserInfoButton();
        getConnectInfoButton = clientUI.getGetConnectInfoButton();
        loginButton = clientUI.getLoginButton();
        logoutButton = clientUI.getLogoutButton();
        exitButton = clientUI.getExitButton();
        privateChatButton = clientUI.getPrivateChatButton();
        sendMessageButton = clientUI.getSendMessageButton();
        messagesToWhoComboBox = clientUI.getMessagesToWhoComboBox();

        //为各个组件绑定事件
        //得到用户名
        editUserInfoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LoginUI loginUI = new LoginUI();
                loginUI.setVisible(true);

                loginUI.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {

                    }

                    @Override
                    public void windowClosing(WindowEvent e) {

                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                        userName = loginUI.getUserName();
                        userPassword = loginUI.getUserPassword();
                        MY_FILE_PATH = new StringBuilder(ROOT_PATH).append(userName).toString();
                        File file = new File(MY_FILE_PATH);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                    }

                    @Override
                    public void windowIconified(WindowEvent e) {

                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {

                    }

                    @Override
                    public void windowActivated(WindowEvent e) {

                    }

                    @Override
                    public void windowDeactivated(WindowEvent e) {

                    }
                });
            }
        });

        //获取hostIp和port
        getConnectInfoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ConnectUI connectUI = new ConnectUI();
                connectUI.setVisible(true);

                connectUI.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {

                    }

                    @Override
                    public void windowClosing(WindowEvent e) {

                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                        port = connectUI.getPort();
                        hostIp = connectUI.getHostIp();
                    }

                    @Override
                    public void windowIconified(WindowEvent e) {

                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {

                    }

                    @Override
                    public void windowActivated(WindowEvent e) {

                    }

                    @Override
                    public void windowDeactivated(WindowEvent e) {

                    }
                });
            }
        });

        //登录
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        //注销
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        //退出客户端
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //先进行注销
                if (isConnect) {
                    logout();
                }
                //再退出界面
                System.exit(0);
            }
        });

        //私聊
        privateChatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendToOne();
            }
        });

        //给文本框增加回车发送功能
        messageTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });

        //按钮发送
        sendMessageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });
    }

    /**
     * @param port
     * @param hostIp
     * @param name
     * @return
     * @Description 连接服务器
     */
    public synchronized boolean connectServer(int port, String hostIp, String name) {
        try {
            socket = new Socket(hostIp, port); // 根据端口号号和服务器
            write = new PrintWriter(socket.getOutputStream());
            read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //发送客户端的基本信息
            sendMessage(name + "@" + socket.getLocalAddress().toString());
            //开启接收消息的线程
            messageThread = new MessageThread();
            messageThread.start();
            isConnect = true;        //状态改为：已连接

            return true;
        } catch (Exception e) {
            try {
                messageList.getDocument().insertString(messageList.getDocument().getLength(),
                        "与端口号为：" + port + ",   IP地址为：" + hostIp + "的服务器连接失败！\r\n", messageList.getStyle("normal"));
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
            isConnect = false;        //状态为：未连接
            return false;
        }
    }

    /**
     * @return
     * @Description 关闭连接
     */
    public synchronized boolean closeConnect() {
        try {
            sendMessage("CLOSE"); // 发送断开连接命令给服务器
            messageThread.stop(); // 停止接受消息的线程
            // 释放资源
            if (read != null) {
                read.close();
            }
            if (write != null) {
                write.close();
            }
            if (socket != null) {
                socket.close();
            }
            isConnect = false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            isConnect = true;
            return false;
        }
    }

    /**
     * @Description 登录操作
     */
    public synchronized void login() {
        if (isConnect) {
            JOptionPane.showMessageDialog(
                    clientUI,
                    "已经处于连接状态，不能重复连接！",
                    "警告信息",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            boolean flag = connectServer(port, hostIp, userName);
            if (flag == false) {
                JOptionPane.showMessageDialog(
                        clientUI,
                        "与服务器连接失败！",
                        "警告信息",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            clientUI.setTitle(userName);  //设置客户端窗口标题为用户名
            JOptionPane.showMessageDialog(
                    clientUI,
                    "成功连接！",
                    "提示信息",
                    JOptionPane.INFORMATION_MESSAGE);

            messagesToWhoComboBox.addItem(userName);
            messagesToWhoComboBox.revalidate();

            getConnectInfoButton.setEnabled(false);
            editUserInfoButton.setEnabled(false);
            loginButton.setEnabled(false);
            logoutButton.setEnabled(true);
            messageTextField.setEditable(true);
            sendMessageButton.setEnabled(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(clientUI, e.toString());
        }
    }

    /**
     * @Description 注销操作
     */
    public synchronized void logout() {
        if (!isConnect) {
            JOptionPane.showMessageDialog(clientUI, "已经是断开状态了哦！");
            return;
        }
        try {
            boolean flag = closeConnect();        //断开连接
            if (!flag) {
                JOptionPane.showMessageDialog(clientUI, "断开连接发生异常！");
                return;
            }
            JOptionPane.showMessageDialog(clientUI, "断开成功！");

            clientUI.setTitle("聊天室客户端");
            messagesToWhoComboBox.removeAllItems();
            messagesToWhoComboBox.addItem("所有人");
            messagesToWhoComboBox.revalidate();

            getConnectInfoButton.setEnabled(true);
            editUserInfoButton.setEnabled(true);
            loginButton.setEnabled(true);
            logoutButton.setEnabled(false);
            messageTextField.setEditable(false);
            sendMessageButton.setEnabled(false);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(clientUI, e.toString());
        }

    }

    /**
     * @Description 发送信息
     */
    public synchronized void send() {
        if (!isConnect) {
            JOptionPane.showMessageDialog(
                    clientUI,
                    "还没有连接服务器，无法发送消息！",
                    "警告信息",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String message = messageTextField.getText().trim();
        if (message == null || message.equals("")) {
            JOptionPane.showMessageDialog(
                    clientUI,
                    "消息不能为空",
                    "警告信息",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        sendMessage(this.userName + "@" + "ALL" + "@" + message);
        messageTextField.setText(null);
    }

    /**
     * 私聊操作
     */
    public synchronized void sendToOne() {
        if (!isConnect) {
            JOptionPane.showMessageDialog(clientUI, "还没有连接服务器，无法发送消息！");
            return;
        }
        String message = messageTextField.getText().trim();
        if (message == null || message.equals("")) {
            JOptionPane.showMessageDialog(clientUI, "消息不能为空");
            return;
        }
        String name = messagesToWhoComboBox.getSelectedItem().toString();
        sendMessage(clientUI.getTitle() + "@" + "ONE" + "@" + message + "@" + name);
        messageTextField.setText(null);
    }

    /**
     * @param message
     * @Description 发送信息
     */
    public synchronized void sendMessage(String message) {
        write.println(message);
        write.flush();
    }


    /**
     * 内部线程类
     */
    class MessageThread extends Thread {

        // 接收消息线程的构造方法
        public MessageThread() {
            super();
        }

        /**
         * @throws Exception
         * @Description 客户端意外关闭
         */
        public synchronized void closeConnect() throws Exception {
            //清空用户列表
            messagesToWhoComboBox.removeAllItems();
            messagesToWhoComboBox.addItem("所有人");

            // 被动关闭连接释放资源
            if (read != null) {
                read.close();
            }
            if (write != null) {
                write.close();
            }
            if (socket != null) {
                socket.close();
            }
            isConnect = false; // 将状态改为未连接状态
            getConnectInfoButton.setEnabled(true);
            editUserInfoButton.setEnabled(true);
            loginButton.setEnabled(true);
            logoutButton.setEnabled(false);
            messageTextField.setEditable(false);
            sendMessageButton.setEnabled(false);
        }

        @Override
        public void run() { // 不断接受消息
            String message = "";
            while (true) {
                try {
                    message = read.readLine();
                    StringTokenizer st = new StringTokenizer(message, "/@");
                    String command = st.nextToken();
                    if (command.equals("CLOSE")) { // 关闭命令
                        messageList.getDocument().insertString(messageList.getDocument().getLength(),
                                "服务器已关闭！\r\n", messageList.getStyle("normal"));
                        closeConnect(); // 被动关闭连接
                        return; // 结束线程
                    } else if (command.equals("ADD")) { // 有用户上线更新列表
                        String newUserName = null, newUserIp = null;
                        if ((newUserName = st.nextToken()) != null && (newUserIp = st.nextToken()) != null) {
                            User user = new User(newUserName, newUserIp);
                            onLineUser.put(newUserName, user);
                            messagesToWhoComboBox.addItem(newUserName);
                            messagesToWhoComboBox.revalidate();
                        }
                        messageList.getDocument().insertString(messageList.getDocument().getLength(),
                                "[系统通知] " + newUserName + "上线了！\r\n", messageList.getStyle("normal"));
                    } else if (command.equals("DELETE")) { // 有用户下线更新列表
                        String userName = st.nextToken();
                        User user = onLineUser.get(userName);
                        onLineUser.remove(userName);

                        messagesToWhoComboBox.removeItem(userName);
                        messagesToWhoComboBox.revalidate();
                        messageList.getDocument().insertString(messageList.getDocument().getLength(),
                                "[系统通知] " + userName + "下线了！\r\n", messageList.getStyle("normal"));
                    } else if (command.equals("USERLIST")) {  //更新用户列表
                        int size = Integer.parseInt(st.nextToken());
                        String userName = null;
                        String userIp = null;
                        for (int i = 0; i < size; i++) {
                            userName = st.nextToken();
                            userIp = st.nextToken();
                            User user = new User(userName, userIp);
                            onLineUser.put(userName, user);
                            messagesToWhoComboBox.addItem(userName);
                            messagesToWhoComboBox.revalidate();
                        }
                    } else if (command.equals("SYSTEMSOUT")) { // 普通消息
                        messageList.getDocument().insertString(messageList.getDocument().getLength(),
                                st.nextToken() + "\r\n", messageList.getStyle("normal"));
                    } else if (command.equals("SENDFILE")) {
                        int result = JOptionPane.showConfirmDialog(
                                clientUI,
                                "确认接受文件？",
                                "提示",
                                JOptionPane.YES_NO_CANCEL_OPTION
                        );
                        if (result == 0) {
                            st.nextToken();
                            String allFilePath = st.nextToken();
                            String fileName = allFilePath.substring(allFilePath.lastIndexOf("\\") + 1);

                            Socket fileSocket = new Socket(hostIp, 8769);
                            DataInputStream fis = new DataInputStream(fileSocket.getInputStream());
                            FileOutputStream fos = new FileOutputStream(new StringBuilder(MY_FILE_PATH).append("\\").append(fileName).toString());
                            byte[] bytes = new byte[4096];
                            int len;

                            while ((len = fis.read(bytes)) != -1) {
                                fos.write(bytes, 0, len);
                            }
                            fos.close();
                            fileSocket.close();

                            //接受文件
                            messageList.getDocument().insertString(messageList.getDocument().getLength(),
                                    "成功接受文件" + fileName + "\r\n", messageList.getStyle("normal"));
                            sendMessage("FILEACCEPT@" + userName + "接收成功");
                        } else {
                            //接受文件
                            messageList.getDocument().insertString(messageList.getDocument().getLength(),
                                    "拒绝接受文件\r\n", messageList.getStyle("normal"));
                            Socket fileSocket = new Socket(hostIp, 8769);
                            DataInputStream fis = new DataInputStream(fileSocket.getInputStream());
                            byte[] bytes = new byte[4096];
                            int len;

                            while ((len = fis.read(bytes)) != -1) ;
                            fileSocket.close();
                            sendMessage("@FILEACCEPT@" + userName + "拒绝接收");
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
