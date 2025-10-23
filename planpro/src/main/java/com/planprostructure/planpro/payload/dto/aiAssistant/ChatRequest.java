package com.planprostructure.planpro.payload.dto.aiAssistant;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRequest {

    private List<Content> contents;
    private GenerationConfig generationConfig;

    public ChatRequest(String prompt) {
        this.contents = new ArrayList<>();
        this.contents.add(new Content(prompt));
        this.generationConfig = new GenerationConfig();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;

        public Content(String text) {
            this.parts = new ArrayList<>();
            this.parts.add(new Part(text));
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GenerationConfig {
        private double temperature = 0.5;
        private int maxOutputTokens = 1024;
    }
}
