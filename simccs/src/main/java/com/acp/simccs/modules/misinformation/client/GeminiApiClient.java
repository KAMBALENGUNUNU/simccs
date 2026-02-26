package com.acp.simccs.modules.misinformation.client;

import com.acp.simccs.modules.misinformation.dto.AiAnalysisRequest;
import com.acp.simccs.modules.misinformation.dto.GeminiApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Component
public class GeminiApiClient {

    private final RestClient restClient;
    private final String apiUrl;
    private final String apiKey;

    public GeminiApiClient(
            RestClient.Builder restClientBuilder,
            @Value("${ai.gemini.api-url}") String apiUrl,
            @Value("${ai.gemini.api-key}") String apiKey) {
        this.restClient = restClientBuilder.build();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    /**
     * Sends the analysis request to the Gemini API.
     * 
     * @param request the analysis payload containing the prompt
     * @return the raw API response wrapper
     */
    public GeminiApiResponse analyze(AiAnalysisRequest request) {
        log.debug("Sending analysis request to Gemini API");
        try {
            return restClient.post()
                    .uri(apiUrl)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(GeminiApiResponse.class);
        } catch (RestClientResponseException e) {
            log.error("HTTP Error communicating with Gemini API. Status: {}, Response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to analyze content using AI: HTTP " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Unexpected error during Gemini API call", e);
            throw new RuntimeException("Unexpected error during AI analysis", e);
        }
    }
}
