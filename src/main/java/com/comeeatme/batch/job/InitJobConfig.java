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

    public static final File FILE_DIR = new File(System.getProperty("user.dir"), "init");

    private static final String INIT_FILE_DELIMITER = "|";

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final int chunkSize = 10;

    private final String restaurantInitFileName = "restaurant-init";

    private final String[] rawCsvHeaders = {
            "번호", "개방서비스명", "개방서비스아이디", "개방자치단체코드", "관리번호", "인허가일자",
            "인허가취소일자", "영업상태구분코드", "영업상태명", "상세영업상태코드", "상세영업상태명", "폐업일자",
            "휴업시작일자", "휴업종료일자", "재개업일자", "소재지전화", "소재지면적", "소재지우편번호",
            "소재지전체주소", "도로명전체주소", "도로명우편번호", "사업장명", "최종수정시점", "데이터갱신구분",
            "데이터갱신일자", "업태구분명", "좌표정보(x)", "좌표정보(y)", "위생업태명", "남성종사자수",
            "여성종사자수", "영업장주변구분명", "등급구분명", "급수시설구분명", "총직원수", "본사직원수",
            "공장사무직직원수", "공장판매직직원수", "공장생산직직원수", "건물소유구분명", "보증액", "월세액",
            "다중이용업소여부", "시설총규모", "전통업소지정번호", "전통업소주된음식", "홈페이지", ""
    };

    private final String[] csvHeaders = {
            "name",                     // 사업장명
            "phone",                    // 소재지전화
            "addressName",              // 도로명전체주소
            "addressRoadName",          // 소재지전체주소
            "openStatus",               // 상세영업상태명
            "openInfoManagementNum",    // 관리번호
            "openInfoServiceId",        // 개방서비스아이디
            "openInfoName",             // 개방서비스명
            "openInfoCategory",         // 업태구분명
            "openInfoPermissionDate",   // 인허가일자
            "openInfoLastModifiedAt"    // 최종수정시점
    };

    @Bean
    public Job initJob(
            Step initFileDirStep,
            Step initAddressCodeJobStep) {
        return jobBuilderFactory.get("initJob")
                .start(initFileDirStep)
                .next(initAddressCodeJobStep)
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
    public Step initAddressCodeJobStep(Job initAddressCodeJob) {
        return stepBuilderFactory.get("initAddressCodeJobStep")
                .job(initAddressCodeJob)
                .build();
    }

}
