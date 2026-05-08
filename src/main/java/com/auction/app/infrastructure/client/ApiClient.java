package com.auction.app.infrastructure.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Component
public class ApiClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.api.base-url:http://localhost:8080}")
    private String baseUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * GET request
     */
    public <T> T get(String endpoint, Class<T> responseType, String authToken) throws Exception {
        String url = baseUrl + endpoint;
        log.debug("GET request to: {}", url);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET();

        if (authToken != null) {
            requestBuilder.header("Authorization", "Bearer " + authToken);
        }

        HttpRequest request = requestBuilder
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new ApiException("GET request failed with status " + response.statusCode() + ": " + response.body());
        }

        return objectMapper.readValue(response.body(), responseType);
    }

    /**
     * GET request without auth
     */
    public <T> T get(String endpoint, Class<T> responseType) throws Exception {
        return get(endpoint, responseType, null);
    }

    /**
     * POST request
     */
    public <T> T post(String endpoint, Object body, Class<T> responseType, String authToken) throws Exception {
        String url = baseUrl + endpoint;
        log.debug("POST request to: {} with body: {}", url, body);

        String jsonBody = objectMapper.writeValueAsString(body);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(new URI(url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        if (authToken != null) {
            requestBuilder.header("Authorization", "Bearer " + authToken);
        }

        HttpRequest request = requestBuilder
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new ApiException("POST request failed with status " + response.statusCode() + ": " + response.body());
        }

        return objectMapper.readValue(response.body(), responseType);
    }

    /**
     * POST request without auth
     */
    public <T> T post(String endpoint, Object body, Class<T> responseType) throws Exception {
        return post(endpoint, body, responseType, null);
    }

    public static class ApiException extends Exception {
        public ApiException(String message) {
            super(message);
        }

        public ApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
