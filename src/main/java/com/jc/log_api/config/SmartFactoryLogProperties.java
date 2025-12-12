package com.jc.log_api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * application.yml 의 smart-factory.log 설정을 바인딩하는 클래스
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "smart-factory.log")
public class SmartFactoryLogProperties {

    /**
     * 스마트공장 로그 API URL
     * 예) https://log.smart-factory.kr/apisvc/sendLogData.json
     */
    private String url;

    /**
     * 스마트공장 사업관리시스템에서 발급받은 로그 API 인증키
     */
    private String crtfcKey;
}
