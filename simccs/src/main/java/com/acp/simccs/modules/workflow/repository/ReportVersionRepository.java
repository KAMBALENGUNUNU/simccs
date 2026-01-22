package com.acp.simccs.modules.workflow.repository;

import com.acp.simccs.modules.workflow.model.ReportVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportVersionRepository extends JpaRepository<ReportVersion, Long> {
    List<ReportVersion> findByReportIdOrderByVersionNumberDesc(Long reportId);
}