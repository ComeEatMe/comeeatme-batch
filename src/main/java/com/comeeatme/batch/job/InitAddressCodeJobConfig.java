package com.comeeatme.batch.job;

import com.comeeatme.batch.domain.AddressCode;
import com.comeeatme.batch.domain.dto.AddressCodeDto;
import com.comeeatme.batch.processor.AddressCodeInitProcessor;
import com.comeeatme.batch.writer.AddressCodeSubAddressUpdateWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import static com.comeeatme.batch.job.InitJobConfig.FILE_DIR;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class InitAddressCodeJobConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final int chunkSize = 10;

    @Bean
    public Job initAddressCodeJob(
            Step addressCodeInitFileDownloadStep,
            Step addressCodeInitFileUnzipStep,
            Step addressCodeInitFileToDbStep,
            Step addressCodeDepth2UpdateStep,
            Step addressCodeDepth3UpdateStep,
            Step addressCodeDepth4UpdateStep) {
        return jobBuilderFactory.get("initAddressCodeJob")
                .start(addressCodeInitFileDownloadStep)
                .next(addressCodeInitFileUnzipStep)
                .next(addressCodeInitFileToDbStep)
                .next(addressCodeDepth2UpdateStep)
                .next(addressCodeDepth3UpdateStep)
                .next(addressCodeDepth4UpdateStep)
                .build();
    }

    @Bean
    public Step addressCodeInitFileDownloadStep() {
        return stepBuilderFactory.get("addressCodeInitFileDownloadStep")
                .tasklet((contribution, chunkContext) -> {
                    String url = "https://www.code.go.kr/etc/codeFullDown.do?codeseId=법정동코드";
                    String zipName = "법정동코드 전체자료.zip";
                    Path zipPath = new File(FILE_DIR, zipName).toPath();

                    try (InputStream in = new URL(url).openStream()) {
                        log.info("법정동 코드 데이터 zip 파일 다운로드 시작 name={}, path={}, url={}",
                                zipName, zipPath, url);
                        Files.copy(in, zipPath);
                        log.info("법정동 코드 데이터 zip 파일 다운로드 완료 name={}",
                                zipName);
                    }

                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step addressCodeInitFileUnzipStep() {
        return stepBuilderFactory.get("addressCodeInitFileUnzipStep")
                .tasklet((contribution, chunkContext) -> {
                    String zipFilename = "법정동코드 전체자료.zip";
                    File zipFile = new File(FILE_DIR, zipFilename);

                    log.info("법정동 코드 데이터 unzip 시작. FILE_DIR={}", Arrays.toString(FILE_DIR.listFiles()));
                    try (ZipFile zip = new ZipFile(zipFile)){
                        zip.setCharset(Charset.forName("EUC-KR"));
                        zip.extractAll(FILE_DIR.getAbsolutePath());
                    }
                    log.info("법정동 코드 데이터 unzip 완료. FILE_DIR={}", Arrays.toString(FILE_DIR.listFiles()));

                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step addressCodeInitFileToDbStep(
            ItemReader<AddressCodeDto> addressCodeInitFileReader,
            ItemProcessor<AddressCodeDto, AddressCode> addressCodeInitProcessor,
            ItemWriter<AddressCode> addressCodeItemWriter) {
        return stepBuilderFactory.get("addressCodeInitFileToDbStep")
                .<AddressCodeDto, AddressCode>chunk(chunkSize)
                .reader(addressCodeInitFileReader)
                .processor(addressCodeInitProcessor)
                .writer(addressCodeItemWriter)
                .build();
    }

    @Bean
    public ItemReader<AddressCodeDto> addressCodeInitFileReader() {
        return new FlatFileItemReaderBuilder<AddressCodeDto>()
                .name("addressCodeInitFileReader")
                .resource(new FileSystemResource(new File(FILE_DIR, "법정동코드 전체자료.txt")))
                .encoding("EUC-KR")
                .targetType(AddressCodeDto.class)
                .linesToSkip(1)
                .delimited().delimiter(DelimitedLineTokenizer.DELIMITER_TAB)
                .names("code", "address", "deleted")
                .build();
    }

    @Bean
    public ItemProcessor<AddressCodeDto, AddressCode> addressCodeInitProcessor() {
        return new AddressCodeInitProcessor();
    }

    @Bean
    public Step addressCodeDepth2UpdateStep(
            ItemReader<AddressCode> addressCodeDepth1Reader,
            JpaItemWriter<AddressCode> addressCodeSubAddressUpdateWriter) {
        return stepBuilderFactory.get("addressCodeDepth2UpdateStep")
                .<AddressCode, AddressCode>chunk(chunkSize)
                .reader(addressCodeDepth1Reader)
                .writer(addressCodeSubAddressUpdateWriter)
                .build();
    }

    @Bean
    public ItemReader<AddressCode> addressCodeDepth1Reader(EntityManagerFactory entityManagerFactory) {
        Map<String, Object> parameters = Map.of("depth", 1);
        return new JpaPagingItemReaderBuilder<AddressCode>()
                .name("addressCodeDepth1Reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("select ac from AddressCode ac where ac.depth = :depth")
                .parameterValues(parameters)
                .build();
    }

    @Bean
    public Step addressCodeDepth3UpdateStep(
            ItemReader<AddressCode> addressCodeDepth2Reader,
            JpaItemWriter<AddressCode> addressCodeSubAddressUpdateWriter) {
        return stepBuilderFactory.get("addressCodeDepth3UpdateStep")
                .<AddressCode, AddressCode>chunk(chunkSize)
                .reader(addressCodeDepth2Reader)
                .writer(addressCodeSubAddressUpdateWriter)
                .build();
    }

    @Bean
    public ItemReader<AddressCode> addressCodeDepth2Reader(EntityManagerFactory entityManagerFactory) {
        Map<String, Object> parameters = Map.of("depth", 2);
        return new JpaPagingItemReaderBuilder<AddressCode>()
                .name("addressCodeDepth2Reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("select ac from AddressCode ac where ac.depth = :depth")
                .parameterValues(parameters)
                .build();
    }

    @Bean
    public Step addressCodeDepth4UpdateStep(
            ItemReader<AddressCode> addressCodeDepth3Reader,
            JpaItemWriter<AddressCode> addressCodeSubAddressUpdateWriter) {
        return stepBuilderFactory.get("addressCodeRiUpdateStep")
                .<AddressCode, AddressCode>chunk(chunkSize)
                .reader(addressCodeDepth3Reader)
                .writer(addressCodeSubAddressUpdateWriter)
                .build();
    }

    @Bean
    public ItemReader<AddressCode> addressCodeDepth3Reader(EntityManagerFactory entityManagerFactory) {
        Map<String, Object> parameters = Map.of("depth", 3);
        return new JpaPagingItemReaderBuilder<AddressCode>()
                .name("addressCodeDepth4Reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("select ac from AddressCode ac where ac.depth = :depth")
                .parameterValues(parameters)
                .build();
    }

    @Bean
    public JpaItemWriter<AddressCode> addressCodeSubAddressUpdateWriter(EntityManagerFactory entityManagerFactory) {
        AddressCodeSubAddressUpdateWriter itemWriter = new AddressCodeSubAddressUpdateWriter();
        itemWriter.setEntityManagerFactory(entityManagerFactory);
        return itemWriter;
    }

}
