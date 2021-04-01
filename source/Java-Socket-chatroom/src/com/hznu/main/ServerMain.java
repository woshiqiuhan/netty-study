package com.hznu.main;

import com.hznu.displayUI.MyButton;
import com.hznu.displayUI.ServerUI;
import com.hznu.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ServerMain {

    /**
     * 开启服务器窗口界面
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //创建用户窗体界面
                    ServerMain serverMain = new ServerMain();
                    serverMain.serverUI.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //服务端界面
    public ServerUI serverUI;

    private JTextField messageTextField;
    private MyButton portSettingButton;
    private MyButton startConnectionButton;
    private MyButton stopConnectionButton;
    private MyButton exitButton;
    private MyButton sendMessageButton;
    private MyButton chooseFileButton;
    private JTextArea textArea;

    //代表服务端是否启动
    private boolean isStart = false;
    //存储所有连接的客户端线程
    private List<ClientThread> clients = null;
    private ServerSocket serverSocket;
    private ServerThread serverThread;
    private DefaultListModel listModel;
    private ServerSocket fileServerSocket;

    private int port = -1;

    public ServerMain() {
        serverUI = new ServerUI();
        messageTextField = serverUI.getMessageTextField();
        portSettingButton = serverUI.getPortSettingButton();
        startConnectionButton = serverUI.getStartConnectionButton();
        stopConnectionButton = serverUI.getStopConnectionButton();
        exitButton = serverUI.getExitButton();
        sendMessageButton = serverUI.getSendMessageButton();
        chooseFileButton = serverUI.getChooseFileButton();
        listModel = serverUI.getListModel();
        textArea = serverUI.getTextArea();
        try {
            fileServerSocket = new ServerSocket(8769);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取服务器开启的端口号
        portSettingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    port = Integer.parseInt(JOptionPane.showInputDialog(
                            new JFrame("请输入端口号"),
                            "请输入端口号(默认请设为8089):"
                    ));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            "端口号不合法，请重新设定！",
                            "警告信息",
                            JOptionPane.WARNING_MESSAGE);
                    port = -1;
                }
            }
        });

        //启动连接
        startConnectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });

        //关闭连接
        stopConnectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });

        //退出服务端
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //如果服务器仍未开启状态，先关闭连接
                if (isStart) {
                    stop();
                }
                System.exit(0);
            }
        });

        //给文本框增加回车发送功能
        messageTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });

        //按钮发送信息
        sendMessageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });

        //选择发送文件
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建一个默认的文件选择器
                JFileChooser fileChooser = new JFileChooser();
                // 设置默认显示的文件夹
                fileChooser.setCurrentDirectory(new File("C:\\Users\\12061\\Desktop"));
                // 添加可用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名）
                //fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("(txt)", "txt"));
                // 设置默认使用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名 可变参数）
                // fileChooser.setFileFilter(new FileNameExtensionFilter("(txt)", "txt"));
                // 打开文件选择框（线程将被堵塞，直到选择框被关闭）
                int result = fileChooser.showOpenDialog(serverUI);  // 对话框将会尽量显示在靠近 parent 的中心
                // 点击确定
                if (result == JFileChooser.APPROVE_OPTION) {
                    // 获取路径
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    if (JOptionPane.showConfirmDialog(
                            serverUI,
                            "确认发送文件" + path + "？",
                            "提示",
                            JOptionPane.YES_NO_CANCEL_OPTION
                    ) == 0) {
                        sendFile(path);
                    }
                }
            }
        });
    }

    /**
     * @Description 启动服务器
     */
    public synchronized void start() {
        if (isStart) {
            JOptionPane.showMessageDialog(serverUI, "服务器已经启动");
            return;
        }
        try {
            if (port < 0) {
                JOptionPane.showMessageDialog(serverUI, "端口号不合法，请重新设定！");
                return;
            }
            serverStart(port);  //启动
            textArea.append("服务器已启动！端口号：" + port + "\r\n");
            JOptionPane.showMessageDialog(serverUI, "服务器启动成功！");

            startConnectionButton.setEnabled(false);
            portSettingButton.setEnabled(false);
            stopConnectionButton.setEnabled(true);
            messageTextField.setEditable(true);
            sendMessageButton.setEnabled(true);
            chooseFileButton.setEnabled(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(serverUI, "端口号不合法，请重新设定！");
        }
    }

    /**
     * @param port
     * @Description 服务器启动服务
     */
    public synchronized void serverStart(int port) {
        clients = new ArrayList<>();  //放客户端线程
        try {
            //创建ServerSocket
            serverSocket = new ServerSocket(port);
            serverThread = new ServerThread(serverSocket);
            serverThread.start();
            isStart = true;
        } catch (BindException e) {
            isStart = false;
            JOptionPane.showMessageDialog(serverUI, "端口号被占用！");
        } catch (Exception e) {
            e.printStackTrace();
            isStart = false;
            JOptionPane.showMessageDialog(serverUI, "服务器启动异常");
        }
    }

    /**
     * @Description 服务器停止服务
     */
    public synchronized void stop() {
        if (!isStart) {
            JOptionPane.showMessageDialog(serverUI, "服务器还未启动，无需停止！");
            return;
        }
        try {
            closeServer();  //关闭
            startConnectionButton.setEnabled(true);
            portSettingButton.setEnabled(true);
            stopConnectionButton.setEnabled(false);
            messageTextField.setEditable(false);
            sendMessageButton.setEnabled(false);
            chooseFileButton.setEnabled(false);

            textArea.append("服务器已成功停止！\r\n");
            JOptionPane.showMessageDialog(serverUI, "服务器已停止！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(serverUI, "停止服务器发生异常!");
        }
    }

    /**
     * @Description 服务器关闭
     */
    public synchronized void closeServer() {
        try {
            if (serverThread != null) {
                serverThread.stop(); // 停止服务线程
            }

            for (ClientThread client : clients) {
                client.getWrite().println("CLOSE");
                client.getWrite().flush();
                // 释放资源
                client.stop(); // 停止此条为客户服务的线程
                client.read.close();
                client.write.close();
                client.socket.close();
                clients.remove(client);
            }

            // 关闭服务器连接
            if (serverSocket != null) {
                serverSocket.close();
            }

            listModel.removeAllElements();  //清空用户列表
            isStart = false;
        } catch (IOException e) {
            e.printStackTrace();
            isStart = true;
        }
    }

    /**
     * @Description 发送信息
     */
    public synchronized void send() {
        if (!isStart) {
            JOptionPane.showMessageDialog(serverUI, "服务器还未启动，请先启动服务器");
            return;
        }
        if (clients.size() == 0) {
            JOptionPane.showMessageDialog(serverUI, "没有用户在线，不能发送消息！");
            return;
        }
        String message = messageTextField.getText().trim(); // 去掉字符串头部和尾部的空字符串
        if (message == null || message.equals("")) {
            JOptionPane.showMessageDialog(serverUI, "消息不能为空！");
            return;
        }
        sendServerMessage(message); // 群发消息
        textArea.append("[系统通知] " + messageTextField.getText() + "\r\n");
        messageTextField.setText(null);
    }

    /**
     * @param message
     * @Description 服务器群发信息
     */
    public synchronized void sendServerMessage(String message) {
        //对所有客户端发送信息
        for (ClientThread client : clients) {
            PrintWriter write = client.getWrite();
            write.println("SYSTEMSOUT@[系统通知] " + message);
            write.flush();
        }
    }

    /**
     * @param message
     * @Description 转发客户端发出的信息
     */
    public synchronized void sendMessage(String message) {
        StringTokenizer st = new StringTokenizer(message, "@");//用@作为分隔符分隔信息
        String name = st.nextToken();        //按格式取，第一个用户名称
        String owner = st.nextToken();        //判断群发还是私聊
        String contant = st.nextToken();    //消息内容
        if (owner.equals("ALL")) { // 群发
            message = "SYSTEMSOUT@" + name + "说：" + contant;
            textArea.append(name + "说：" + contant + "\r\n");
            for (ClientThread client : clients) {
                PrintWriter write = client.getWrite();
                write.println(message);  //获取用户的输出流并打印信息
                write.flush();  //清空缓存区
            }
        } else if (owner.equals("ONE")) {
            String to = st.nextToken();
            message = "SYSTEMSOUT@" + name + "对" + to + "说：" + contant;
            textArea.append(name + "对" + to + "说：" + contant + "\r\n");
            int cnt = 0;
            for (ClientThread client : clients) {
                if (client.getUser().getName().equals(to) || client.getUser().getName().equals(name)) {
                    PrintWriter write = client.getWrite();
                    write.println(message);  //获取用户的输出流并打印信息
                    write.flush();  //清空缓存区
                    cnt++;
                }
                if (cnt == 2) {
                    break;
                }
            }
        }
    }

    /**
     * @Description 发送文件
     */
    public synchronized void sendFile(String path) {
        if (!isStart) {
            JOptionPane.showMessageDialog(serverUI, "服务器还未启动，请先启动服务器");
            return;
        }
        if (clients.size() == 0) {
            JOptionPane.showMessageDialog(serverUI, "没有用户在线，不能发送消息！");
            return;
        }
        sendServerFile(path); // 群发消息
        textArea.append("[系统通知] 发送文件" + path + "\r\n");
    }

    /**
     * @param path
     * @Description 服务器群发文件
     */
    public synchronized void sendServerFile(String path) {
        File file = new File(path);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //对所有客户端发送信息
                for (ClientThread client : clients) {
                    PrintWriter write = client.getWrite();
                    client.getWrite().println("SENDFILE@[系统通知] 发送文件@" + path);
                    write.flush();  //清空缓存区
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try (FileInputStream fis = new FileInputStream(file);
                                 Socket fileAcceptSocket = fileServerSocket.accept();
                                 DataOutputStream fos = new DataOutputStream(fileAcceptSocket.getOutputStream())) {
                                byte[] bytes = new byte[4096];
                                int len;
                                while ((len = fis.read(bytes)) != -1) {
                                    fos.write(bytes, 0, len);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        }).start();
    }


    /**
     * @Description 服务器监听线程
     */
    class ServerThread extends Thread {
        private ServerSocket serverSocket;

        public ServerThread(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            // 不停地等待客户连接
            while (true) {
                try {
                    //获取发送请求的客户端socket
                    Socket socket = serverSocket.accept();
                    //创建客户端线程类，并向所有用户发送上线通知
                    ClientThread client = new ClientThread(socket);
                    client.start(); // 开启客户端服务线程

                    //将上线的用户的socket存储进clients中
                    clients.add(client);

                    listModel.addElement(client.getUser().getName());  //更新在线列表
                    textArea.append("[系统通知] " + client.getUser().getName() + "上线了！\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @Description 为客户端服务的服务器线程
     */
    class ClientThread extends Thread {
        private Socket socket;
        private BufferedReader read;
        private PrintWriter write;
        private DataOutputStream fos;
        private User user;

        //为用户提供输入输出流的getter方法
        public BufferedReader getRead() {
            return read;
        }

        public PrintWriter getWrite() {
            return write;
        }

        public User getUser() {
            return user;
        }

        public DataOutputStream getFos() {
            return fos;
        }

        public void setFos(DataOutputStream fos) {
            this.fos = fos;
        }

        public ClientThread(Socket socket) {
            super();
            this.socket = socket;
            try {
                read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                write = new PrintWriter(socket.getOutputStream());
                fos = new DataOutputStream(socket.getOutputStream());
                // 接受用户信息
                String info = read.readLine();
                StringTokenizer st = new StringTokenizer(info, "@");
                user = new User(st.nextToken(), st.nextToken());
                // 反馈连接成功的信息
                write.print("SYSTEMSOUT@[系统通知] " + user.getName() + "与服务器连接成功！\r\n");
                write.flush();

                // 反馈当前所有在线用户信息
                if (clients.size() > 0) {
                    StringBuilder temp = new StringBuilder();
                    temp.append("USERLIST@" + clients.size() + "@");
                    for (ClientThread client : clients) {
                        User clientUser = client.getUser();
                        temp.append((clientUser.getName() + "/" + clientUser.getIp()) + "@");
                    }
                    //注意换行符！！！！！PrintWriter的flush方法不起作用！！！
                    write.println(temp.toString());
                    write.flush();
                }

                // 向所有的用户发送该用户上线的消息
                for (ClientThread client : clients) {
                    PrintWriter write = client.getWrite();
                    write.println("ADD@" + user.getName() + "@" + user.getIp());  //获取用户的输出流并打印信息
                    write.flush();  //清空缓存区
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 不断接受客户传来的消息，进行处理
         */
        @Override
        public void run() {
            String message = null;
            while (true) {
                try {
                    message = read.readLine(); // 接受消息
                    StringTokenizer st = new StringTokenizer(message, "@");
                    String command = st.nextToken();
                    String command2 = null;
                    if (st.hasMoreTokens()) {
                        command2 = st.nextToken();
                    }

                    if (message.equals("CLOSE")) { // 下线命令
                        textArea.append("[系统通知] " + this.getUser().getName() + "下线!\r\n");
                        // 断开连接的资源
                        read.close();
                        write.close();
                        socket.close();

                        // 向所有在线用户发送该用户下线的消息
                        for (ClientThread client : clients) {
                            PrintWriter write = client.getWrite();
                            write.println("DELETE@" + this.getUser().getName());  //获取用户的输出流并打印信息
                            write.flush();  //清空缓存区
                        }

                        listModel.removeElement(user.getName());

                        // 删除此条客户端的服务线程
                        for (ClientThread client : clients) {
                            if (client.getUser() == user) {
                                ClientThread temp = client;
                                clients.remove(client); // 删除此用户的服务线程
                                temp.stop(); // 停止该条线程
                                return;
                            }
                        }
                    } else if (command2 != null && (command2.equals("ONE") || command2.equals("ALL"))) {
                        sendMessage(message);
                    } else if (command.equals("FILEACCEPT")) {
                        textArea.append("[系统通知] " + command2 + "\r\n");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
