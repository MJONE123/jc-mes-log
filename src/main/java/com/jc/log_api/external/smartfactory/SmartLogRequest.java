package com.jc.log_api.external.smartfactory;

import lombok.Getter;
import lombok.Setter;

/**
 * 스마트공장 로그 수집 API 요청 DTO
 */
@Getter
@Setter
public class SmartLogRequest {

    // YYYY-MM-DD HH:MM:SS.SSS
    private String logDt;

    // 접속 구분 코드 (D06001 ~ D06999)
    private String useSe;

    // 시스템 사용자 (MES user_id)
    private String sysUser;

    // 접속 IP
    private String conectIp;

    // 데이터 사용량(Byte) - 없으면 0
    private int dataUsgqty;
}
