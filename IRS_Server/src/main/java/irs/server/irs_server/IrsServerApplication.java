package irs.server.irs_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableGlobalAuthentication
public class IrsServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IrsServerApplication.class, args);
    }
}
