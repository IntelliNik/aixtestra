package com.aixtra.couchcode;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OpenAPIDefinition(
    info = @Info(
            title = "code-your-couch",
            version = "0.0"
    )
)
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) {
        LOGGER.trace("This is a trace");
        LOGGER.debug("This is a debug");
        LOGGER.info("This is a info");
        LOGGER.warn("This is a warn");
        LOGGER.error("This is a error");
        Micronaut.run(Application.class, args);
    }
}
