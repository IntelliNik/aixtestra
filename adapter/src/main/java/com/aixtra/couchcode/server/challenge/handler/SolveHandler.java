package com.aixtra.couchcode.server.challenge.handler;

import com.aixtra.couchcode.client.ocr.OCRClient;
import com.aixtra.couchcode.util.data.option.Option;
import com.aixtra.couchcode.util.data.option.Some;
import io.micronaut.context.annotation.Bean;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Bean
public class SolveHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SolveHandler.class);
    private final OCRClient client;

    SolveHandler(OCRClient client) {
        this.client = client;
    }

    public @NotNull Mono<byte[]> solve(@NotNull Option<byte[]> body) {
        if (!(body instanceof Some<byte[]> data)) {
            return Mono.error(new HttpStatusException(HttpStatus.BAD_REQUEST, "No file uploaded"));
        }
        LOGGER.info("Trying to ping OCR");
        return client.ping()
                .doOnError((err) -> LOGGER.error("Pinging OCR errored", err))
                .doOnNext(resp -> LOGGER.info("Got ping Response-Code: {}", resp.getStatus()))
                .then(client.recognize(data.getValue())
                                .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
                                        .doBeforeRetry(signal -> LOGGER.info("Calling OCR failed {} times: {}, retrying...", signal.totalRetries(), signal.failure().getMessage()))
                                ).doOnNext(bytes -> {
                                    String json = new String(bytes, StandardCharsets.UTF_8);
                                    LOGGER.info("Got response from OCR: {}", json);
                                }).defaultIfEmpty(new byte[0]));
    }
}
