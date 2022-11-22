package irs.client.irs.Forms;

import irs.server.irs_server.models.User;
import irs.server.irs_server.payload.response.JwtResponse;

import javax.swing.*;

public class ChangeForm extends JFrame {
    private JwtResponse response;
    private JPanel panel;

    public ChangeForm(JwtResponse response)
    {
        this.response = response;
    }

    public void open(){
        setTitle("IRS");
        setContentPane(new ChangeForm(response).panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
