package com.aixtra.couchcode.controller;

import com.aixtra.couchcode.server.controller.StudentsController;
import com.aixtra.couchcode.server.model.Solution;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.uri.UriTemplate;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;


/**
 * API tests for StudentsController
 */
@MicronautTest
public class StudentsControllerTest {

    @Inject
    EmbeddedServer server;

    @Inject
    @Client
    HttpClient client;

    @Inject
    StudentsController controller;

    /**
     * This test is used to validate the implementation of ping() method
     * The method should: A simple endpoint to test interaction
     * This endpoint is used to determine, whether or not the system is  generally available. It is used before every rating run. System which  are not available or unable to react to the ping in time, will be  disqualified and not further considered during the rating run.
     * TODO fill in the parameters and test return value.
     */
    @Test
    @Disabled("Not Implemented")
    void pingMethodTest() {
        // given

        // when
        //controller.ping().block();

        // then
        Assertions.assertTrue(true);
    }

    /**
     * This test is used to check that the api available to client through
     * '/ping' to the features of ping() works as desired.
     *
     * TODO fill in the request parameters and test response.
     */
    @Test
    @Disabled("Not Implemented")
    void pingClientApiTest() {
        // given
        String uri = UriTemplate.of("/ping").expand(new HashMap<>());
        MutableHttpRequest<?> request = HttpRequest.GET(uri)
            .accept("application/json");

        // when
        HttpResponse<?> response = client.toBlocking().exchange(request); // To retrieve body you must specify required type (e.g. Map.class) as second argument 

        // then
        Assertions.assertEquals(HttpStatus.OK, response.status());
    }

    /**
     * This test is used to validate the implementation of solve() method
     * The method should: Tries to extract the information of a given task.
     * For a task image, provided in the request body in base64 format, the service tries to retrieve the information  about the product and embed it in the provided model.
     * TODO fill in the parameters and test return value.
     */
    @Test
    @Disabled("Not Implemented")
    void solveMethodTest() {
        // given
        //CompletedFileUpload _body = null;

        // when
        //Solution result = controller.solve(_body).block();

        // then
        Assertions.assertTrue(true);
    }

    /**
     * This test is used to check that the api available to client through
     * '/solve' to the features of solve() works as desired.
     *
     * TODO fill in the request parameters and test response.
     */
    @Test
    @Disabled("Not Implemented")
    void solveClientApiTest(){
        // given
        CompletedFileUpload body = null;
        String uri = UriTemplate.of("/solve").expand(new HashMap<>());
        MutableHttpRequest<?> request = HttpRequest.POST(uri, body)
            .accept("application/json");

        // when
        HttpResponse<?> response = client.toBlocking().exchange(request, Solution.class);

        // then
        Assertions.assertEquals(HttpStatus.OK, response.status());
    }

}
