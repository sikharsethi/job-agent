package com.sikhar.job_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateCoverLetter(String jobTitle, String company, String jobDescription) {
        try {
            String requestBody = String.format("""
                {
                    "model": "llama-3.3-70b-versatile",
                    "messages": [
                        {
                            "role": "user",
                            "content": "Write a professional cover letter for this job. Job Title: %s, Company: %s, Description: %s. Keep it under 300 words."
                        }
                    ]
                }
                """, jobTitle, company, jobDescription.replace("\"", "\\\""));

            Request request = new Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                System.out.println("=== GROQ RESPONSE ===");
                System.out.println(responseBody);
                JsonNode root = objectMapper.readTree(responseBody);
                String result = root.path("choices")
                        .path(0)
                        .path("message")
                        .path("content")
                        .asText();
                System.out.println("=== RESULT ===");
                System.out.println(result);
                return result;
            }
        } catch (Exception e) {
            return "Error generating cover letter: " + e.getMessage();
        }
    }
}