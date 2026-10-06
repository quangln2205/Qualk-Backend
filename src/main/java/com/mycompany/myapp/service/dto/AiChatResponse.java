package com.mycompany.myapp.service.dto;

/** Response returned by the data-aware Qualk assistant. */
public class AiChatResponse {

    private final String answer;
    private final AiDataSummary data;

    public AiChatResponse(String answer, AiDataSummary data) {
        this.answer = answer;
        this.data = data;
    }

    public String getAnswer() {
        return answer;
    }

    public AiDataSummary getData() {
        return data;
    }

    public record AiDataSummary(long posts, long likesReceived, long commentsWritten, String latestPostCaption) {}
}
