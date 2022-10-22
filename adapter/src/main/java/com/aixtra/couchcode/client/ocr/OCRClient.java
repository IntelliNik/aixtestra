package com.aixtra.couchcode.client.ocr;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

@Client("${ocr.url}")
public abstract class OCRClient {

    public Mono<byte[]> recognize(byte[] image) {
        return Mono.defer(() -> rec(image));
    }

    @Post("/compute")
    abstract Mono<byte[]> rec(@Body byte[] image);

    @Get("/ping")
    public abstract Mono<HttpResponse<?>> ping();
}
