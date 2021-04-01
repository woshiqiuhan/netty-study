package com.hznu.displayUI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * 服务器端界面
 */
public class ServerUI extends JFrame {
    private static final Font TEXT_FONT = new Font("微软雅黑", Font.BOLD, 17);

    private JPanel mainContentPanel;
    private JPanel headMenuPanel;
    private JPanel bottomMessageListPanel;
    private JPanel sendMessagePanel;
    private JPanel onlineUserPanel;

    private JTextField messageTextField;

    private MyButton portSettingButton;
    private MyButton startConnectionButton;
    private MyButton stopConnectionButton;
    private MyButton exitButton;
    private MyButton sendMessageButton;
    private MyButton chooseFileButton;

    private JTextArea textArea;

    private DefaultListModel listModel;
    private JList userList;

    public MyButton getChooseFileButton() {
        return chooseFileButton;
    }

    public void setChooseFileButton(MyButton chooseFileButton) {
        this.chooseFileButton = chooseFileButton;
    }

    /**
     * 初始化界面
     */
    public ServerUI() {
        setResizable(false);
        setTitle("聊天室服务端");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(700, 200, 500, 600);
        mainContentPanel = new JPanel();
        mainContentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        mainContentPanel.setLayout(new BorderLayout(0, 0));
        setContentPane(mainContentPanel);

        //顶部菜单栏
        headMenuPanel = new JPanel();
        headMenuPanel.setBorder(new CompoundBorder());
        mainContentPanel.add(headMenuPanel, BorderLayout.NORTH);

        portSettingButton = new MyButton("端口设置");

        portSettingButton.setVerticalAlignment(SwingConstants.BOTTOM);

        startConnectionButton = new MyButton("启动服务");

        stopConnectionButton = new MyButton("停止服务");

        exitButton = new MyButton("退出");

        //将按钮添加进顶部菜单栏
        headMenuPanel.add(portSettingButton);
        headMenuPanel.add(startConnectionButton);
        headMenuPanel.add(stopConnectionButton);
        headMenuPanel.add(exitButton);

        //下方布局
        bottomMessageListPanel = new JPanel();
        mainContentPanel.add(bottomMessageListPanel, BorderLayout.SOUTH);
        bottomMessageListPanel.setLayout(new GridLayout(2, 1, 0, 3));

        sendMessagePanel = new JPanel();
        sendMessagePanel.setBorder(new CompoundBorder());
        bottomMessageListPanel.add(sendMessagePanel);
        sendMessagePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JLabel jLabel = new JLabel("发送信息：");
        jLabel.setFont(TEXT_FONT);
        sendMessagePanel.add(jLabel);

        messageTextField = new JTextField();
        messageTextField.setEditable(false);
        messageTextField.setFont(TEXT_FONT);
        messageTextField.setHorizontalAlignment(SwingConstants.LEFT);
        sendMessagePanel.add(messageTextField);
        //设置输入文本框长度
        messageTextField.setColumns(10);


        sendMessagePanel.add(new JLabel("     "));

        //单击发送
        sendMessageButton = new MyButton("发送");
        sendMessageButton.setEnabled(false);

        sendMessagePanel.add(sendMessageButton);

        //选择文件
        chooseFileButton = new MyButton("选择文件");
        chooseFileButton.setEnabled(false);

        sendMessagePanel.add(chooseFileButton);

        //消息面板
        JPanel user_panel = new JPanel();
        user_panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        mainContentPanel.add(user_panel, BorderLayout.CENTER);
        user_panel.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();

        //显示在线用户侧边栏
        onlineUserPanel = new JPanel();
        onlineUserPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        scrollPane.setRowHeaderView(onlineUserPanel);
        onlineUserPanel.setLayout(new BorderLayout(0, 0));

        JLabel onlineUserLabel = new JLabel("在线用户");
        onlineUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
        onlineUserLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        onlineUserPanel.add(onlineUserLabel, BorderLayout.NORTH);

        listModel = new DefaultListModel();
        userList = new JList(listModel);

        userList.setForeground(new Color(0, 0, 0));
        userList.setFont(new Font("微软雅黑", Font.BOLD, 12));
        onlineUserPanel.add(userList, BorderLayout.CENTER);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("微软雅黑", Font.BOLD, 14));

        textArea.setBounds(23, 217, 650, 266);
        scrollPane.setViewportView(textArea);
        user_panel.add(scrollPane);
    }

    public JPanel getMainContentPanel() {
        return mainContentPanel;
    }

    public void setMainContentPanel(JPanel mainContentPanel) {
        this.mainContentPanel = mainContentPanel;
    }

    public JPanel getHeadMenuPanel() {
        return headMenuPanel;
    }

    public void setHeadMenuPanel(JPanel headMenuPanel) {
        this.headMenuPanel = headMenuPanel;
    }

    public JPanel getBottomMessageListPanel() {
        return bottomMessageListPanel;
    }

    public void setBottomMessageListPanel(JPanel bottomMessageListPanel) {
        this.bottomMessageListPanel = bottomMessageListPanel;
    }

    public JPanel getSendMessagePanel() {
        return sendMessagePanel;
    }

    public void setSendMessagePanel(JPanel sendMessagePanel) {
        this.sendMessagePanel = sendMessagePanel;
    }

    public JPanel getOnlineUserPanel() {
        return onlineUserPanel;
    }

    public void setOnlineUserPanel(JPanel onlineUserPanel) {
        this.onlineUserPanel = onlineUserPanel;
    }

    public JTextField getMessageTextField() {
        return messageTextField;
    }

    public void setMessageTextField(JTextField messageTextField) {
        this.messageTextField = messageTextField;
    }

    public MyButton getPortSettingButton() {
        return portSettingButton;
    }

    public void setPortSettingButton(MyButton portSettingButton) {
        this.portSettingButton = portSettingButton;
    }

    public MyButton getStartConnectionButton() {
        return startConnectionButton;
    }

    public void setStartConnectionButton(MyButton startConnectionButton) {
        this.startConnectionButton = startConnectionButton;
    }

    public MyButton getStopConnectionButton() {
        return stopConnectionButton;
    }

    public void setStopConnectionButton(MyButton stopConnectionButton) {
        this.stopConnectionButton = stopConnectionButton;
    }

    public MyButton getExitButton() {
        return exitButton;
    }

    public void setExitButton(MyButton exitButton) {
        this.exitButton = exitButton;
    }

    public MyButton getSendMessageButton() {
        return sendMessageButton;
    }

    public void setSendMessageButton(MyButton sendMessageButton) {
        this.sendMessageButton = sendMessageButton;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    public DefaultListModel getListModel() {
        return listModel;
    }

    public void setListModel(DefaultListModel listModel) {
        this.listModel = listModel;
    }

    public JList getUserList() {
        return userList;
    }

    public void setUserList(JList userList) {
        this.userList = userList;
    }
}
