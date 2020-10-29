package com.example.springsse;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalTime;

public class Consumer {
    public static void main(String[] args) {

    }
    public void consumeServerSentEvent() {
        WebClient client = WebClient.create("http://localhost:8080/sse-server");
        ParameterizedTypeReference<ServerSentEvent<String>> type
                = new ParameterizedTypeReference<ServerSentEvent<String>>() {};

        Flux<ServerSentEvent<String>> eventStream = client.get()
                .uri("/stream-flux")
                .retrieve()
                .bodyToFlux(type);

        eventStream.subscribe(
                content -> System.out.println(content),
                error -> System.out.println(error));
    }
}
