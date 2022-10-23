package com.aixtra.couchcode.util.files;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Singleton
public class FileStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStore.class);
    private final Set<String> requestedFiles = new HashSet<>();
    private final Set<String> ratingFiles = new HashSet<>();

    FileStore() {
        try (Stream<Path> stream = Files.walk(Path.of("images"))) {
            stream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            LOGGER.warn("An error occurred deleting the images directory.", e);
        }
    }

    public String newFile() {
        String name = "images/requested/image_" + requestedFiles.size() + ".png";
        new File(name).getParentFile().mkdirs();
        requestedFiles.add(name);
        return name;
    }

    public String newRatingFile() {
        String name = "images/rating/image_" + ratingFiles.size() + ".png";
        new File(name).getParentFile().mkdirs();
        ratingFiles.add(name);
        return name;
    }

    public Set<String> getFiles() {
        return requestedFiles;
    }

    public Set<String> getRatingFiles() {
        return ratingFiles;
    }
}
