package com.jc.log_api.external.smartfactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jc.log_api.config.SmartFactoryLogProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmartFactoryLogClient {

    private final SmartFactoryLogProperties props;
    // ✅ Config에서 만든 smartFactoryRestTemplate Bean 주입
    private final RestTemplate smartFactoryRestTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 스마트공장 로그 API 전송
     */
    public SmartLogResult sendLog(SmartLogRequest req) {

        // 1) 파라미터 구성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("crtfcKey", props.getCrtfcKey());
        params.add("logDt", req.getLogDt());
        params.add("useSe", req.getUseSe());
        params.add("sysUser", req.getSysUser());
        params.add("conectIp", req.getConectIp());
        params.add("dataUsgqty", String.valueOf(req.getDataUsgqty()));

        // 2) 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 3) POST 전송용 HttpEntity
        HttpEntity<MultiValueMap<String, String>> httpEntity =
                new HttpEntity<>(params, headers);

        String url = props.getUrl();

        try {
            // 4) API 호출 (✅ 이제 Bean으로부터 주입받은 RestTemplate 사용)
            String responseJson = smartFactoryRestTemplate.postForObject(
                    url,
                    httpEntity,
                    String.class
            );

            log.debug("SmartFactory response: {}", responseJson);

            // 5) 응답 JSON → SmartLogResponse 변환
            SmartLogResponse wrapper =
                    objectMapper.readValue(responseJson, SmartLogResponse.class);

            return wrapper.getResult();

        } catch (Exception e) {

            log.error("SmartFactory 로그 전송 실패: {}", e.getMessage(), e);

            SmartLogResult error = new SmartLogResult();
            error.setRecptnRsltCd("LOCAL_ERR");
            error.setRecptnRslt("LOCAL_ERROR");
            error.setRecptnRsltDtl(e.getMessage());
            return error;
        }
    }
}
