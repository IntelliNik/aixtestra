package com.aixtra.couchcode.server.web;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;


@Controller("/gui")
public class WebServer {
    private static final Logger LOG = LoggerFactory.getLogger(WebServer.class);

    @Get
    public HttpResponse<?> index() {
        LOG.info("Website requested");
        return HttpResponse.redirect(URI.create("/gui/index.html"));
    }
}
