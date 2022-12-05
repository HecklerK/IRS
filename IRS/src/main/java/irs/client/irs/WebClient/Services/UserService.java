package irs.client.irs.WebClient.Services;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.*;
import irs.server.irs_server.payload.request.LoginRequest;
import irs.server.irs_server.payload.response.JwtResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Value("${irs.app.connectionString}")
    private String Base_URL = "http://localhost:3456/api/";
    private WebClient client;
    public UserService()
    {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
    public Mono<JwtResponse> authenticateUser(LoginRequest loginRequest)
    {
        return client
                .post()
                .uri( Base_URL + "auth/signin")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(loginRequest))
                .retrieve()
                .bodyToMono(JwtResponse.class)
                .doOnError(error -> handleError(error.getMessage()));
    }

    public JwtResponse authenticateUserSync(LoginRequest loginRequest)
    {
        return client
                .post()
                .uri( Base_URL + "auth/signin")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(loginRequest))
                .retrieve()
                .bodyToMono(JwtResponse.class)
                .doOnError(error -> handleError(error.getMessage()))
                .block();
    }

    private Mono<? extends Throwable> handleError(String message) {
        return Mono.error(Exception::new);
    }
}
