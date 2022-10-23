package com.aixtra.couchcode.server.web.api;

import com.aixtra.couchcode.client.challenge.model.TaskDifficulty;
import com.aixtra.couchcode.test.TestRunner;
import com.aixtra.couchcode.util.files.FileReader;
import com.aixtra.couchcode.util.files.FileStore;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.exceptions.HttpStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Set;

@Controller("/gui/api")
public class ApiServer {

    private final FileStore store;
    private final FileReader reader;
    private final TestRunner runner;

    ApiServer(FileStore store, FileReader reader, TestRunner runner) {
        this.store = store;
        this.reader = reader;
        this.runner = runner;
    }

    @Get("/images")
    public Map<String, Set<String>> getImages() {
        Set<String> ratingFiles = store.getRatingFiles();
        Set<String> requestedFiles = store.getFiles();
        return Map.of(
                "rating", ratingFiles,
                "requested", requestedFiles
        );
    }

    @Get(value = "/images/{type}/{name}", produces = MediaType.IMAGE_PNG)
    public byte[] getImage(String type, String name) {
        byte[] bytes = reader.readFile("images/" + type + "/" + name);
        if (bytes.length == 0) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Image not found");
        }
        return bytes;
    }

    @Get(value = "/request-new/{type}", produces = MediaType.APPLICATION_JSON)
    public Mono<String> requestNew(String type) {
        return runner.runNewTest(TaskDifficulty.fromValue(type.toUpperCase()));
    }
}
