package irs.client.irs.WebClient.Services;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import irs.server.irs_server.models.User;
import irs.server.irs_server.payload.request.LoginRequest;
import irs.server.irs_server.payload.request.OrderRequest;
import irs.server.irs_server.payload.request.SectionRequest;
import irs.server.irs_server.payload.request.SectionUpdateRequest;
import irs.server.irs_server.payload.response.JwtResponse;
import irs.server.irs_server.payload.response.SectionsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jdbc.repository.query.StringBasedJdbcQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Service
public class SectionService {
    @Value("${irs.app.connectionString}")
    private String Base_URL = "http://localhost:3456/api/";
    private WebClient client;

    public SectionService()
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

    public SectionsResponse getOrderSections(String token)
    {
        return client
                .get()
                .uri(Base_URL + "section/getOrderSections")
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .onStatus(HttpStatus::isError,
                        (it -> handleError(it.statusCode().getReasonPhrase())))
                .bodyToMono(SectionsResponse.class)
                .block();
    }

    public SectionsResponse getSearchSections(String token, String string)
    {
        return  client
                .get()
                .uri(Base_URL + "section/getSearchSections/" + string)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .onStatus(HttpStatus::isError,
                        (it -> handleError(it.statusCode().getReasonPhrase())))
                .bodyToMono(SectionsResponse.class)
                .block();
    }

    public HttpStatus createSection(String token, SectionRequest request)
    {
        WebClient.ResponseSpec response = client
                .put()
                .uri(Base_URL + "section/createSection")
                .headers(headers -> {
                    headers.setBearerAuth(token);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(Mono.just(request), SectionRequest.class)
                .retrieve();

        return Optional.of(response.toBodilessEntity().block().getStatusCode()).get();
    }

    public HttpStatus updateSection(String token, SectionUpdateRequest request)
    {
        WebClient.ResponseSpec response = client
                .post()
                .uri(Base_URL + "section/updateSection")
                .headers(headers -> {
                    headers.setBearerAuth(token);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(Mono.just(request), SectionRequest.class)
                .retrieve();

        return Optional.of(response.toBodilessEntity().block().getStatusCode()).get();
    }

    public HttpStatus updateOrderSection(String token, OrderRequest request)
    {
        WebClient.ResponseSpec response = client
                .post()
                .uri(Base_URL + "section/updateOrderSection")
                .headers(headers -> {
                    headers.setBearerAuth(token);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(Mono.just(request), SectionRequest.class)
                .retrieve();

        return Optional.of(response.toBodilessEntity().block().getStatusCode()).get();
    }

    private Mono<? extends Throwable> handleError(String message) {

        return Mono.error(new RuntimeException(message));
    }
}
