package com.aixtra.couchcode;

import com.aixtra.couchcode.client.challenge.api.RatingApi;
import io.micronaut.context.annotation.Context;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void register() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            LOGGER.info("Registering for next rating run");
            rating.register().subscribe();
        }, 0, 30, TimeUnit.MINUTES);

    }
}
