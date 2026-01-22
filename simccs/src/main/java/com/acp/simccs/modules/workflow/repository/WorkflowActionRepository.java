package com.acp.simccs.modules.workflow.repository;

import com.acp.simccs.modules.workflow.model.WorkflowAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowActionRepository extends JpaRepository<WorkflowAction, Long> {
    List<WorkflowAction> findByReportIdOrderByTimestampDesc(Long reportId);
}