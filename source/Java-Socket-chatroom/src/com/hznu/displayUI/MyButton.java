package com.hznu.displayUI;

import javax.swing.*;
import java.awt.*;

/**
 * 自定义button组件，为所有按钮设定为统一样式
 */
public class MyButton extends JButton {
    private static final Font BUTTON_FONT = new Font("微软雅黑", Font.BOLD, 16);

    public MyButton(String text) {
        super(text);
        this.setFont(BUTTON_FONT);
        this.setBackground(Color.WHITE);
    }
}
