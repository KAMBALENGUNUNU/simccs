package com.acp.simccs.modules.misinformation.service;

import com.acp.simccs.modules.misinformation.client.GeminiApiClient;
import com.acp.simccs.modules.misinformation.dto.AiAnalysisRequest;
import com.acp.simccs.modules.misinformation.dto.AiAnalysisResponse;
import com.acp.simccs.modules.misinformation.dto.GeminiApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for acting as a bridge between the system and the AI
 * client
 * to detect misinformation in crisis reports.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiMisinformationService {

    private final GeminiApiClient geminiApiClient;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            You are a Senior Crisis Intelligence Analyst working for the Agence Congolaise de Presse (ACP) in the Democratic Republic of the Congo (DRC). 
            Your task is to analyze field crisis reports (which will primarily be in French) for misinformation, rumors, and contextual inaccuracies.

            CRITICAL RULES for your analysis:
            1. DRC Context Match: The event MUST make logical, geographical, and climatic sense within the DRC. If a report describes events that are impossible or highly improbable in the Congo (e.g., snowstorms, hurricanes, ocean tsunamis, subway train derailments) or references foreign cities/institutions as if they were local, you MUST assign a high confidence score (>0.80).
            2. Factuality: Flag hyperbole, known conspiracy theories, or panic-inducing claims lacking factual evidence.
            
            When assigning the reason, if it fails the DRC context check, explicitly state why it does not fit the reality of the Democratic Republic of the Congo.

            You must return ONLY a raw JSON object matching the exact structure below, with no markdown formatting, no code blocks, and no conversational text.

            Expected JSON structure:
            {
              "confidenceScore": <Double between 0.0 and 1.0 representing the likelihood of misinformation>,
              "reason": "<String explaining the specific reasons for this score based on the text>"
            }

            Text to analyze:
            """;

    /**
     * Analyzes a decrypted crisis report to detect if it contains misinformation.
     *
     * @param decryptedReport the plain text of the decrypted report
     * @return an {@link AiAnalysisResponse} containing the confidence score and
     *         reasoning
     */
    public AiAnalysisResponse analyzeReport(String decryptedReport) {
        log.debug("Starting misinformation analysis for report of length {}", decryptedReport.length());

        String fullPrompt = SYSTEM_PROMPT + decryptedReport;

        AiAnalysisRequest request = AiAnalysisRequest.builder()
                .contents(List.of(
                        AiAnalysisRequest.Content.builder()
                                .parts(List.of(
                                        AiAnalysisRequest.Part.builder()
                                                .text(fullPrompt)
                                                .build()))
                                .build()))
                .build();

        // 1. Call API
        GeminiApiResponse response = geminiApiClient.analyze(request);

        // 2. Extract JSON string from response
        String rawJson = extractTextFromResponse(response);

        // 3. Parse into the expected DTO
        try {
            return objectMapper.readValue(rawJson, AiAnalysisResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Gemini API response into AiAnalysisResponse. Raw response: {}", rawJson, e);
            throw new RuntimeException("Failed to parse AI response. Expected raw JSON but found: " + rawJson, e);
        }
    }

    private String extractTextFromResponse(GeminiApiResponse response) {
        if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
            GeminiApiResponse.Candidate candidate = response.getCandidates().get(0);
            if (candidate.getContent() != null && candidate.getContent().getParts() != null
                    && !candidate.getContent().getParts().isEmpty()) {
                String text = candidate.getContent().getParts().get(0).getText();

                // Keep only the text and clean up any accidental markdown formatting
                if (text != null) {
                    text = text.trim();
                    if (text.startsWith("```json")) {
                        text = text.substring(7);
                    } else if (text.startsWith("```")) {
                        text = text.substring(3);
                    }
                    if (text.endsWith("```")) {
                        text = text.substring(0, text.length() - 3);
                    }
                    return text.trim();
                }
            }
        }
        throw new RuntimeException("Invalid response structure received from Gemini API");
    }
}
