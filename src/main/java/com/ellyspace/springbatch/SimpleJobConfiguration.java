package com.ellyspace.springbatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration //Spring Batch의 모든 Job은 @Configuration으로 등록해서 사용한다.
public class SimpleJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public SimpleJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob") //simpleJob이라는 이름으로 batch job을 생성한다.
                .start(simpleStep1())
                .build();
    }

    @Bean
    public Step simpleStep1() {
        return stepBuilderFactory.get("simpleStep1") //simpleStep1이라는 batch step 생성
                .tasklet(((contribution, chunkContext) -> { //tasklet: step 안에서 단일로 수행될 커스텀 기능들을 선언할 때 사용
                    log.info(">>>>> This is STEP1");
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

}


/**
 * Job: 하나의 배치 작업 단위
 * Step: Job 안에 여러 Step이 존재한다.
 * Step 안에 Tasklet 또는 Reader & Processor & Writer 묶음이 존재한다.
 * Tasklet: @Component, @Bean과 비슷한 역할. 명확한 역할은 없지만 개발자가 지정한 커스텀한 기능을 위한 단위.
 **/