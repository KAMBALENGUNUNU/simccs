package com.acp.simccs.modules.crisis.service;

import com.acp.simccs.modules.communication.dto.MessageDTO;
import com.acp.simccs.modules.communication.service.ChatService;
import com.acp.simccs.modules.crisis.dto.AiEditorRequest;
import com.acp.simccs.modules.crisis.dto.AiEditorResponse;
import com.acp.simccs.modules.crisis.dto.ReportResponse;
import com.acp.simccs.modules.misinformation.client.GeminiApiClient;
import com.acp.simccs.modules.misinformation.dto.AiAnalysisRequest;
import com.acp.simccs.modules.misinformation.dto.GeminiApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiEditorService {

    private final ReportService reportService;
    private final ChatService chatService;
    private final GeminiApiClient geminiApiClient;

    public AiEditorResponse getSuggestedEdits(AiEditorRequest request) {
        log.info("Generating AI edits for report ID: {}", request.getReportId());

        // 1. Fetch Report content
        ReportResponse report = reportService.getReportById(request.getReportId());
        String originalContent = report.getContent();

        // 2. Fetch Chat comments
        Long channelId = chatService.getReportChannel(request.getReportId()).getId();
        List<MessageDTO> messages = chatService.getHistory(channelId);

        StringBuilder commentsBuilder = new StringBuilder();
        if (messages.isEmpty()) {
            commentsBuilder.append("No comments provided.");
        } else {
            for (MessageDTO msg : messages) {
                commentsBuilder.append("- ").append(msg.getSenderName()).append(": ").append(msg.getContent())
                        .append("\n");
            }
        }

        // 3. Construct prompt
        String prompt = String.format(
                "You are an expert AI Editor for the Agence Congolaise de Presse.\n" +
                        "Please rewrite and edit the following Crisis Report draft.\n\n" +
                        "Rules:\n" +
                        "- Requested Tone: %s\n" +
                        "- Formatting/Redaction Rules: %s\n\n" +
                        "Original Draft:\n" +
                        "%s\n\n" +
                        "Editorial Comments from the team:\n" +
                        "%s\n\n" +
                        "Output ONLY the rewritten text, without backticks or markdown formatting.",
                request.getTone() != null ? request.getTone() : "Professional",
                request.getCustomRules() != null ? request.getCustomRules() : "None",
                originalContent,
                commentsBuilder.toString());

        // 4. Send to Gemini
        AiAnalysisRequest aiRequest = AiAnalysisRequest.builder()
                .contents(List.of(
                        AiAnalysisRequest.Content.builder()
                                .parts(List.of(
                                        AiAnalysisRequest.Part.builder()
                                                .text(prompt)
                                                .build()))
                                .build()))
                .build();

        GeminiApiResponse response = geminiApiClient.analyze(aiRequest);
        String suggestedContent = extractTextFromResponse(response);

        // Remove any markdown code blocks if the AI ignored the instruction
        if (suggestedContent.startsWith("```")) {
            suggestedContent = suggestedContent.replaceAll("^```[a-zA-Z]*\\n?", "");
            suggestedContent = suggestedContent.replaceAll("```$", "");
        }

        return new AiEditorResponse(suggestedContent.trim());
    }

    private String extractTextFromResponse(GeminiApiResponse response) {
        try {
            GeminiApiResponse.Candidate candidate = response.getCandidates().get(0);
            if (candidate.getContent() != null && candidate.getContent().getParts() != null) {
                return candidate.getContent().getParts().get(0).getText();
            }
        } catch (Exception e) {
            log.error("Error extracting text from Gemini response", e);
        }
        throw new RuntimeException("Invalid response structure received from Gemini API");
    }
}
