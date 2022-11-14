package com.comeeatme.batch.service;

import com.comeeatme.batch.service.dto.JusoAddressDto;
import io.netty.handler.codec.http.HttpResponseStatus;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class JusoServiceTest {

    private MockWebServer mockWebServer;

    private JusoService jusoService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        jusoService = new JusoService(
                String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                "/addrlink/addrLinkApi.do",
                "BUSINESS_JUSO_ROAD_ADDRESS_KEY",
                "/addrlink/addrCoordApi.do",
                "BUSINESS_JUSO_COORD_KEY"
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void searchAddress() throws Exception {
        // given
        String responseJson = "{\n" +
                "    \"results\": {\n" +
                "        \"common\": {\n" +
                "            \"errorMessage\": \"정상\",\n" +
                "            \"countPerPage\": \"10\",\n" +
                "            \"totalCount\": \"1\",\n" +
                "            \"errorCode\": \"0\",\n" +
                "            \"currentPage\": \"1\"\n" +
                "        },\n" +
                "        \"juso\": [\n" +
                "            {\n" +
                "                \"detBdNmList\": \"\",\n" +
                "                \"engAddr\": \"24-6 Yatap-ro 69beon-gil, Bundang-gu, Seongnam-si, Gyeonggi-do\",\n" +
                "                \"rn\": \"야탑로69번길\",\n" +
                "                \"emdNm\": \"야탑동\",\n" +
                "                \"zipNo\": \"13497\",\n" +
                "                \"roadAddrPart2\": \"(야탑동)\",\n" +
                "                \"emdNo\": \"01\",\n" +
                "                \"sggNm\": \"성남시 분당구\",\n" +
                "                \"jibunAddr\": \"경기도 성남시 분당구 야탑동 353-4 두만프라자\",\n" +
                "                \"siNm\": \"경기도\",\n" +
                "                \"roadAddrPart1\": \"경기도 성남시 분당구 야탑로69번길 24-6\",\n" +
                "                \"bdNm\": \"두만프라자\",\n" +
                "                \"admCd\": \"4113510700\",\n" +
                "                \"udrtYn\": \"0\",\n" +
                "                \"lnbrMnnm\": \"353\",\n" +
                "                \"roadAddr\": \"경기도 성남시 분당구 야탑로69번길 24-6(야탑동)\",\n" +
                "                \"lnbrSlno\": \"4\",\n" +
                "                \"buldMnnm\": \"24\",\n" +
                "                \"bdKdcd\": \"0\",\n" +
                "                \"liNm\": \"\",\n" +
                "                \"rnMgtSn\": \"411354340306\",\n" +
                "                \"mtYn\": \"0\",\n" +
                "                \"bdMgtSn\": \"4113510700103530004046238\",\n" +
                "                \"buldSlno\": \"6\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpResponseStatus.OK.code())
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .setBody(responseJson));

        // when
        jusoService.searchAddress(
                "경기도 성남시 분당구 야탑로69번길 24-6 (야탑동, 두만프라자  1층  104호)");

        // then
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.GET.name());
        assertThat(recordedRequest.getPath()).startsWith("/addrlink/addrLinkApi.do");

        HttpUrl requestUrl = recordedRequest.getRequestUrl();
        assertThat(requestUrl).isNotNull();
        assertThat(requestUrl.host()).isEqualTo(mockWebServer.getHostName());
        assertThat(requestUrl.queryParameter("confmKey")).isEqualTo("BUSINESS_JUSO_ROAD_ADDRESS_KEY");
        assertThat(requestUrl.queryParameter("resultType")).isEqualTo("json");
        assertThat(requestUrl.queryParameter("keyword"))
                .isEqualTo("경기도 성남시 분당구 야탑로69번길 24-6 (야탑동, 두만프라자  1층  104호)");
    }

    @Test
    void searchCoordinate_JusoParam() throws Exception {
        // given
        String responseJson = "{\n" +
                "    \"results\": {\n" +
                "        \"common\": {\n" +
                "            \"errorMessage\": \"정상\",\n" +
                "            \"totalCount\": \"1\",\n" +
                "            \"errorCode\": \"0\"\n" +
                "        },\n" +
                "        \"juso\": [\n" +
                "            {\n" +
                "                \"buldMnnm\": \"24\",\n" +
                "                \"rnMgtSn\": \"411354340306\",\n" +
                "                \"bdNm\": \"두만프라자\",\n" +
                "                \"entX\": \"967019.0357698901\",\n" +
                "                \"entY\": \"1934785.166226367\",\n" +
                "                \"admCd\": \"4113510700\",\n" +
                "                \"bdMgtSn\": \"4113510700103530004046238\",\n" +
                "                \"buldSlno\": \"6\",\n" +
                "                \"udrtYn\": \"0\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpResponseStatus.OK.code())
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .setBody(responseJson));

        // when
        JusoAddressDto.Juso juso = new JusoAddressDto.Juso();
        juso.setAdmCd("4113510700");
        juso.setRnMgtSn("411354340306");
        juso.setUdrtYn("0");
        juso.setBuldMnnm(24);
        juso.setBuldSlno(6);

        jusoService.searchCoordinate(juso);

        // then
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.GET.name());
        assertThat(recordedRequest.getPath()).startsWith("/addrlink/addrCoordApi.do");

        HttpUrl requestUrl = recordedRequest.getRequestUrl();
        assertThat(requestUrl).isNotNull();
        assertThat(requestUrl.host()).isEqualTo(mockWebServer.getHostName());
        assertThat(requestUrl.queryParameter("confmKey")).isEqualTo("BUSINESS_JUSO_COORD_KEY");
        assertThat(requestUrl.queryParameter("resultType")).isEqualTo("json");
        assertThat(requestUrl.queryParameter("admCd")).isEqualTo("4113510700");
        assertThat(requestUrl.queryParameter("rnMgtSn")).isEqualTo("411354340306");
        assertThat(requestUrl.queryParameter("udrtYn")).isEqualTo("0");
        assertThat(requestUrl.queryParameter("buldMnnm")).isEqualTo("24");
        assertThat(requestUrl.queryParameter("buldSlno")).isEqualTo("6");
    }

    @Test
    void searchCoordinate() throws Exception {
        // given
        String responseJson = "{\n" +
                "    \"results\": {\n" +
                "        \"common\": {\n" +
                "            \"errorMessage\": \"정상\",\n" +
                "            \"totalCount\": \"1\",\n" +
                "            \"errorCode\": \"0\"\n" +
                "        },\n" +
                "        \"juso\": [\n" +
                "            {\n" +
                "                \"buldMnnm\": \"24\",\n" +
                "                \"rnMgtSn\": \"411354340306\",\n" +
                "                \"bdNm\": \"두만프라자\",\n" +
                "                \"entX\": \"967019.0357698901\",\n" +
                "                \"entY\": \"1934785.166226367\",\n" +
                "                \"admCd\": \"4113510700\",\n" +
                "                \"bdMgtSn\": \"4113510700103530004046238\",\n" +
                "                \"buldSlno\": \"6\",\n" +
                "                \"udrtYn\": \"0\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpResponseStatus.OK.code())
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .setBody(responseJson));

        // when
        jusoService.searchCoordinate("4113510700", "411354340306", "0", 24, 6);

        // then
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.GET.name());
        assertThat(recordedRequest.getPath()).startsWith("/addrlink/addrCoordApi.do");

        HttpUrl requestUrl = recordedRequest.getRequestUrl();
        assertThat(requestUrl).isNotNull();
        assertThat(requestUrl.host()).isEqualTo(mockWebServer.getHostName());
        assertThat(requestUrl.queryParameter("confmKey")).isEqualTo("BUSINESS_JUSO_COORD_KEY");
        assertThat(requestUrl.queryParameter("resultType")).isEqualTo("json");
        assertThat(requestUrl.queryParameter("admCd")).isEqualTo("4113510700");
        assertThat(requestUrl.queryParameter("rnMgtSn")).isEqualTo("411354340306");
        assertThat(requestUrl.queryParameter("udrtYn")).isEqualTo("0");
        assertThat(requestUrl.queryParameter("buldMnnm")).isEqualTo("24");
        assertThat(requestUrl.queryParameter("buldSlno")).isEqualTo("6");
    }

}