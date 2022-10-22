package com.aixtra.couchcode.client.ocr;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

@Client("${ocr.url}")
public interface OCRClient {
    @Post(value = "/compute", consumes = "image/png", produces = "application/json")
    Mono<byte[]> recognize(@Body byte[] image);
}
