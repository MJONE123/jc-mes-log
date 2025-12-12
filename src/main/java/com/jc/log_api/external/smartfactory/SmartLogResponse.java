package com.jc.log_api.external.smartfactory;

import lombok.Getter;
import lombok.Setter;

/**
 * 스마트공장 로그 수집 API 전체 응답 DTO
 * {
 *   "result": { ... }
 * }
 */
@Getter
@Setter
public class SmartLogResponse {

    private SmartLogResult result;
}
