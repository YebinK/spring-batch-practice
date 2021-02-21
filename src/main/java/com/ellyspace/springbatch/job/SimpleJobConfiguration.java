package com.ellyspace.springbatch.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
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
                .start(simpleStep1(null))
                .next(simpleStep2(null))
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) { //jobParameters : 외부에서 받을 수 있는 파라미터 (Program Arguments)
        return stepBuilderFactory.get("simpleStep1") //simpleStep1이라는 batch step 생성
                .tasklet(((contribution, chunkContext) -> { //tasklet: step 안에서 단일로 수행될 커스텀 기능들을 선언할 때 사용
                    log.info(">>>>> This is STEP1");
                    log.info(">>>>> requestDate = {}" , requestDate);
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep2(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep2")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>>> This is STEP2");
                    log.info(">>>>> requestDate = {}", requestDate);
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
 *
 * Spring Batch의 메타데이터
 * - 이전에 실행한 Job이 어떤 것들이 있는지
 * - 최근 실패한 Batch Parameter가 어떤 것들이고, 성공한 Job은 어떤 것들이 있는지
 * - 다시 실행한다면 어디서부터 시작하면 될지
 * - 어떤 Job에 어떤 Step들이 있었고, Step들 중에서 성공한 Step과 실패한 Step들은 어떤 것들이 있는지
 */

/**
 * MySQL이랑 연결 참고: https://jojoldu.tistory.com/325
 *
 * [BATCH_JOB_INSTANCE]
 * Job Parameter(배치 실행 시 외부에서 받을 수 있는 파라미터)에 따라 생성되는 테이블.
 * 방금 실행했던 Job의 정보 저장. Job Parameter가 다르면 같은 Batch Job이더라도 두 번 기록되지 않는다.
 *
 * [BATCH_JOB_EXECUTION]
 * JOB_INSTANCE가 성공/실패했던 모든 내역을 갖고 있다.
 * JOB_INSTANCE와 부모-자식 관계
 *
 * BatchStatus vs ExitStatus
 * BatchStatus는 job 또는 step의 실행 결과를 Spring에서 기록할 때 사용하는 Enum
 * ExitStatus는 step 실행 후의 상태 표시
 *
 * jobParameters는 @JobScope, @StepScope 일 때만 받아올 수 있다.
 */