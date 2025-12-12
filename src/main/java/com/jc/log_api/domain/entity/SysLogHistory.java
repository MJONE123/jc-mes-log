package com.jc.log_api.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * MES sysLogHistory 테이블 매핑 엔티티
 */
@Getter
@Setter
@Entity
@Table(name = "sysLogHistory")
public class SysLogHistory {

    @Id
    @Column(name = "login_key")
    private String loginKey;

    @Column(name = "log_date")
    private String logDate;          // 예: 20250306

    @Column(name = "user_id")
    private String userId;

    @Column(name = "log_type")
    private String logType;          // BROWSER / FORM 등

    @Column(name = "login_time")
    private LocalDateTime loginTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "log_ip")
    private String logIp;

    // ----- 스마트공장 전송 상태 컬럼 (A 선택지) -----

    @Column(name = "smart_log_send_yn")
    private String smartLogSendYn;   // NULL:미전송, Y:성공, E:실패

    @Column(name = "smart_log_send_dt")
    private LocalDateTime smartLogSendDt;

    @Column(name = "smart_log_rslt_cd")
    private String smartLogRsltCd;

    @Column(name = "smart_log_rslt_msg")
    private String smartLogRsltMsg;
}
