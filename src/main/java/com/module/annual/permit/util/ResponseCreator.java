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

    private final MessageService messageService;

    /**
     * @param httpStatus @Description Http status of response.
     * @param messageCode @Description The code of the message to be displayed.
     * @param data @Description Data to be returned.
     * @return ResponseEntity<Object>
     *
     * This method standardizes the response of APIs.
     */
    public ResponseEntity<Object> createResponse(HttpStatus httpStatus, String messageCode, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", messageService.getMessage(messageCode));
        map.put("data", data);
        return new ResponseEntity<>(map, httpStatus);
    }

    /**
     * @param httpStatus @Description Http status of response.
     * @param messageCode @Description The code of the message to be displayed.
     * @param data @Description Data to be returned.
     * @param params Custom parameters that want to be added to messages
     * @return ResponseEntity<Object>
     *
     * This method standardized the response of APIs. Custom parameters can be added to messages.
     */
    public ResponseEntity<Object> createResponse(HttpStatus httpStatus, String messageCode, Object data, String... params) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", messageService.getMessage(messageCode, params));
        map.put("data", data);
        return new ResponseEntity<>(map, httpStatus);
    }

}
