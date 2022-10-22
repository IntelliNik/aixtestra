package com.aixtra.couchcode.util.startup;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

@Singleton
public class StartupLatch {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartupLatch.class);
    private final CountDownLatch latch = new CountDownLatch(1);

    public void awaitStartup() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for startup", e);
        }
    }

    public void startupComplete() {
        latch.countDown();
    }
}
