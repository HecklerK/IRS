package irs.client.irs.Forms;

import irs.client.irs.WebClient.Services.SectionService;
import irs.client.irs.WebClient.Services.UserService;
import irs.server.irs_server.models.Section;
import irs.server.irs_server.payload.request.LoginRequest;
import irs.server.irs_server.payload.request.OrderRequest;
import irs.server.irs_server.payload.request.SectionRequest;
import irs.server.irs_server.payload.request.SectionUpdateRequest;
import irs.server.irs_server.payload.response.JwtResponse;
import irs.server.irs_server.payload.response.SectionsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
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
    private Boolean isChangeOrder;
    private int indexChangeOrderSection;

    public ChangeForm(JwtResponse response, LoginRequest loginRequest)
    {
        this.response = response;
        this.loginRequest = loginRequest;
        this.sectionsResponse = new SectionsResponse();
        this.model = new DefaultListModel<>();
        this.sectionList.setModel(model);
        this.isSearch = false;
        this.isCreate = false;
        this.isChangeOrder = false;

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
                int index = sectionList.getSelectedIndex();

                if (index == -1)
                {
                    sectionHeader.setText("");
                    sectionBody.setText("");
                    return;
                }

                if (isChangeOrder)
                    return;

                Section section = sectionsResponse.getSectionList().get(index);
                sectionHeader.setText(section.getHeader());
                sectionBody.setText(section.getBody());
                sectionBody.setCaretPosition(0);
                if(section.getVisible() != null && !section.getVisible())
                    hideCheck.setSelected(!section.getVisible());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm").withZone(ZoneId.systemDefault());

                createdByLabel.setText(section.getCreatedBy().getUsername());
                createdOnLabel.setText(formatter.format(LocalDateTime.ofInstant(section.getCreatedOn(), ZoneId.of("UTC"))));

                if (section.getChangeBy() != null) {
                    changeByLabel.setText(section.getChangeBy().getUsername());
                    changeOnLabel.setText(formatter.format(LocalDateTime.ofInstant(section.getChangeOn(), ZoneId.of("UTC"))));
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

                        enableChangeOrderControls(false);
                    }
                }
                else
                {
                    searchButton.setText("Поиск");
                    isSearch = false;

                    getSections();

                    enableChangeOrderControls(true);
                }
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeAddButton();
            }
        });
        saveSectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isCreate & sectionList.getSelectedIndex() == -1)
                {
                    messageHeader("Выберите карточку или создайте новую");
                    return;
                }

                if (sectionBody.getText().length() > 8000)
                {
                    messageHeader("Длина текста статьи должна быть меньше 8000 символов");
                    return;
                }

                if (sectionHeader.getText().length() < 3)
                {
                    messageHeader("Заполните заголовок, минимальная длина 3");
                    return;
                }

                if (isCreate)
                {
                    createSection();
                    return;
                }

                if (sectionList.getSelectedIndex() != -1)
                {
                    updateSection();
                }
            }
        });
        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sectionList.getSelectedIndex() == -1 || model.getSize() < 2)
                {
                    messageHeader("Выберите карточку или создайте больше карточек");
                    return;
                }

                if (sectionList.getSelectedIndex() == model.getSize() - 1)
                {
                    messageHeader("Выбранный элемент последний");
                    return;
                }

                if (!isChangeOrder)
                    indexChangeOrderSection = sectionList.getSelectedIndex();

                isChangeOrder = true;

                int index = sectionList.getSelectedIndex();
                int index2 = index + 1;

                String element = model.getElementAt(index);
                String element2 = model.getElementAt(index2);

                model.setElementAt(element, index2);
                model.setElementAt(element2, index);
                sectionList.setSelectedIndex(index2);
                enabledSectionControls(false);
            }
        });
        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sectionList.getSelectedIndex() == -1 || model.getSize() < 2)
                {
                    messageHeader("Выберите карточку или создайте больше карточек");
                    return;
                }

                if (sectionList.getSelectedIndex() == 0)
                {
                    messageHeader("Выбранный элемент первый");
                    return;
                }

                if (!isChangeOrder)
                    indexChangeOrderSection = sectionList.getSelectedIndex();

                isChangeOrder = true;

                int index = sectionList.getSelectedIndex();
                int index2 = index - 1;

                String element = model.getElementAt(index);
                String element2 = model.getElementAt(index2);

                model.setElementAt(element, index2);
                model.setElementAt(element2, index);
                sectionList.setSelectedIndex(index2);
                enabledSectionControls(false);
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateOrderSection();
                enabledSectionControls(true);
                getSections();
                isChangeOrder = false;
            }
        });
    }

    private void enabledSectionControls(boolean value)
    {
        saveButton.setEnabled(!value);
        sectionList.setEnabled(value);
        addButton.setEnabled(value);
        sectionHeader.setEditable(value);
        sectionBody.setEditable(value);
        saveSectionButton.setEnabled(value);
        hideCheck.setEnabled(value);
        searchButton.setEnabled(value);
    }

    private void enableChangeOrderControls(boolean value)
    {
        saveButton.setEnabled(value);
        upButton.setEnabled(value);
        downButton.setEnabled(value);
    }

    private void messageHeader(String string)
    {
        headerText = sectionHeader.getText();
        sectionHeader.setText(string);
        sectionHeader.setForeground(Color.RED);
        sectionHeader.setEditable(false);
        saveSectionButton.setEnabled(false);
        sectionList.setEnabled(false);
        addButton.setEnabled(false);
        saveButton.setEnabled(false);
        upButton.setEnabled(false);
        downButton.setEnabled(false);


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
        sectionList.setEnabled(true);
        addButton.setEnabled(true);
        saveButton.setEnabled(true);
        upButton.setEnabled(true);
        downButton.setEnabled(true);
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
                    if (sectionsResponse.getSectionList().isEmpty()) {
                        messageHeader("Статьи не были найдены");
                        return;
                    }
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
        SwingWorker worker = new SwingWorker<HttpStatus, Void>() {
            @Override
            protected HttpStatus doInBackground() throws Exception {
                userService = new UserService();
                JwtResponse jwtResponse = userService.authenticateUserSync(loginRequest);

                SectionRequest request = new SectionRequest();
                request.setHeader(sectionHeader.getText());
                request.setBody(sectionBody.getText());
                request.setVisible(!hideCheck.getModel().isSelected());
                request.setUserId(response.getId());

                sectionService = new SectionService();
                return sectionService.createSection(jwtResponse.getAccessToken(), request);
            }
            protected void done() {
                try {
                    HttpStatus status = get();

                    if (status == HttpStatus.OK){
                        getSections();
                        changeAddButton();
                    }
                    else
                    {
                        retrieveHttpErrors(status);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        worker.execute();
    }

    private void updateSection()
    {
        SwingWorker worker = new SwingWorker<HttpStatus, Void>() {
            @Override
            protected HttpStatus doInBackground() throws Exception {
                userService = new UserService();
                JwtResponse jwtResponse = userService.authenticateUserSync(loginRequest);

                SectionUpdateRequest request = new SectionUpdateRequest();
                request.setId(sectionsResponse.getSectionList().get(sectionList.getSelectedIndex()).getId());
                request.setHeader(sectionHeader.getText());
                request.setBody(sectionBody.getText());
                request.setVisible(!hideCheck.getModel().isSelected());
                request.setUserId(response.getId());

                sectionService = new SectionService();
                return sectionService.updateSection(jwtResponse.getAccessToken(), request);
            }
            protected void done() {
                try {
                    HttpStatus status = get();

                    if (status == HttpStatus.OK){
                    }
                    else
                    {
                        retrieveHttpErrors(status);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        worker.execute();
    }

    private void updateOrderSection()
    {
        SwingWorker worker = new SwingWorker<HttpStatus, Void>() {
            @Override
            protected HttpStatus doInBackground() throws Exception {
                userService = new UserService();
                JwtResponse jwtResponse = userService.authenticateUserSync(loginRequest);

                OrderRequest request = new OrderRequest();
                request.setSection_id(sectionsResponse.getSectionList().get(indexChangeOrderSection).getId());
                request.setNumber((long)sectionList.getSelectedIndex() + 1);

                sectionService = new SectionService();
                return sectionService.updateOrderSection(jwtResponse.getAccessToken(), request);
            }
            protected void done() {
                try {
                    HttpStatus status = get();

                    if (status == HttpStatus.OK){
                        getSections();
                    }
                    else
                    {
                        retrieveHttpErrors(status);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        worker.execute();
    }

    private void changeAddButton()
    {
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

    private void retrieveHttpErrors(HttpStatus status)
    {
        String message = "";
        switch (status.value())
        {
            case 404:
               message = "Элемент не найден";
               break;

            default:
                message = "Код не известен: " + status.toString();
                break;
        }

        messageHeader(message);
    }

    public void open(){
        setTitle("IRS");
        setContentPane(new ChangeForm(response, loginRequest).panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Icons/scooter-Freepik.png")));
        setMinimumSize(new Dimension(1200, 800));
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
