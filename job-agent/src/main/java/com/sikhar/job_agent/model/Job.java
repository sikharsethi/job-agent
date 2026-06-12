package com.sikhar.job_agent.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String company;
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String url;
    private String status;

    private LocalDateTime createdAt;
}