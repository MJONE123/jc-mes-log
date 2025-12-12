package com.jc.log_api.external.smartfactory;

import lombok.Getter;
import lombok.Setter;

/**
 * 스마트공장 로그 수집 API 응답 result DTO
 */
@Getter
@Setter
public class SmartLogResult {

    // 수신 일시
    private String recptnDt;

    // 수신 결과 코드 (API001 등)
    private String recptnRsltCd;

    // 수신 결과 설명
    private String recptnRslt;

    // 수신 결과 상세 설명
    private String recptnRsltDtl;
}
