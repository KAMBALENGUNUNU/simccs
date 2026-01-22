package com.acp.simccs.modules.workflow.repository;

import com.acp.simccs.modules.workflow.model.MisinformationFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MisinformationFlagRepository extends JpaRepository<MisinformationFlag, Long> {
    List<MisinformationFlag> findByReportId(Long reportId);
}