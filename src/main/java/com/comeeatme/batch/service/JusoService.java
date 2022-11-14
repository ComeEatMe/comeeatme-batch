package com.comeeatme.batch.service;

import com.comeeatme.batch.service.dto.JusoAddressDto;
import com.comeeatme.batch.service.dto.JusoCoordDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

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
        return modelMapper.map(responseBody.get("results"), JusoAddressDto.class);
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
        return modelMapper.map(responseBody.get("results"), JusoCoordDto.class);
    }

    public JusoCoordDto searchCoordinate(JusoAddressDto.Juso param) {
        return searchCoordinate(
                param.getAdmCd(), param.getRnMgtSn(), param.getUdrtYn(), param.getBuldMnnm(), param.getBuldSlno());
    }

}
