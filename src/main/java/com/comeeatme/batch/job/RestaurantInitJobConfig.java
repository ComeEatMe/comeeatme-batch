package com.comeeatme.batch.job;

import com.comeeatme.batch.domain.LocalData;
import com.comeeatme.batch.exception.ApiFrequentInShortException;
import com.comeeatme.batch.exception.NoRequiredDataException;
import com.comeeatme.batch.listener.SkipLogSkipListener;
import com.comeeatme.batch.mapper.RestaurantInitCsvFieldSetMapper;
import com.comeeatme.batch.service.dto.LocalDataRestaurantDto;
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
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.backoff.FixedBackOffPolicy;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.comeeatme.batch.job.InitJobConfig.FILE_DIR;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RestaurantInitJobConfig {

    private static final int CHUNK_SIZE = 10;

    private static final String[] initCsvHeaders = {
            "번호", "개방서비스명", "개방서비스아이디", "개방자치단체코드", "관리번호", "인허가일자",
            "인허가취소일자", "영업상태구분코드", "영업상태명", "상세영업상태코드", "상세영업상태명", "폐업일자",
            "휴업시작일자", "휴업종료일자", "재개업일자", "소재지전화", "소재지면적", "소재지우편번호",
            "소재지전체주소", "도로명전체주소", "도로명우편번호", "사업장명", "최종수정시점", "데이터갱신구분",
            "데이터갱신일자", "업태구분명", "좌표정보(x)", "좌표정보(y)", "위생업태명", "남성종사자수",
            "여성종사자수", "영업장주변구분명", "등급구분명", "급수시설구분명", "총직원수", "본사직원수",
            "공장사무직직원수", "공장판매직직원수", "공장생산직직원수", "건물소유구분명", "보증액", "월세액",
            "다중이용업소여부", "시설총규모", "전통업소지정번호", "전통업소주된음식", "홈페이지", ""
    };

    public static final String INIT_FILE_ENCODING = "EUC-KR";

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job restaurantInitJob(
            Step restaurantFileDownloadStep,
            Step restaurantFileUnzipStep,
            Step restaurantInitFileToDbJobStep
    ) {
        return jobBuilderFactory.get("restaurantInitJob")
                .start(restaurantFileDownloadStep)
                .next(restaurantFileUnzipStep)
                .next(restaurantInitFileToDbJobStep)
                .build();
    }

    @Bean
    public Step restaurantFileDownloadStep() {
        return stepBuilderFactory.get("restaurantFileDownloadStep")
                .tasklet((contribution, chunkContext) -> {
                    String[] urls = {
                            "https://www.localdata.go.kr/datafile/each/07_24_04_P_CSV.zip", // 일반음식점
                            "https://www.localdata.go.kr/datafile/each/07_24_05_P_CSV.zip"  // 휴게음식점
                    };

                    for (int i = 0; i < urls.length; i++) {
                        String url = urls[i];
                        String[] urlSplit = url.split("/");
                        String zipName = urlSplit[urlSplit.length - 1];
                        Path zipPath = new File(FILE_DIR, zipName).toPath();

                        try (InputStream in = new URL(url).openStream()) {
                            log.info("지역 데이터 zip 파일 다운로드 시작 [{}/{}] name={}, path={}, url={}",
                                    (i + 1), urls.length, zipName, zipPath, url);
                            Files.copy(in, zipPath);
                            log.info("지역 데이터 zip 파일 다운로드 완료 [{}/{}] name={}",
                                    (i + 1), urls.length, zipName);
                        }
                    }

                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step restaurantFileUnzipStep() {
        return stepBuilderFactory.get("restaurantFileUnzipStep")
                .tasklet((contribution, chunkContext) -> {
                    List<String> zipFilenames = List.of("07_24_04_P_CSV.zip", "07_24_05_P_CSV.zip");
                    List<File> zipFiles = zipFilenames.stream()
                            .map(filename -> new File(FILE_DIR, filename))
                            .collect(Collectors.toList());

                    log.info("지역 데이터 unzip 시작. FILE_DIR={}", Arrays.toString(FILE_DIR.listFiles()));
                    for (File zipFile : zipFiles) {
                        try (ZipFile zip = new ZipFile(zipFile)){
                            zip.setCharset(Charset.forName(INIT_FILE_ENCODING));
                            zip.extractAll(FILE_DIR.getAbsolutePath());
                        }
                    }
                    log.info("지역 데이터 unzip 완료. FILE_DIR={}", Arrays.toString(FILE_DIR.listFiles()));

                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step restaurantInitFileToDbJobStep(Job restaurantInitFileToDbJob) {
        return stepBuilderFactory.get("restaurantInitFileToDbJobStep")
                .job(restaurantInitFileToDbJob)
                .build();
    }

    @Bean
    public Job restaurantInitFileToDbJob(
            Step normalRestaurantInitFileToDbStep,
            Step restingRestaurantInitFileToDbStep) {
        return jobBuilderFactory.get("restaurantInitFileToDbJob")
                .start(normalRestaurantInitFileToDbStep)
                .next(restingRestaurantInitFileToDbStep)
                .build();
    }

    @Bean
    public Step normalRestaurantInitFileToDbStep(
            ItemReader<LocalDataRestaurantDto> normalRestaurantInitFileReader,
            ItemProcessor<LocalDataRestaurantDto, LocalData> localDataRestaurantProcessor,
            ItemWriter<LocalData> localDataRestaurantWriter,
            SkipLogSkipListener skipLogSkipListener) {
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000);
        return stepBuilderFactory.get("normalRestaurantInitFileToDbStep")
                .<LocalDataRestaurantDto, LocalData>chunk(CHUNK_SIZE)
                .reader(normalRestaurantInitFileReader)
                .processor(localDataRestaurantProcessor)
                .writer(localDataRestaurantWriter)

                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skip(NoRequiredDataException.class)
                .skip(DataIntegrityViolationException.class)
                .skipLimit(Integer.MAX_VALUE)

                .retry(ApiFrequentInShortException.class)
                .backOffPolicy(fixedBackOffPolicy)
                .retryLimit(Integer.MAX_VALUE)

                .listener(skipLogSkipListener)
                .build();
    }

    @Bean
    public ItemReader<LocalDataRestaurantDto> normalRestaurantInitFileReader() {
        return new FlatFileItemReaderBuilder<LocalDataRestaurantDto>()
                .name("normalRestaurantInitFileReader")
                .resource(new FileSystemResource(new File(FILE_DIR, "fulldata_07_24_04_P_일반음식점.csv")))
                .fieldSetMapper(new RestaurantInitCsvFieldSetMapper())
                .encoding(INIT_FILE_ENCODING)
                .linesToSkip(1)
                .delimited().delimiter(DelimitedLineTokenizer.DELIMITER_COMMA)
                .names(initCsvHeaders)
                .build();
    }

    @Bean
    public Step restingRestaurantInitFileToDbStep(
            ItemReader<LocalDataRestaurantDto> restingRestaurantInitFileReader,
            ItemProcessor<LocalDataRestaurantDto, LocalData> localDataRestaurantProcessor,
            ItemWriter<LocalData> localDataRestaurantWriter,
            SkipLogSkipListener skipLogSkipListener) {
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000);
        return stepBuilderFactory.get("restingRestaurantInitFileToDbStep")
                .<LocalDataRestaurantDto, LocalData>chunk(CHUNK_SIZE)
                .reader(restingRestaurantInitFileReader)
                .processor(localDataRestaurantProcessor)
                .writer(localDataRestaurantWriter)

                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skip(NoRequiredDataException.class)
                .skip(DataIntegrityViolationException.class)
                .skipLimit(Integer.MAX_VALUE)

                .retry(ApiFrequentInShortException.class)
                .backOffPolicy(fixedBackOffPolicy)
                .retryLimit(Integer.MAX_VALUE)

                .listener(skipLogSkipListener)
                .build();
    }

    @Bean
    public ItemReader<LocalDataRestaurantDto> restingRestaurantInitFileReader() {
        return new FlatFileItemReaderBuilder<LocalDataRestaurantDto>()
                .name("restingRestaurantInitFileReader")
                .resource(new FileSystemResource(new File(FILE_DIR, "fulldata_07_24_05_P_휴게음식점.csv")))
                .fieldSetMapper(new RestaurantInitCsvFieldSetMapper())
                .encoding(INIT_FILE_ENCODING)
                .linesToSkip(1)
                .delimited().delimiter(DelimitedLineTokenizer.DELIMITER_COMMA)
                .names(initCsvHeaders)
                .build();
    }


}
