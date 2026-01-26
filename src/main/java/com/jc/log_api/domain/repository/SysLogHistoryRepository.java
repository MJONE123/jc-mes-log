package com.jc.log_api.domain.repository;

import com.jc.log_api.domain.entity.SysLogHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SysLogHistoryRepository extends JpaRepository<SysLogHistory, String> {

    /**
     * 오늘 날짜 + 미전송 + BROWSER 타입 로그인 로그 상위 100건
     * 0126 수정: 아예 안보낸 건 + 로그인만 보냈는데 로그아웃 시간이 찍힌 건 추가
     */
    @Query(value = """
            SELECT TOP 100 *
            FROM sysLogHistory
            WHERE log_date = CONVERT(VARCHAR(8), GETDATE(), 112)
              AND (
                  smart_log_send_yn IS NULL 
                  OR 
                  (smart_log_send_yn = 'L' AND logout_time IS NOT NULL)
              )
              AND log_type = 'FORM'
              AND login_time IS NOT NULL
            ORDER BY login_time
            """, nativeQuery = true)
    List<SysLogHistory> findTodayNotSentLoginTop100();
}
