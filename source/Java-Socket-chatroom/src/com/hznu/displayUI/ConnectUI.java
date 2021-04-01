package com.hznu.displayUI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 用户确定要连接的服务器ip和端口号
 */
public class ConnectUI extends JFrame {
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
    public JTextField hostIpTextField;
    public JTextField portTextField;

    private int port;
    private String hostIp;

    public int getPort() {
        return port;
    }

    public String getHostIp() {
        return hostIp;
    }

    /**
     * 初始化窗体
     */
    public ConnectUI() {
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

        hostIpLabel = new JLabel("请输入服务器的IP地址：");
        hostIpLabel.setFont(LABEL_FONT);
        hostIpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hostIpPanel.add(hostIpLabel);

        hostIpTextField = new JTextField();
        hostIpTextField.setFont(TEXTFIELD_FONT);
        hostIpTextField.setText("127.0.0.1");
        hostIpPanel.add(hostIpTextField);
        hostIpTextField.setColumns(8);

        portPanel = new JPanel();
        portPanel.setBorder(new CompoundBorder());
        portPanel.setBackground(Color.WHITE);
        mainContentPanel.add(portPanel);

        portLabel = new JLabel("请输入服务器的端口号：");
        portLabel.setFont(LABEL_FONT);
        portPanel.add(portLabel);

        portTextField = new JTextField();
        portTextField.setFont(TEXTFIELD_FONT);
        portTextField.setText("8089");
        portPanel.add(portTextField);
        portTextField.setColumns(8);

        buttonPanel = new JPanel();
        buttonPanel.setBorder(new CompoundBorder());
        buttonPanel.setBackground(Color.WHITE);
        mainContentPanel.add(buttonPanel);

        saveButton = new MyButton("保存");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tmpPort = portTextField.getText().trim();
                String tmpHostIp = hostIpTextField.getText().trim();
                if (tmpHostIp == null || tmpPort == null || tmpHostIp.length() == 0 || tmpPort.length() == 0) {
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            "输入不能为空！",
                            "警告信息",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    try {
                        port = Integer.parseInt(tmpPort);
                        hostIp = tmpHostIp;
                        //关闭当前窗口
                        ConnectUI.this.dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                "输入的端口号不规范，要求为整数！",
                                "警告信息",
                                JOptionPane.WARNING_MESSAGE);
                    }

                }
            }
        });
        buttonPanel.add(saveButton);

        cancleButton = new MyButton("取消");
        cancleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ConnectUI.this.dispose();
            }
        });
        buttonPanel.add(cancleButton);
    }
}
