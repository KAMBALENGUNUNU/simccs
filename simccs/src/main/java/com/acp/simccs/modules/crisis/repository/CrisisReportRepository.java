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

    // Production-ready search: Handles nulls dynamically in the DB query
    @Query("SELECT r FROM CrisisReport r WHERE " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:authorId IS NULL OR r.author.id = :authorId)")
    List<CrisisReport> searchReports(@Param("status") EReportStatus status,
                                     @Param("authorId") Long authorId);

    List<CrisisReport> findByStatus(EReportStatus status);
    List<CrisisReport> findByAuthorId(Long authorId);
}