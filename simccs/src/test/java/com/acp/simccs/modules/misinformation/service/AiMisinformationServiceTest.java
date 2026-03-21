package com.acp.simccs.modules.misinformation.service;

import com.acp.simccs.modules.misinformation.client.FactCheckClient;
import com.acp.simccs.modules.misinformation.client.GeminiApiClient;
import com.acp.simccs.modules.misinformation.dto.AiAnalysisRequest;
import com.acp.simccs.modules.misinformation.dto.AiAnalysisResponse;
import com.acp.simccs.modules.misinformation.dto.FactCheckResponse;
import com.acp.simccs.modules.misinformation.dto.GeminiApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiMisinformationServiceTest {

    @Mock
    private GeminiApiClient geminiApiClient;

    @Mock
    private FactCheckClient factCheckClient;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AiMisinformationService aiMisinformationService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void analyzeReport_ShouldKeepGeminiResultsSeparateFromFactCheck() throws Exception {
        // Arrange
        String report = "Test report content";

        GeminiApiResponse geminiResponse = createMockGeminiResponse(
                "{\"confidenceScore\": 0.45, \"reason\": \"Initial logic\"}");
        when(geminiApiClient.analyze(any(AiAnalysisRequest.class))).thenReturn(geminiResponse);

        FactCheckResponse factCheckResponse = new FactCheckResponse();
        FactCheckResponse.Claim claim = new FactCheckResponse.Claim();
        claim.setText("Sample Claim");
        FactCheckResponse.ClaimReview review = new FactCheckResponse.ClaimReview();
        review.setTextualRating("False");
        review.setUrl("http://factcheck.org");
        claim.setClaimReview(List.of(review));
        factCheckResponse.setClaims(List.of(claim));

        when(factCheckClient.search(anyString())).thenReturn(factCheckResponse);

        // Act
        AiAnalysisResponse result = aiMisinformationService.analyzeReport(report);

        // Assert
        assertEquals(0.45, result.getConfidenceScore(), "Confidence score should not be boosted by Fact Check hits");
        assertEquals("Initial logic", result.getReason(), "Reason should not be appended with Fact Check info");
        assertNotNull(result.getFactCheckHits());
        assertEquals(1, result.getFactCheckHits().size());
        assertEquals("False", result.getFactCheckHits().get(0).getRating());
    }

    private GeminiApiResponse createMockGeminiResponse(String json) {
        GeminiApiResponse response = new GeminiApiResponse();
        GeminiApiResponse.Candidate candidate = new GeminiApiResponse.Candidate();
        GeminiApiResponse.Content content = new GeminiApiResponse.Content();
        GeminiApiResponse.Part part = new GeminiApiResponse.Part();
        part.setText(json);
        content.setParts(List.of(part));
        candidate.setContent(content);
        response.setCandidates(List.of(candidate));
        return response;
    }
}
