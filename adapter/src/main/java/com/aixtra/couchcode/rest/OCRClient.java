package com.aixtra.couchcode.rest;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

@Client("dns://ocr")
public interface OCRClient {
    @Get(value = "/compute", consumes = "data/raw", produces = "application/json")
    Mono<byte[]> recognize(@Body byte[] image);
}
