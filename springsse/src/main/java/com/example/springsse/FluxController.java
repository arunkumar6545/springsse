package com.example.springsse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;
import rx.subjects.BehaviorSubject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RestController
public class FluxController {

    BehaviorSubject<String> stringBehaviorSubject;

    public FluxController() {
        this.stringBehaviorSubject = BehaviorSubject.create();
        this.runflux();
    }

    private final Set<SseEmitter> sseEmitters = new HashSet<>();
    private int count;
    FluxProcessor<String, String> stringFluxProcessor = EmitterProcessor.create();
    FluxSink<String> sink = stringFluxProcessor.sink();


    @GetMapping("/publish")
    public String runflux() {
        count++;
        this.stringBehaviorSubject.onNext("hi" + count);
        sink.next("hi" + count);

        return "done";
    }

    @GetMapping(path = "/stream-flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamFlux2() {
        return stringFluxProcessor.map(s -> s);
    }


    @GetMapping(path = "/stream-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamFlux() {
        SseEmitter emitter = new SseEmitter();
        sseEmitters.add(emitter);
        System.out.println(sseEmitters.size());
        this.stringBehaviorSubject.subscribe(s -> {
            try {
                emitter.send(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        emitter.onCompletion(() -> {
            this.sseEmitters.remove(emitter);
            System.out.println("completed");
        });
        emitter.onError((e) -> {
            this.sseEmitters.remove(emitter);
            System.out.println("error");
        });

        return emitter;
    }


}
