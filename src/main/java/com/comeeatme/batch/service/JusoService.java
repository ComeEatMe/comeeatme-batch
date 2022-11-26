package com.comeeatme.batch.service;

import com.comeeatme.batch.exception.ApiFrequentInShortException;
import com.comeeatme.batch.exception.ApiRequestErrorException;
import com.comeeatme.batch.service.dto.JusoAddressDto;
import com.comeeatme.batch.service.dto.JusoCommonDto;
import com.comeeatme.batch.service.dto.JusoCoordDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;

@Service
public class JusoService {

    private final String jusoRoadAddressPath;

    private final String jusoRoadAddressKey;

    private final String jusoCoordPath;

    private final String jusoCoordKey;

    private final WebClient webClient;

    private final ModelMapper modelMapper;

    public JusoService(
            @Value("${open-api.business-juso.base-url}") String jusoUrl,
            @Value("${open-api.business-juso.road-address.path}") String jusoRoadAddressPath,
            @Value("${open-api.business-juso.road-address.key}") String jusoRoadAddressKey,
            @Value("${open-api.business-juso.coord.path}") String jusoCoordPath,
            @Value("${open-api.business-juso.coord.key}") String jusoCoordKey) {
        this.jusoRoadAddressPath = jusoRoadAddressPath;
        this.jusoRoadAddressKey = jusoRoadAddressKey;
        this.jusoCoordPath = jusoCoordPath;
        this.jusoCoordKey = jusoCoordKey;
        this.webClient = WebClient.builder()
                .baseUrl(jusoUrl)
                .build();
        this.modelMapper = new ModelMapper();
    }

    /**
     * Ref: https://business.juso.go.kr/addrlink/openApi/searchApi.do (도로명주소 검색 API)
     */
    public JusoAddressDto searchAddress(String keyword) {
        Map responseBody = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(jusoRoadAddressPath)
                        .queryParam("confmKey", jusoRoadAddressKey)
                        .queryParam("resultType", "json")
                        .queryParam("keyword", keyword)
                        .build()
                ).retrieve()
                .bodyToMono(Map.class)
                .block();
        if (isNull(responseBody)) {
            throw new ApiRequestErrorException("Response Body is null. keyword=" + keyword);
        }
        JusoAddressDto results = modelMapper.map(responseBody.get("results"), JusoAddressDto.class);
        validateResult(results.getCommon());
        return results;
    }

    private void validateResult(JusoCommonDto jusoCommon) {
        if (isNull(jusoCommon)) {
            throw new ApiRequestErrorException("응답에 문제가 있습니다. " + jusoCommon);
        } else if (Objects.equals("E007", jusoCommon.getErrorCode())) {
            throw new ApiFrequentInShortException(jusoCommon.getErrorMessage());
        } else if (!Objects.equals("0", jusoCommon.getErrorCode())) {
            throw new ApiRequestErrorException(
                    jusoCommon.getErrorCode(), jusoCommon.getErrorMessage());
        }
    }

    /**
     *  Ref: https://business.juso.go.kr/addrlink/openApi/searchApi.do (좌표제공 검색 API)
     */
    public JusoCoordDto searchCoordinate(
            String amdCd, String rnMgtSn, String udrtYn, Integer buldMnnm, Integer buldSlno) {
        Map responseBody = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(jusoCoordPath)
                        .queryParam("confmKey", jusoCoordKey)
                        .queryParam("resultType", "json")
                        .queryParam("admCd", amdCd)
                        .queryParam("rnMgtSn", rnMgtSn)
                        .queryParam("udrtYn", udrtYn)
                        .queryParam("buldMnnm", buldMnnm)
                        .queryParam("buldSlno", buldSlno)
                        .build()
                ).retrieve()
                .bodyToMono(Map.class)
                .block();
        if (isNull(responseBody)) {
            throw new ApiRequestErrorException(String.format("Response Body is null." +
                    " amdCd=%s, rnMgtSn=%s, udrtYn=%s, buldMnnm=%s, buldSlno=%s",
                    amdCd, rnMgtSn, udrtYn, buldMnnm, buldSlno));
        }
        JusoCoordDto results = modelMapper.map(responseBody.get("results"), JusoCoordDto.class);
        validateResult(results.getCommon());
        return results;
    }

    public JusoCoordDto searchCoordinate(JusoAddressDto.Juso param) {
        return searchCoordinate(
                param.getAdmCd(), param.getRnMgtSn(), param.getUdrtYn(), param.getBuldMnnm(), param.getBuldSlno());
    }

}
