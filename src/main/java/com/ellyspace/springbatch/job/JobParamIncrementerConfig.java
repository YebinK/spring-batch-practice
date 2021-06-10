package com.ellyspace.springbatch.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "INCREMENT_JOB")
public class JobParamIncrementerConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public JobParamIncrementerConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job incrementJob() {
        return jobBuilderFactory.get("INCREMENT_JOB")
                .incrementer(new RunIdIncrementer())
                .start(incrementStep())
                .build();
    }

    @Bean
    public Step incrementStep() {
        return stepBuilderFactory.get("INCREMENT_STEP")
                .startLimit(2)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>> INCREMENT STEP");
                    Object o = chunkContext.getStepContext().getJobParameters().get("run.id");
                    log.info(">>> Incremented Job Id : {}", o);
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }
}
