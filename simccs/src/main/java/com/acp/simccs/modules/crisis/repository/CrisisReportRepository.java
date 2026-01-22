package com.acp.simccs.modules.crisis.repository;

import com.acp.simccs.modules.crisis.model.CrisisReport;
import com.acp.simccs.modules.crisis.model.EReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrisisReportRepository extends JpaRepository<CrisisReport, Long> {
    List<CrisisReport> findByStatus(EReportStatus status);
    List<CrisisReport> findByAuthorId(Long authorId);
}