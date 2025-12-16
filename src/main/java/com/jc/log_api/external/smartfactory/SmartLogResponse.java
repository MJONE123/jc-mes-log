package com.jc.log_api.external.smartfactory;

import lombok.Data;

/**
 * 스마트공장 로그 수집 API 전체 응답 DTO
 * { "result": { ... } }
 */
@Data
public class SmartLogResponse {

    private SmartLogResult result;
}
