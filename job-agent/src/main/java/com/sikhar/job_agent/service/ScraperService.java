package com.sikhar.job_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sikhar.job_agent.model.Job;
import com.sikhar.job_agent.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScraperService {

    @Value("${adzuna.app.id}")
    private String appId;

    @Value("${adzuna.api.key}")
    private String apiKey;

    private final JobService jobService;
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedRate = 3600000)
    public void scrapeJobs() {
        log.info("Starting job scraping...");
        List<Job> jobs = fetchFromAdzuna("java developer", "in");
        jobs.forEach(jobService::saveJob);
        log.info("Scraping complete. Saved {} jobs.", jobs.size());
    }

    public List<Job> scrapeNow() {
        List<Job> jobs = fetchFromAdzuna("java developer", "in");
        jobs.forEach(jobService::saveJob);
        return jobs;
    }

    private List<Job> fetchFromAdzuna(String keyword, String country) {
        List<Job> jobs = new ArrayList<>();
        try {
            String url = String.format(
                    "https://api.adzuna.com/v1/api/jobs/%s/search/1?app_id=%s&app_key=%s&results_per_page=10&what=%s",
                    country, appId, apiKey, keyword.replace(" ", "%20")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                String body = response.body().string();
                log.info("Adzuna response: {}", body);
                JsonNode root = objectMapper.readTree(body);
                JsonNode results = root.path("results");

                for (JsonNode result : results) {
                    Job job = new Job();
                    job.setTitle(result.path("title").asText());
                    job.setCompany(result.path("company").path("display_name").asText());
                    job.setLocation(result.path("location").path("display_name").asText());
                    job.setDescription(result.path("description").asText());
                    job.setUrl(result.path("redirect_url").asText());
                    job.setStatus("NEW");
                    jobs.add(job);
                }
                log.info("Fetched {} jobs from Adzuna", jobs.size());
            }
        } catch (Exception e) {
            log.error("Adzuna fetch failed: {}", e.getMessage());
        }
        return jobs;
    }
}