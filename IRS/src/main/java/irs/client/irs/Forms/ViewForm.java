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

public class ViewForm extends JFrame {
    private JwtResponse response;
    private JPanel panel;
    private JTextField searchField;
    private JButton searchButton;
    private JTextPane sectionBody;
    private JList sectionList;
    private JTextPane sectionHeader;
    private JScrollPane bodyScroll;
    private LoginRequest loginRequest;
    private SectionService sectionService;
    private SectionsResponse sectionsResponse;
    private UserService userService;
    private DefaultListModel<String> model;
    private Timer timer;
    private Boolean isSearch;
    private java.util.List<Section> sections;

    public ViewForm(JwtResponse response, LoginRequest loginRequest)
    {
        this.response = response;
        this.loginRequest = loginRequest;
        this.sectionsResponse = new SectionsResponse();
        this.model = new DefaultListModel<>();
        this.sectionList.setModel(model);
        this.isSearch = false;

        getSections();

        timer = new Timer(600000, new ActionListener() {
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
                    return;
                }
                Section section = sections.get(index);
                sectionHeader.setText(section.getHeader());
                sectionBody.setText(section.getBody());
                sectionBody.setCaretPosition(0);
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSearch)
                {
                    if (searchField.getText() != "") {
                        searchButton.setText("Отмена");
                        isSearch = true;

                        getSearchSections(searchField.getText());
                    }
                }
                else
                {
                    searchButton.setText("Поиск");
                    isSearch = false;

                    getSections();
                }
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
                    sections = sectionsResponse.getSectionList().stream().filter(x -> x.getVisible() == null || x.getVisible()).toList();
                    sectionList.setSelectedIndex(-1);
                    model.clear();
                    model.addAll(sections.stream().map(x -> x.getHeader()).toList());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        worker.execute();
    }

    private void getSearchSections(String string)
    {
        SwingWorker worker = new SwingWorker<SectionsResponse, Void>() {
            @Override
            protected SectionsResponse doInBackground() throws Exception {
                userService = new UserService();
                JwtResponse jwtResponse = userService.authenticateUserSync(loginRequest);

                sectionService = new SectionService();
                SectionsResponse sectionsResponse = sectionService.getSearchSections(jwtResponse.getAccessToken(), string);

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
        setContentPane(new ViewForm(response, loginRequest).panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
