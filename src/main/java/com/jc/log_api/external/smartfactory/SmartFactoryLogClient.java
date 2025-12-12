package com.jc.log_api.external.smartfactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jc.log_api.config.SmartFactoryLogProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 스마트공장 로그 수집 API 클라이언트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmartFactoryLogClient {

    private final SmartFactoryLogProperties props;
    private final RestTemplate restTemplate = new RestTemplate();  // 필요하면 Config로 분리

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 로그 1건 전송
     */
    public SmartLogResult sendLog(SmartLogRequest req) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("crtfcKey", props.getCrtfcKey());
        params.add("logDt", req.getLogDt());
        params.add("useSe", req.getUseSe());
        params.add("sysUser", req.getSysUser());
        params.add("conectIp", req.getConectIp());
        params.add("dataUsgqty", String.valueOf(req.getDataUsgqty()));

        String url = props.getUrl();

        try {
            String responseJson = restTemplate.postForObject(
                    url,
                    new HttpEntity<>(params),
                    String.class
            );

            log.debug("SmartFactory response: {}", responseJson);

            SmartLogResponse response =
                    objectMapper.readValue(responseJson, SmartLogResponse.class);

            return response.getResult();

        } catch (Exception e) {
            log.error("SmartFactory 로그 전송 실패: {}", e.getMessage(), e);

            SmartLogResult error = new SmartLogResult();
            error.setRecptnRsltCd("LOCAL_ERROR");
            error.setRecptnRsltDtl(e.getMessage());
            return error;
        }
    }
}
