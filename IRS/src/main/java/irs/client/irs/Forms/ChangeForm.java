package irs.client.irs.Forms;

import irs.client.irs.WebClient.Services.SectionService;
import irs.client.irs.WebClient.Services.UserService;
import irs.server.irs_server.models.Section;
import irs.server.irs_server.payload.request.LoginRequest;
import irs.server.irs_server.payload.response.JwtResponse;
import irs.server.irs_server.payload.response.SectionsResponse;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

public class ChangeForm extends JFrame {
    private JwtResponse response;
    private JPanel panel;
    private JTextField searchField;
    private JButton searchButton;
    private JTextArea sectionBody;
    private JList sectionList;
    private JTextPane sectionHeader;
    private JButton saveButton;
    private JButton addButton;
    private JButton upButton;
    private JButton downButton;
    private JButton saveSectionButton;
    private JCheckBox hide;
    private JLabel createdBy;
    private JLabel createdOn;
    private JLabel changeBy;
    private JLabel changeOn;
    private LoginRequest loginRequest;
    private SectionService sectionService;
    private SectionsResponse sectionsResponse;
    private UserService userService;
    private DefaultListModel<String> model;
    private Timer timer;

    public ChangeForm(JwtResponse response, LoginRequest loginRequest)
    {
        this.response = response;
        this.loginRequest = loginRequest;
        this.sectionsResponse = new SectionsResponse();
        this.model = new DefaultListModel<>();
        this.sectionList.setModel(model);

        getSections();

        timer = new Timer(60000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getSections();
            }
        });
        timer.setRepeats(true);
        timer.start();
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        sectionList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Integer index = sectionList.getSelectedIndex();

                if (index == -1)
                {
                    sectionHeader.setText("");
                    sectionBody.setText("");
                }
                Section section = sectionsResponse.getSectionList().get(index);
                sectionHeader.setText(section.getHeader());
                sectionBody.setText(section.getBody());
            }
        });
    }


    private void getSections()
    {
        SwingWorker worker = new SwingWorker<SectionsResponse, Void>() {
            @Override
            protected SectionsResponse doInBackground() throws Exception {
                userService = new UserService();
                JwtResponse jwtResponse = userService.authenticateUserSync(loginRequest);

                sectionService = new SectionService();
                SectionsResponse sectionsResponse = sectionService.getOrderSections(jwtResponse.getAccessToken());

                return sectionsResponse;
            }
            protected void done() {
                try {
                    sectionsResponse = get();
                    sectionList.setSelectedIndex(-1);
                    model.clear();
                    model.addAll(sectionsResponse.getSectionList().stream().map(x -> x.getHeader()).toList());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        worker.execute();
    }

    public void open(){
        setTitle("IRS");
        setContentPane(new ChangeForm(response, loginRequest).panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(1200, 600);
        setMinimumSize(new Dimension(1000, 600));
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
