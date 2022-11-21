package irs.client.irs;

import irs.client.irs.Forms.MainForm;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.util.Optional;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = createApplicationContext(args);
        displayMainFrame(context);
    }

    private static ConfigurableApplicationContext createApplicationContext(String[] args) {
        return new SpringApplicationBuilder(Application.class)
                .headless(false)
                .run(args);
    }

    private static void displayMainFrame(ConfigurableApplicationContext context) {
        SwingUtilities.invokeLater(() -> {
            MainForm mainForm = context.getBean(MainForm.class);
            mainForm.open();
        });
    }

}
