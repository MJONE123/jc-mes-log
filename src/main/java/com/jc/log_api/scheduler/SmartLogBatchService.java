package com.jc.log_api.scheduler;

import com.jc.log_api.domain.entity.SysLogHistory;
import com.jc.log_api.domain.repository.SysLogHistoryRepository;
import com.jc.log_api.external.smartfactory.SmartFactoryLogClient;
import com.jc.log_api.external.smartfactory.SmartLogRequest;
import com.jc.log_api.external.smartfactory.SmartLogResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * sysLogHistory → 스마트공장 로그 API 전송 배치
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmartLogBatchService {

    private final SysLogHistoryRepository historyRepository;
    private final SmartFactoryLogClient logClient;

    private static final DateTimeFormatter LOG_DT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void sendLogs() { // 메서드명 변경 (LoginLogs -> Logs)

        List<SysLogHistory> logs = historyRepository.findTodayNotSentLoginTop100();
        if (logs.isEmpty()) {
            return;
        }

        log.info("스마트공장 로그 전송 대상: {}건", logs.size());

        for (SysLogHistory h : logs) {
            try {
                processLog(h);
            } catch (Exception e) {
                log.error("로그 처리 중 예외 발생 login_key={}: {}", h.getLoginKey(), e.getMessage(), e);
                h.setSmartLogSendYn("E"); // 에러 처리
                h.setSmartLogRsltMsg(e.getMessage());
            }
        }
    }

    private void processLog(SysLogHistory h) {
        String currentStatus = h.getSmartLogSendYn();

        // 1. 로그인 로그 전송 (아직 안 보낸 경우)
        if (currentStatus == null) {
            SmartLogRequest req = toSmartLogRequest(h, "DO6001", h.getLoginTime());
            SmartLogResult res = logClient.sendLog(req);

            if (!isSuccess(res)) {
                markError(h, res);
                return; // 로그인 전송 실패하면 여기서 중단
            }

            // 로그인 성공 -> 우선 'L' 상태로 마킹
            h.setSmartLogSendYn("L");
            h.setSmartLogRsltCd(res.getRecptnRsltCd());
        }

        // 2. 로그아웃 로그 전송 (로그아웃 시간이 존재할 때)
        if (h.getLogoutTime() != null) {
            SmartLogRequest req = toSmartLogRequest(h, "DO6002", h.getLogoutTime());
            SmartLogResult res = logClient.sendLog(req);

            if (!isSuccess(res)) {
                markError(h, res);
                return;
            }

            // 로그아웃까지 성공 -> 최종 'Y' 상태로 마킹
            h.setSmartLogSendYn("Y");
            h.setSmartLogSendDt(LocalDateTime.now());
            h.setSmartLogRsltCd(res.getRecptnRsltCd());
            h.setSmartLogRsltMsg("Login & Logout Sent");
        }
    }

    /**
     * 통합 DTO 매핑 메서드
     * @param useSe : D06001(로그인) or D06002(로그아웃)
     * @param targetTime : loginTime or logoutTime
     */
    private SmartLogRequest toSmartLogRequest(SysLogHistory h, String useSe, LocalDateTime targetTime) {
        SmartLogRequest req = new SmartLogRequest();

        // 전송할 시간 (로그인 시간 or 로그아웃 시간)
        req.setLogDt(targetTime.format(LOG_DT_FORMATTER));

        // 구분 코드 설정
        req.setUseSe(useSe);

        req.setSysUser(h.getUserId());
        req.setConectIp(h.getLogIp());
        req.setDataUsgqty(0);

        return req;
    }

    // 성공 여부 판단 헬퍼
    private boolean isSuccess(SmartLogResult res) {
        return "AP1001".equals(res.getRecptnRsltCd()) || "AP1002".equals(res.getRecptnRsltCd()) || "AP1029".equals(res.getRecptnRsltCd());
    }

    // 에러 마킹 헬퍼
    private void markError(SysLogHistory h, SmartLogResult res) {
        h.setSmartLogSendYn("E");
        h.setSmartLogSendDt(LocalDateTime.now());
        h.setSmartLogRsltCd(res.getRecptnRsltCd());
        h.setSmartLogRsltMsg(res.getRecptnRsltDtl());
    }
}