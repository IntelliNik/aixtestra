package com.aixtra.couchcode.util.files;

import io.micronaut.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Bean
public class FileBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileBuilder.class);
    private final FileStore store;

    FileBuilder(FileStore store) {
        this.store = store;
    }

    public String writeToFile(boolean ratingRun, byte[] content) {
        String name = ratingRun ?
                store.newRatingFile() :
                store.newFile();

        try {
            OutputStream outputStream = new FileOutputStream(name);
            outputStream.write(content);
            outputStream.close();
            LOGGER.info("Successfully wrote the file {}.", name);
        } catch (IOException e) {
            LOGGER.warn("An error occurred writing the file {}.", name, e);
        }
        return name;
    }
}
