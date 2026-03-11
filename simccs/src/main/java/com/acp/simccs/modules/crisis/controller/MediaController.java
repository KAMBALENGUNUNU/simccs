package com.acp.simccs.modules.crisis.controller;

import com.acp.simccs.security.SecurityService;
import com.acp.simccs.modules.crisis.service.MinioStorageService;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
@Tag(name = "Media", description = "Secure media upload/download via MinIO")
public class MediaController {

    @Autowired
    SecurityService securityService;

    @Autowired
    MinioStorageService minioStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = minioStorageService.uploadFile(file);
            return ResponseEntity.ok(fileName);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Could not upload file: " + ex.getMessage());
        }
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Resource resource = minioStorageService.downloadFile(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }
}