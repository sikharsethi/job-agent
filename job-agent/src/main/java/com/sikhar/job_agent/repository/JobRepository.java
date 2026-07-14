package com.sikhar.job_agent.repository;

import com.sikhar.job_agent.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    boolean existsByTitleAndCompany(String title, String company);
    @Query("SELECT j FROM Job j WHERE j.createdAt >= :since ORDER BY j.createdAt DESC")
    List<Job> findRecentJobs(@Param("since") LocalDateTime since);
}

