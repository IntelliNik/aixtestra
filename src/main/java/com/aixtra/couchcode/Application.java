package com.aixtra.couchcode;

import io.micronaut.runtime.Micronaut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
