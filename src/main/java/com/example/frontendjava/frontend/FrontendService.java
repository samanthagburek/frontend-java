package com.example.frontendjava.frontend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FrontendService {

    private final HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080";

    public void fetchDetectionRules(FrontendCallback callback) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/rules"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(callback::onSuccess)
                .exceptionally(ex -> {
                    callback.onError(ex);
                    return null;
                });
    }

    public void updateDetectionRulePatch(int ruleId, String jsonPayload, FrontendCallback callback) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/rules/" + ruleId))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(callback::onSuccess)
                .exceptionally(ex -> {
                    callback.onError(ex);
                    return null;
                });
    }

    public void fetchLogEntries(FrontendCallback callback) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/logs")) // Update with your actual endpoint
                .GET()
                .build();

        sendRequest(request, callback);
    }

    public void fetchAlerts(FrontendCallback callback) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/alerts")) // Adjust endpoint as needed
                .GET()
                .build();

        sendRequest(request, callback);
    }

    public void postNewDetectionRule(String jsonPayload, FrontendCallback callback) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/rules"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(callback::onSuccess)
                .exceptionally(ex -> {
                    callback.onError(ex);
                    return null;
                });
    }

    // Callback interface
    public interface FrontendCallback {
        void onSuccess(String response);
        void onError(Throwable throwable);
    }

    private void sendRequest(HttpRequest request, FrontendCallback callback) {
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(callback::onSuccess)
                .exceptionally(ex -> {
                    callback.onError(ex);
                    return null;
                });
    }
}
