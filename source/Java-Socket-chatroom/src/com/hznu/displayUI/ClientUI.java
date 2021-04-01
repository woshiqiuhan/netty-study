package com.hznu.displayUI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * 客户端界面
 */
public class ClientUI extends JFrame {
    private static final Font TEXT_FONT = new Font("微软雅黑", Font.BOLD, 17);

    private JPanel mainContentPanel;
    private JPanel headMenuPanel;
    private JPanel bottomMessageListPanel;
    private JPanel bottomSubPanel;
    private JPanel sendMessagePanel;
    private JPanel middlePanel;
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

    private JComboBox messagesToWhoComboBox;


    /**
     * 创建用户窗体
     */
    public ClientUI() {
        //设置窗体信息
        setResizable(false);
        setTitle("聊天室客户端");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(700, 200, 500, 600);

        //创建主布局
        mainContentPanel = new JPanel();
        mainContentPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
        mainContentPanel.setLayout(new BorderLayout(0, 0));
        setContentPane(mainContentPanel);

        //创建顶部菜单按钮布局
        headMenuPanel = new JPanel();
        headMenuPanel.setBorder(new CompoundBorder());
        mainContentPanel.add(headMenuPanel, BorderLayout.NORTH);

        //创用户名设置按钮
        editUserInfoButton = new MyButton("用户设置");
        editUserInfoButton.setVerticalAlignment(SwingConstants.BOTTOM);

        //获取端口号本服务器ip
        getConnectInfoButton = new MyButton("连接设置");

        //设置好ip端口用户名之后即可登录
        loginButton = new MyButton("登录");

        //注销登录，但可以重新登录
        logoutButton = new MyButton("注销");
        logoutButton.setEnabled(false);

        //退出当前界面
        exitButton = new MyButton("退出");

        //将按钮加入顶部布局
        headMenuPanel.add(editUserInfoButton);
        headMenuPanel.add(getConnectInfoButton);
        headMenuPanel.add(logoutButton);
        headMenuPanel.add(loginButton);
        headMenuPanel.add(exitButton);

        //下方布局
        bottomMessageListPanel = new JPanel();
        mainContentPanel.add(bottomMessageListPanel, BorderLayout.SOUTH);
        bottomMessageListPanel.setLayout(new GridLayout(3, 1, 0, 3));

        bottomSubPanel = new JPanel();
        bottomSubPanel.setBorder(new CompoundBorder());
        bottomMessageListPanel.add(bottomSubPanel);
        bottomSubPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        messagesToWhoComboBox = new JComboBox();
        messagesToWhoComboBox.addItem("所有人");
        messagesToWhoComboBox.setSelectedIndex(0);
        messagesToWhoComboBox.setFont(new Font("微软雅黑", Font.BOLD, 14));
        bottomSubPanel.add(messagesToWhoComboBox);

        bottomSubPanel.add(new JLabel("                        "));

        privateChatButton = new MyButton("私聊");

        bottomSubPanel.add(privateChatButton);

        sendMessagePanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) sendMessagePanel.getLayout();
        sendMessagePanel.setBorder(new CompoundBorder());
        bottomMessageListPanel.add(sendMessagePanel);

        JLabel jLabel = new JLabel("发送信息：");
        jLabel.setFont(TEXT_FONT);
        sendMessagePanel.add(jLabel);

        messageTextField = new JTextField();
        messageTextField.setEditable(false);
        messageTextField.setFont(TEXT_FONT);
        messageTextField.setHorizontalAlignment(SwingConstants.LEFT);
        sendMessagePanel.add(messageTextField);
        //设置输入文本框长度
        messageTextField.setColumns(15);

        sendMessagePanel.add(new JLabel("     "));

        //单击发送
        sendMessageButton = new MyButton("发送");
        sendMessageButton.setEnabled(false);

        sendMessagePanel.add(sendMessageButton);

        middlePanel = new JPanel();
        middlePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        mainContentPanel.add(middlePanel, BorderLayout.CENTER);
        middlePanel.setLayout(new BorderLayout(0, 0));

        messageList = new JTextPane();
        messageList.setEditable(false);
        messageList.setFont(new Font("微软雅黑", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane();
        middlePanel.add(scrollPane);
        scrollPane.setBounds(23, 217, 650, 266);
        messageList.setBounds(23, 217, 650, 266);
        scrollPane.setViewportView(messageList);
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

    public JPanel getBottomSubPanel() {
        return bottomSubPanel;
    }

    public void setBottomSubPanel(JPanel bottomSubPanel) {
        this.bottomSubPanel = bottomSubPanel;
    }

    public JPanel getSendMessagePanel() {
        return sendMessagePanel;
    }

    public void setSendMessagePanel(JPanel sendMessagePanel) {
        this.sendMessagePanel = sendMessagePanel;
    }

    public JTextField getMessageTextField() {
        return messageTextField;
    }

    public void setMessageTextField(JTextField messageTextField) {
        this.messageTextField = messageTextField;
    }

    public JTextPane getMessageList() {
        return messageList;
    }

    public void setMessageList(JTextPane messageList) {
        this.messageList = messageList;
    }

    public MyButton getEditUserInfoButton() {
        return editUserInfoButton;
    }

    public void setEditUserInfoButton(MyButton editUserInfoButton) {
        this.editUserInfoButton = editUserInfoButton;
    }

    public MyButton getGetConnectInfoButton() {
        return getConnectInfoButton;
    }

    public void setGetConnectInfoButton(MyButton getConnectInfoButton) {
        this.getConnectInfoButton = getConnectInfoButton;
    }

    public MyButton getLoginButton() {
        return loginButton;
    }

    public void setLoginButton(MyButton loginButton) {
        this.loginButton = loginButton;
    }

    public MyButton getLogoutButton() {
        return logoutButton;
    }

    public void setLogoutButton(MyButton logoutButton) {
        this.logoutButton = logoutButton;
    }

    public MyButton getExitButton() {
        return exitButton;
    }

    public void setExitButton(MyButton exitButton) {
        this.exitButton = exitButton;
    }

    public MyButton getPrivateChatButton() {
        return privateChatButton;
    }

    public void setPrivateChatButton(MyButton privateChatButton) {
        this.privateChatButton = privateChatButton;
    }

    public MyButton getSendMessageButton() {
        return sendMessageButton;
    }

    public void setSendMessageButton(MyButton sendMessageButton) {
        this.sendMessageButton = sendMessageButton;
    }

    public JComboBox getMessagesToWhoComboBox() {
        return messagesToWhoComboBox;
    }

    public void setMessagesToWhoComboBox(JComboBox messagesToWhoComboBox) {
        this.messagesToWhoComboBox = messagesToWhoComboBox;
    }

    public JPanel getMiddlePanel() {
        return middlePanel;
    }

    public void setMiddlePanel(JPanel middlePanel) {
        this.middlePanel = middlePanel;
    }
}
