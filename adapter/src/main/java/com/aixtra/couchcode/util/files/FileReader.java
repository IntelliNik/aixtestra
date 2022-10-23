package com.aixtra.couchcode.util.files;

import io.micronaut.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Bean
public class FileReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileReader.class);

    public byte[] readFile(String path) {
        LOGGER.debug("Reading file {}.", path);
        try (InputStream input = new FileInputStream(path)) {
            return input.readAllBytes();
        } catch (IOException e) {
            LOGGER.warn("An error occurred reading the file {}.", path, e);
            return new byte[0];
        }
    }
}
