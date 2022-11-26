package com.comeeatme.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.file.Files;
import java.util.Objects;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class InitJobConfig {

    public static final File FILE_DIR = new File(System.getProperty("user.dir"), "cem-init");

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job initJob(
            Step initFileDirStep,
            Step addressCodeInitJobStep,
            Step restaurantInitJobStep) {
        return jobBuilderFactory.get("initJob")
                .start(initFileDirStep)
                .next(addressCodeInitJobStep)
                .next(restaurantInitJobStep)
                .build();
    }

    @Bean
    public Step initFileDirStep() {
        return stepBuilderFactory.get("initFileDirStep")
                .tasklet((contribution, chunkContext) -> {
                    if (!FILE_DIR.exists() && !FILE_DIR.mkdirs()) {
                        throw new IllegalStateException("파일 저장을 위한 디렉토리 생성에 실패했습니다.");
                    }
                    for (File file : Objects.requireNonNull(FILE_DIR.listFiles())) {
                        Files.delete(file.toPath());
                    }
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step addressCodeInitJobStep(Job addressInitCodeJob) {
        return stepBuilderFactory.get("initAddressCodeJobStep")
                .job(addressInitCodeJob)
                .build();
    }

    @Bean
    public Step restaurantInitJobStep(Job restaurantInitJob) {
        return stepBuilderFactory.get("initRestaurantJobStep")
                .job(restaurantInitJob)
                .build();
    }

}
