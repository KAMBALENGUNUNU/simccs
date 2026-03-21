package com.acp.simccs.modules.misinformation.client;

import com.acp.simccs.modules.misinformation.dto.FactCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Component
public class FactCheckClient {

    private final RestClient restClient;
    private final String apiUrl;
    private final String apiKey;

    public FactCheckClient(
            RestClient.Builder restClientBuilder,
            @Value("${ai.factcheck.api-url}") String apiUrl,
            @Value("${ai.factcheck.api-key}") String apiKey) {
        this.restClient = restClientBuilder.build();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    /**
     * Searches for fact checks related to a query using Google Fact Check Tools
     * API.
     * 
     * @param query the text or claim to check
     * @return the fact check response
     */
    public FactCheckResponse search(String query) {
        log.debug("Searching Google Fact Check API for: {}", query);
        try {
            return restClient.get()
                    .uri(apiUrl + "?query={query}&key={key}", query, apiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(FactCheckResponse.class);
        } catch (RestClientResponseException e) {
            log.error("HTTP Error communicating with Fact Check API. Status: {}, Response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to check facts using Google API: HTTP " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Unexpected error during Fact Check API call", e);
            throw new RuntimeException("Unexpected error during Fact Check analysis", e);
        }
    }
}
