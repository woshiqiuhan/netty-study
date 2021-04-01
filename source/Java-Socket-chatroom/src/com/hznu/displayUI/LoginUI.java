package com.hznu.displayUI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录窗口
 */
public class LoginUI extends JFrame {
    private final static Font LABEL_FONT = new Font("微软雅黑", Font.BOLD, 18);
    private final static Font TEXTFIELD_FONT = new Font("微软雅黑", Font.BOLD, 12);

    private JPanel mainContentPanel;
    private JLabel hostIpLabel;
    private JPanel hostIpPanel;
    private JLabel portLabel;
    private JPanel portPanel;
    private JPanel buttonPanel;
    private MyButton cancleButton;
    private MyButton saveButton;
    public JTextField userNameTextField;
    public JPasswordField userPasswordTextField;


    private String userName;
    private String userPassword;

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    private static Map<String, String> usersMap;

    static {
        usersMap = new HashMap<>();
        usersMap.put("秋寒", "123456");
        usersMap.put("user01", "123456");
        usersMap.put("user02", "123456");
        usersMap.put("user03", "123456");
        usersMap.put("user04", "123456");
        usersMap.put("user05", "123456");
        usersMap.put("user06", "123456");
        usersMap.put("user07", "123456");
        usersMap.put("user08", "123456");
    }

    /**
     * 初始化窗体
     */
    public LoginUI() {
        setResizable(false);
        setTitle("连接设置");
        setBackground(Color.WHITE);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(780, 400, 350, 194);

        mainContentPanel = new JPanel();
        mainContentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(mainContentPanel);
        mainContentPanel.setLayout(new GridLayout(0, 1, 0, 0));

        hostIpPanel = new JPanel();
        hostIpPanel.setBorder(new CompoundBorder());
        hostIpPanel.setBackground(Color.WHITE);
        mainContentPanel.add(hostIpPanel);

        hostIpLabel = new JLabel("请输入用户名：");
        hostIpLabel.setFont(LABEL_FONT);
        hostIpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hostIpPanel.add(hostIpLabel);

        userNameTextField = new JTextField();
        userNameTextField.setFont(TEXTFIELD_FONT);
        hostIpPanel.add(userNameTextField);
        userNameTextField.setColumns(8);

        portPanel = new JPanel();
        portPanel.setBorder(new CompoundBorder());
        portPanel.setBackground(Color.WHITE);
        mainContentPanel.add(portPanel);

        portLabel = new JLabel("请输入密码： ");
        portLabel.setFont(LABEL_FONT);
        portPanel.add(portLabel);

        userPasswordTextField = new JPasswordField();
        userPasswordTextField.setFont(TEXTFIELD_FONT);
        portPanel.add(userPasswordTextField);
        userPasswordTextField.setColumns(8);

        buttonPanel = new JPanel();
        buttonPanel.setBorder(new CompoundBorder());
        buttonPanel.setBackground(Color.WHITE);
        mainContentPanel.add(buttonPanel);

        saveButton = new MyButton("保存");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tmpUserPassword = userPasswordTextField.getText().trim();
                String tmpUserName = userNameTextField.getText().trim();
                if (tmpUserName == null || tmpUserPassword == null || tmpUserName.length() == 0 || tmpUserPassword.length() == 0) {
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            "输入不能为空！",
                            "警告信息",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    userName = tmpUserName;
                    userPassword = tmpUserPassword;
                    if (!usersMap.containsKey(userName)) {
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                "用户名不存在！",
                                "警告信息",
                                JOptionPane.WARNING_MESSAGE
                        );
                    } else {
                        if (!userPassword.equals(usersMap.get(userName))) {
                            JOptionPane.showMessageDialog(
                                    new JFrame(),
                                    "密码错误！",
                                    "警告信息",
                                    JOptionPane.WARNING_MESSAGE
                            );
                        } else {
                            JOptionPane.showMessageDialog(
                                    new JFrame(),
                                    "登录成功！",
                                    "提示信息",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            //关闭当前窗口
                            LoginUI.this.dispose();
                        }
                    }
                }
            }
        });
        buttonPanel.add(saveButton);

        cancleButton = new MyButton("取消");
        cancleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LoginUI.this.dispose();
            }
        });
        buttonPanel.add(cancleButton);
    }
}
