package irs.client.irs.Forms;

import javax.swing.*;

public class MainForm {
    private JPanel panel;
    private JButton ChangeButton;
    private JButton ViewButton;

    public static void main(String[] args){
        JFrame frame = new JFrame("IRS");
        frame.setContentPane(new MainForm().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(250, 120);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
