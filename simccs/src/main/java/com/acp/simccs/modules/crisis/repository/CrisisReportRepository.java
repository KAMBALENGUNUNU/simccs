package com.acp.simccs.modules.crisis.repository;

import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.crisis.model.EReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrisisReportRepository extends JpaRepository<CrisisReport, Long> {

    /**
     * Searches reports by optional status and author filters.
     * Results are ordered by Priority: URGENT (1) → HIGH (2) → NORMAL (3) → LOW
     * (4).
     */
    @Query("SELECT r FROM CrisisReport r WHERE " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:authorId IS NULL OR r.author.id = :authorId) " +
            "ORDER BY CASE r.priority " +
            "  WHEN 'URGENT' THEN 1 " +
            "  WHEN 'HIGH'   THEN 2 " +
            "  WHEN 'NORMAL' THEN 3 " +
            "  WHEN 'LOW'    THEN 4 " +
            "  ELSE 5 END ASC, r.createdAt DESC")
    List<CrisisReport> searchReports(@Param("status") EReportStatus status,
            @Param("authorId") Long authorId);

    List<CrisisReport> findByStatus(EReportStatus status);

    List<CrisisReport> findByAuthorId(Long authorId);
}