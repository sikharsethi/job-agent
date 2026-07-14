package com.sikhar.job_agent.service;

import com.sikhar.job_agent.model.Job;
import com.sikhar.job_agent.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public Job saveJob(Job job) {
        boolean exists = jobRepository.existsByTitleAndCompany(job.getTitle(), job.getCompany());
        if (exists) {
            return null;
        }
        job.setCreatedAt(LocalDateTime.now());
        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        // show only last 7 days jobs
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return jobRepository.findRecentJobs(sevenDaysAgo);
    }
    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }
}
