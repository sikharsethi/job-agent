package com.sikhar.job_agent.controller;

import com.sikhar.job_agent.model.Job;
import com.sikhar.job_agent.service.GroqService;
import com.sikhar.job_agent.service.JobService;
import com.sikhar.job_agent.service.ScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final JobService jobService;
    private final ScraperService scraperService;

    @GetMapping("/")
    public String dashboard(Model model) {
        List<Job> jobs = jobService.getAllJobs();
        long totalCompanies = jobs.stream()
                .map(Job::getCompany)
                .distinct()
                .count();
        model.addAttribute("jobs", jobs);
        model.addAttribute("totalJobs", jobs.size());
        model.addAttribute("totalCompanies", totalCompanies);
        return "index";
    }

    @PostMapping("/scrape")
    public String scrape() {
        scraperService.scrapeNow();
        return "redirect:/";
    }
    private final GroqService groqService;

    @GetMapping("/cover-letter/{id}")
    public String coverLetter(@PathVariable Long id, Model model) {
        return jobService.getJobById(id)
                .map(job -> {
                    String letter = groqService.generateCoverLetter(
                            job.getTitle(), job.getCompany(), job.getDescription());
                    model.addAttribute("job", job);
                    model.addAttribute("coverLetter", letter);
                    return "cover-letter";
                })
                .orElse("redirect:/");
    }
}

