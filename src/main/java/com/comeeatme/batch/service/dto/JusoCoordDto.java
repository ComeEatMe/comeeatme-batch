package com.comeeatme.batch.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class JusoCoordDto {

    private JusoCommonDto common;

    private List<Juso> juso;

    @Data
    @NoArgsConstructor
    public static class Juso {
        private String admCd;               // Y	행정구역코드
        private String rnMgtSn;             // Y	도로명코드
        private String bdMgtSn;             // Y	건물관리번호
        private String udrtYn;              // Y	지하여부(0 : 지상, 1 : 지하)
        private Integer buldMnnm;           // Y	건물본번
        private Integer buldSlno;           // Y	건물부번
        private Double entX;                // Y    X좌표
        private Double entY;                // Y    Y좌표
        private String bdNm;                // N	건물명
    }

}
