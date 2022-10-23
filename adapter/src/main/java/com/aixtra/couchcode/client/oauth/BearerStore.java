package com.aixtra.couchcode.client.oauth;

import com.aixtra.couchcode.util.data.option.Option;
import com.aixtra.couchcode.util.data.option.Some;
import com.aixtra.couchcode.util.startup.StartupLatch;
import jakarta.inject.Singleton;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class BearerStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(BearerStore.class);
    private final AtomicReference<String> bearer;
    private final BearerClient client;

    BearerStore(BearerClient client, StartupLatch latch) {
        this.client = client;
        this.bearer = new AtomicReference<>();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        new Runnable() {
            @Override
            public void run() {
                int retrySecs = 1;
                try {
                    retrySecs = getBearer();
                    latch.startupComplete();
                } catch (Exception e) {
                    LOGGER.warn("Get Bearer errored", e);
                }
                executor.schedule(this, retrySecs, TimeUnit.SECONDS);
            }
        }.run();
    }

    public String currentBearerAsAuth() {
        return "Bearer " + bearer.get();
    }

    /**
     * Get a new bearer token
     *
     * @return time in seconds till refresh
     */
    private int getBearer() {
        Option<JSONObject> bearerResponse = Option.of(client.getNewBearerToken()
                .defaultIfEmpty(new JSONObject())
                .flatMap(json -> {
                    if (!json.has("access_token") || !json.has("expires_in")) {
                        LOGGER.trace("Invalid bearer Json response contains: token:{} expires_in:{}", json.has("access_token"), json.has("expires_in"));
                        return Mono.error(new IllegalArgumentException("Invalid bearer token response %s".formatted(json)));
                    } else {
                        LOGGER.trace("Got bearer response{}", json);
                        return Mono.just(json);
                    }
                })
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(2))
                        .doBeforeRetry(signal -> LOGGER.warn("Error while getting bearer {} times: {}, retrying...", signal.totalRetries() + 1, signal.failure().getMessage()))
                ).doOnError((err) -> LOGGER.error("Could not get bearer ", err))
                .blockOptional());

        if (bearerResponse instanceof Some<JSONObject> some) {
            JSONObject json = some.getValue();
            String accessToken = json.getString("access_token");
            bearer.set(accessToken);
            LOGGER.info("Got new bearer token: {}...", accessToken.substring(0, 8));
            return json.getInt("expires_in");
        } else {
            LOGGER.error("Could not get bearer token");
        }
        return 1;
    }
}
