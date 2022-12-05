package irs.client.irs.Forms;

import irs.client.irs.WebClient.Services.SectionService;
import irs.client.irs.WebClient.Services.UserService;
import irs.server.irs_server.models.Section;
import irs.server.irs_server.payload.request.LoginRequest;
import irs.server.irs_server.payload.request.SectionRequest;
import irs.server.irs_server.payload.request.SectionUpdateRequest;
import irs.server.irs_server.payload.response.JwtResponse;
import irs.server.irs_server.payload.response.SectionsResponse;
import org.springframework.http.ResponseEntity;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private JCheckBox hideCheck;
    private JLabel createdByLabel;
    private JLabel createdOnLabel;
    private JLabel changeByLabel;
    private JLabel changeOnLabel;
    private LoginRequest loginRequest;
    private SectionService sectionService;
    private SectionsResponse sectionsResponse;
    private UserService userService;
    private DefaultListModel<String> model;
    private Timer timer;
    private Boolean isSearch;
    private Boolean isCreate;
    private Timer clearHead;
    private String headerText;

    public ChangeForm(JwtResponse response, LoginRequest loginRequest)
    {
        this.response = response;
        this.loginRequest = loginRequest;
        this.sectionsResponse = new SectionsResponse();
        this.model = new DefaultListModel<>();
        this.sectionList.setModel(model);
        this.isSearch = false;
        this.isCreate = false;

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
                Section section = sectionsResponse.getSectionList().get(index);
                sectionHeader.setText(section.getHeader());
                sectionBody.setText(section.getBody());
                sectionBody.setCaretPosition(0);
                if(section.getVisible() != null && !section.getVisible())
                    hideCheck.setSelected(!section.getVisible());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm").withZone(ZoneId.systemDefault());

                createdByLabel.setText(section.getCreatedBy().getUsername());
                createdOnLabel.setText(formatter.format(section.getCreatedOn()));

                if (section.getChangeBy() != null) {
                    changeByLabel.setText(section.getChangeBy().getUsername());
                    changeOnLabel.setText(formatter.format(section.getChangeOn()));
                }
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
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isCreate)
                {
                    addButton.setText("Отмена");
                    isCreate = true;

                    sectionList.clearSelection();
                    sectionList.setEnabled(false);
                    sectionHeader.setText("");
                    sectionBody.setText("");
                    hideCheck.setSelected(false);
                    createdByLabel.setText("");
                    createdOnLabel.setText("");
                    changeByLabel.setText("");
                    changeOnLabel.setText("");
                }
                else
                {
                    addButton.setText("Добавить");
                    isCreate = false;

                    sectionList.clearSelection();
                    sectionList.setEnabled(true);
                    sectionHeader.setText("");
                    sectionBody.setText("");
                    hideCheck.setSelected(false);
                    createdByLabel.setText("");
                    createdOnLabel.setText("");
                    changeByLabel.setText("");
                    changeOnLabel.setText("");
                }
            }
        });
        saveSectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isCreate & sectionList.getSelectedIndex() == -1)
                {
                    messageHeader("Выберите карточку или создайте новую");
                }

                if (sectionBody.getText().length() > 8000)
                {
                    messageHeader("Длина текста статьи должна быть меньше 8000 символов");
                    return;
                }

                if (isCreate)
                {
                    if (sectionHeader.getText().length() < 3)
                    {
                        messageHeader("Заполните заголовок, минимальная длина 3");
                    }
                    else {
                       createSection();
                    }
                }

                if (sectionList.getSelectedIndex() != -1)
                {
                    SectionUpdateRequest request = new SectionUpdateRequest();
                    request.setId(sectionsResponse.getSectionList().get(sectionList.getSelectedIndex()).getId());
                    request.setHeader(sectionHeader.getText());
                    request.setBody(sectionBody.getText());
                    request.setVisible(!hideCheck.getModel().isSelected());
                    request.setUserId(response.getId());
                }
            }
        });
    }

    private void messageHeader(String string)
    {
        headerText = sectionHeader.getText();
        sectionHeader.setText(string);
        sectionHeader.setForeground(Color.RED);
        sectionHeader.setEditable(false);
        saveSectionButton.setEnabled(false);

        clearHead = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearHeader();
            }
        });

        clearHead.setRepeats(false);
        clearHead.start();
    }

    private void clearHeader()
    {
        sectionHeader.setText("");
        sectionHeader.setForeground(new Color(252, 253, 255));
        sectionHeader.setEditable(true);

        sectionHeader.setText(headerText);

        saveSectionButton.setEnabled(true);
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
                    Integer index = sectionList.getSelectedIndex();
                    model.clear();
                    model.addAll(sectionsResponse.getSectionList().stream().map(x -> x.getHeader()).toList());
                    sectionList.setSelectedIndex(index);
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
                    sectionList.clearSelection();
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

    private void createSection()
    {
        SwingWorker worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                userService = new UserService();
                JwtResponse jwtResponse = userService.authenticateUserSync(loginRequest);

                SectionRequest request = new SectionRequest();
                request.setHeader(sectionHeader.getText());
                request.setBody(sectionBody.getText());
                request.setVisible(!hideCheck.getModel().isSelected());
                request.setUserId(response.getId());

                sectionService = new SectionService();
                String string = sectionService.createSection(jwtResponse.getAccessToken(), request);

                return null;
            }
            protected void done() {
                try {
                    get();
                    getSections();
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
        setSize(1200, 800);
        setMinimumSize(new Dimension(1200, 800));
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
