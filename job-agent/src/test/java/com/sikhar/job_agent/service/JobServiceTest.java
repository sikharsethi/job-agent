package com.sikhar.job_agent.service;

import com.sikhar.job_agent.model.Job;
import com.sikhar.job_agent.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    void shouldNotSaveDuplicateJob() {
        Job job = new Job();
        job.setTitle("Java Developer");
        job.setCompany("Google");

        when(jobRepository.existsByTitleAndCompany("Java Developer", "Google"))
                .thenReturn(true);

        Job result = jobService.saveJob(job);

        assertNull(result);
        verify(jobRepository, never()).save(any(Job.class));
    }

    @Test
    void shouldSaveNewJob() {
        Job job = new Job();
        job.setTitle("Python Developer");
        job.setCompany("Microsoft");

        when(jobRepository.existsByTitleAndCompany("Python Developer", "Microsoft"))
                .thenReturn(false);
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        Job result = jobService.saveJob(job);

        assertNotNull(result);
        verify(jobRepository, times(1)).save(any(Job.class));
    }
}