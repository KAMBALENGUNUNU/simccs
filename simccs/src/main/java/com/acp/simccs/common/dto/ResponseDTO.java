package com.acp.simccs.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> implements Serializable {

    private String timestamp;
    private Integer status;
    private String message;
    private T data;
    private List<String> errors;

    // Constructors
    public ResponseDTO(Integer status, String message) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        this.status = status;
        this.message = message;
    }

    public ResponseDTO(Integer status, String message, T data) {
        this(status, message);
        this.data = data;
    }

    // Static Builders
    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(HttpStatus.OK.value(), "Success", data);
    }

    public static <T> ResponseDTO<T> success(String message, T data) {
        return new ResponseDTO<>(HttpStatus.OK.value(), message, data);
    }

    public static <T> ResponseDTO<T> error(String message) {
        return new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), message);
    }

    public static <T> ResponseDTO<T> error(Integer status, String message) {
        return new ResponseDTO<>(status, message);
    }

    // FIX: Removed the conflicting wildcard cast. Now returns ResponseDTO<T>
    public ResponseEntity<ResponseDTO<T>> toResponseEntity() {
        return ResponseEntity.status(this.status).body(this);
    }
}