package com.jc.log_api.external.smartfactory;

import lombok.Getter;
import lombok.Setter;

/**
 * 스마트공장 로그 수집 API 응답 result DTO
 */
@Getter
@Setter
public class SmartLogResult {

    private String recptnDt;       // 수신 일시
    private String recptnRsltCd;   // 결과 코드 AP1002 등
    private String recptnRslt;     // "데이터 이관 완료"
    private String recptnRsltDtl;  // 상세 메시지
}
