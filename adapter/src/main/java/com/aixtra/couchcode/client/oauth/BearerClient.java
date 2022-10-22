package com.aixtra.couchcode.client.oauth;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static io.micronaut.http.MediaType.APPLICATION_FORM_URLENCODED;

@Client("${backend.keycloakUrl}")
public abstract class BearerClient {
    private final String secret;
    private final String id;

    BearerClient(@NonNull @Value("${backend.client-id}") String id, @NonNull @Value("${backend.client-secret}") String secret) {
        this.id = id;
        this.secret = secret;
    }

    public Mono<JSONObject> getNewBearerToken() {
        try {
            String dataString = getDataString(Map.of(
                    "grant_type", "client_credentials",
                    "client_id", id,
                    "client_secret", secret
            ));
            return Mono.defer(()->login(dataString))
                    .map(JSONObject::new);
        } catch (UnsupportedEncodingException e) {
            return Mono.error(e);
        }
    }

    private @NotNull String getDataString(@NotNull Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return result.toString();
    }

    @Post(produces = APPLICATION_FORM_URLENCODED)
    abstract Mono<String> login(@Body String body);
}
