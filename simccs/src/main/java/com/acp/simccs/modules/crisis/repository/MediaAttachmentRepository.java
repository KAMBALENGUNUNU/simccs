package com.acp.simccs.modules.crisis.repository;

import com.acp.simccs.modules.crisis.model.MediaAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaAttachmentRepository extends JpaRepository<MediaAttachment, Long> {
    List<MediaAttachment> findByReportId(Long reportId);
}