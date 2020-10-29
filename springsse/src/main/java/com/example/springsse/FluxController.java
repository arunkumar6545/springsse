package com.example.springsse;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import rx.subjects.BehaviorSubject;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@RestController
public class FluxController {

    BehaviorSubject<String> stringBehaviorSubject;

    public FluxController() {
        this.stringBehaviorSubject = BehaviorSubject.create();
    }

    private Set<SseEmitter> sseEmitters = new HashSet<SseEmitter>();

    @Scheduled(fixedRate = 1000)
    public void runflux() {
        System.out.println("ran now");
        this.stringBehaviorSubject.onNext("Flux - " + LocalTime.now().toString());

    }

    @GetMapping(path = "/stream-flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamFlux() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "Flux - " + LocalTime.now().toString());
    }

    public void readevents(Flux f){
        this.stringBehaviorSubject.subscribe(s -> f.map(o -> s));
    }



    @GetMapping(path = "/stream-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamFlux(String a) {
        SseEmitter emitter = new SseEmitter();
        sseEmitters.add(emitter);
        System.out.println(sseEmitters.size());
        this.stringBehaviorSubject.subscribe(s -> {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .data("SSE MVC - " + LocalTime.now().toString())
                    .name("sse event - mvc");
            try {
                emitter.send(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        emitter.onCompletion(() -> this.sseEmitters.remove(emitter));
        emitter.onError((e) -> this.sseEmitters.remove(emitter));

        return emitter;
    }


}
