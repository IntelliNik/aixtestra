package com.aixtra.couchcode;

import com.aixtra.couchcode.client.challenge.api.RatingApi;
import com.aixtra.couchcode.util.startup.StartupLatch;
import io.micronaut.context.annotation.Context;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Context
public class ChallengeRequester {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChallengeRequester.class);
    private final RatingApi rating;
    ChallengeRequester(RatingApi rating) {
        this.rating = rating;
    }


    @PostConstruct
    public void register(StartupLatch latch) {
        latch.awaitStartup();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            LOGGER.info("Requesting bearer token");
            rating.register()
                    .retryWhen(Retry.backoff(5, Duration.ofSeconds(2))
                            .doBeforeRetry(signal -> {
                                if (signal.failure() instanceof HttpClientResponseException ex) {
                                    LOGGER.warn("Error while registering for rating status {}", ex.getStatus(), ex);
                                    LOGGER.error("Header: {}", ex.getResponse().getHeaders().asMap());
                                    ex.getResponse().getBody().ifPresent(body -> LOGGER.error("Error response: {}", body));
                                } else {
                                    LOGGER.warn("Error while registering for rating {} times:  {} , retrying...", signal.totalRetries(), signal.failure().getMessage());
                                }
                            })
                    ).doOnError((err) -> LOGGER.error("Could not register for rating", err))
                    .doOnNext((res) -> LOGGER.info("Registered for next rating run response: {}", res))
                    .subscribe();

        }, 0, 15, TimeUnit.MINUTES);

    }
}
