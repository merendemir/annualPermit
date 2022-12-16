package com.module.annual.permit.util;

import com.module.annual.permit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ResponseCreator {

    private final MessageService localeService;

    public ResponseEntity<Object> createResponse(HttpStatus httpStatus, String messageCode, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", localeService.getMessage(messageCode));
        map.put("data", data);
        return new ResponseEntity<>(map, httpStatus);
    }

    public ResponseEntity<Object> createResponse(HttpStatus httpStatus, String messageCode, Object data, String... params) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", localeService.getMessage(messageCode, params));
        map.put("data", data);
        return new ResponseEntity<>(map, httpStatus);
    }

}
