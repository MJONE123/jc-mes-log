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

    // yyyy-MM-dd HH:mm:ss.SSS
    private static final DateTimeFormatter LOG_DT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 일정 주기마다 실행 (예: 1분마다)
     */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void sendLoginLogs() {

        List<SysLogHistory> logs = historyRepository.findTodayNotSentLoginTop100();
        if (logs.isEmpty()) {
            return;
        }

        log.info("스마트공장 로그 전송 대상: {}건", logs.size());

        for (SysLogHistory h : logs) {
            try {
                SmartLogRequest req = toSmartLogRequest(h);
                SmartLogResult res = logClient.sendLog(req);

                String cd = res.getRecptnRsltCd();
                boolean success = "API001".equals(cd) || "API002".equals(cd);

                h.setSmartLogSendYn(success ? "Y" : "E");
                h.setSmartLogSendDt(LocalDateTime.now());
                h.setSmartLogRsltCd(cd);
                h.setSmartLogRsltMsg(
                        (res.getRecptnRslt() == null ? "" : res.getRecptnRslt()) + " " +
                                (res.getRecptnRsltDtl() == null ? "" : res.getRecptnRsltDtl())
                );

            } catch (Exception e) {
                log.error("로그 전송 중 예외 발생 login_key={}: {}", h.getLoginKey(), e.getMessage(), e);
                h.setSmartLogSendYn("E");
                h.setSmartLogSendDt(LocalDateTime.now());
                h.setSmartLogRsltCd("LOCAL_ERROR");
                h.setSmartLogRsltMsg(e.getMessage());
            }
        }
    }

    /**
     * sysLogHistory → 스마트공장 요청 DTO 매핑
     * (1차 버전: 로그인(D06001)만 전송)
     */
    private SmartLogRequest toSmartLogRequest(SysLogHistory h) {
        SmartLogRequest req = new SmartLogRequest();

        // 로그인 시간 기준
        LocalDateTime loginTime = h.getLoginTime();
        req.setLogDt(loginTime.format(LOG_DT_FORMATTER));

        // 로그인 코드 (D06001)
        req.setUseSe("D06001");

        req.setSysUser(h.getUserId());

        // 현재는 log_ip 그대로 사용 (운영 데이터 보고 필요하면 조정)
        req.setConectIp(h.getLogIp());

        // 데이터 사용량은 0으로 (필요시 계산 로직 추가)
        req.setDataUsgqty(0);

        return req;
    }
}
