package com.sikhar.job_agent.controller;

import com.sikhar.job_agent.model.Job;
import com.sikhar.job_agent.service.GroqService;
import com.sikhar.job_agent.service.JobService;
import com.sikhar.job_agent.service.ScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final GroqService groqService;
    private final ScraperService scraperService;

    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        return ResponseEntity.ok(jobService.saveJob(job));
    }

    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }
    @GetMapping("/{id}/cover-letter")
    public ResponseEntity<String> generateCoverLetter(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(job -> ResponseEntity.ok(
                        groqService.generateCoverLetter(job.getTitle(), job.getCompany(), job.getDescription())
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/scrape")
    public ResponseEntity<String> triggerScrape() {
        List<Job> jobs = scraperService.scrapeNow();
        return ResponseEntity.ok("Scraped and saved " + jobs.size() + " jobs!");
    }


}
