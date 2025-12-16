package com.jc.log_api.external.smartfactory;

import lombok.Getter;
import lombok.Setter;

/**
 * 스마트공장 로그 수집 API 요청 DTO
 */
@Getter
@Setter
public class SmartLogRequest {

    private String logDt;       // YYYY-MM-DD HH:mm:ss
    private String useSe;       // D06001 등
    private String sysUser;     // MES 사용자 ID
    private String conectIp;    // 접속 IP
    private int dataUsgqty;     // Byte 단위 사용량 (없으면 0)
}
