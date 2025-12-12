package com.jc.log_api.domain.repository;

import com.jc.log_api.domain.entity.SysLogHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SysLogHistoryRepository extends JpaRepository<SysLogHistory, String> {

    /**
     * 오늘 날짜 + 미전송 + BROWSER 타입 로그인 로그 상위 100건
     */
    @Query(value = """
            SELECT TOP 100 *
            FROM sysLogHistory
            WHERE log_date = CONVERT(VARCHAR(8), GETDATE(), 112)
              AND smart_log_send_yn IS NULL
              AND log_type = 'BROWSER'
              AND login_time IS NOT NULL
            ORDER BY login_time
            """, nativeQuery = true)
    List<SysLogHistory> findTodayNotSentLoginTop100();
}
