package irs.client.irs.Forms;

import irs.client.irs.WebClient.Services.SectionService;
import irs.client.irs.WebClient.Services.UserService;
import irs.server.irs_server.models.Section;
import irs.server.irs_server.payload.request.LoginRequest;
import irs.server.irs_server.payload.response.JwtResponse;
import irs.server.irs_server.payload.response.SectionsResponse;

import javax.swing.*;
import java.util.ArrayList;

public class ViewForm extends JFrame {
    private JwtResponse response;
    private JPanel panel;
    private JList SectionsList;
    private JTextField textField1;
    private JButton Button;
    private JTextPane SectionBody;
    private LoginRequest loginRequest;
    private SectionService sectionService;
    private SectionsResponse sectionsResponse;
    private UserService userService;

    public ViewForm(JwtResponse response, LoginRequest loginRequest)
    {
        this.response = response;
        this.loginRequest = loginRequest;
    }

    private void getSections()
    {
        userService = new UserService();
        userService.authenticateUser(loginRequest)
                .subscribe(JwtResponse -> {
                    sectionService = new SectionService();
                    sectionService.getOrderSections(JwtResponse.getAccessToken())
                            .subscribe(SectionsResponse ->
                            {
                                sectionsResponse = SectionsResponse;
                                SectionsList.setListData(sectionsResponse.getSectionList().stream().map(x -> x.getHeader()).toArray());
                            });
                });
    }



    public void open(){
        setTitle("IRS");
        setContentPane(new ViewForm(response, loginRequest).panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(800, 600);
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
        getSections();
    }
}
