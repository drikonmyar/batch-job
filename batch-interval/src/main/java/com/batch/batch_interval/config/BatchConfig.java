package com.batch.batch_interval.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    @Bean
    public Job retrieveDataJob() {
        JobBuilder jobBuilder = new JobBuilder("retrieveDataJob", jobRepository);
        SimpleJobBuilder simpleJobBuilder = jobBuilder.start(step());
        return simpleJobBuilder.incrementer(new RunIdIncrementer()).build();
    }

    @Bean
    public Step step() {
        StepBuilder stepBuilder = new StepBuilder("step", jobRepository);
        TaskletStep step = stepBuilder
                .<Map<String, Object>, Map<String, Object>>chunk(10, transactionManager)
                .reader(reader())
                .writer(writer())
                .build();
        return step;
    }

    @Bean
    public JdbcCursorItemReader<Map<String, Object>> reader() {
        JdbcCursorItemReader<Map<String, Object>> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT * FROM student");
        reader.setRowMapper((rs, rowNum) -> {
            Map<String, Object> row = new HashMap<>();
            row.put("id", rs.getObject("id"));
            row.put("name", rs.getObject("name"));
            return row;
        });
        return reader;
    }

    @Bean
    public ItemWriter<Map<String, Object>> writer() {
        return new ItemWriter<Map<String, Object>>() {
            @Override
            public void write(Chunk<? extends Map<String, Object>> items) throws Exception {
                for (Map<String, Object> item : items) {
                    System.out.println("Retrieved Row: " + item);
                }
            }
        };
    }
}
