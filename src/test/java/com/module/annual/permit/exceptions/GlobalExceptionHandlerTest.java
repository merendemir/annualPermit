package com.module.annual.permit.exceptions;

import com.module.annual.permit.util.ResponseCreator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private ResponseCreator responseCreator;


    @Before
    public void setUp() {
        responseCreator = mock(ResponseCreator.class);

        globalExceptionHandler = new GlobalExceptionHandler(
                responseCreator);
    }

    @Test
    public void testHandleException_itShouldReturnResponseEntity() {
        // given
        Exception e = new Exception();

        String messageCode = "something.went.wrong";
        String messageContext = "context";

        Map<String, Object> map = new HashMap<>();
        map.put("message", messageContext);
        map.put("data", null);
        ResponseEntity<Object> mapResponseEntity = new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);

        //when
        when(responseCreator.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, messageCode, null))
                .thenReturn(mapResponseEntity);

        //then
        ResponseEntity<Object> result = globalExceptionHandler.handleException(e);

        assertEquals(mapResponseEntity, result);

        verify(responseCreator, times(1)).createResponse(HttpStatus.INTERNAL_SERVER_ERROR, messageCode, null);

    }

    @Test
    public void testHandleDataNotFoundException_itShouldReturnResponseEntity() {
        // given
        String message = "message";
        String param = "param";

        DataNotFoundException e = new DataNotFoundException(message, param);

        Map<String, Object> map = new HashMap<>();
        map.put("message", e.getMessage());
        map.put("data", null);
        ResponseEntity<Object> mapResponseEntity = new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        //when
        when(responseCreator.createResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null, param))
                .thenReturn(mapResponseEntity);

        //then
        ResponseEntity<Object> result = globalExceptionHandler.HandleDataNotFoundException(e);

        assertEquals(mapResponseEntity, result);

        verify(responseCreator, times(1)).createResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null, param);
    }

    @Test
    public void testHandleDataNotAcceptableException_itShouldReturnResponseEntity() {
        // given
        String message = "message";

        DataNotAcceptableException e = new DataNotAcceptableException(message);

        Map<String, Object> map = new HashMap<>();
        map.put("message", e.getMessage());
        map.put("data", null);
        ResponseEntity<Object> mapResponseEntity = new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        //when
        when(responseCreator.createResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null))
                .thenReturn(mapResponseEntity);

        //then
        ResponseEntity<Object> result = globalExceptionHandler.handleDataNotAcceptableException(e);

        assertEquals(mapResponseEntity, result);

        verify(responseCreator, times(1)).createResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @Test
    public void testHandleDataNotAcceptableExceptionWithParam_itShouldReturnResponseEntity() {
        // given
        String message = "message";
        String param = "param";

        DataNotAcceptableException e = new DataNotAcceptableException(message, param);

        Map<String, Object> map = new HashMap<>();
        map.put("message", e.getMessage());
        map.put("data", null);
        ResponseEntity<Object> mapResponseEntity = new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        //when
        when(responseCreator.createResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null, param))
                .thenReturn(mapResponseEntity);

        //then
        ResponseEntity<Object> result = globalExceptionHandler.handleDataNotAcceptableException(e);

        assertEquals(mapResponseEntity, result);

        verify(responseCreator, times(1)).createResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null, param);
    }

    @Test
    public void testHandleDataAllReadyExistsException_itShouldReturnResponseEntity() {
        // given
        String message = "message";

        DataAllReadyExistsException e = new DataAllReadyExistsException(message);

        Map<String, Object> map = new HashMap<>();
        map.put("message", e.getMessage());
        map.put("data", null);
        ResponseEntity<Object> mapResponseEntity = new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        //when
        when(responseCreator.createResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null))
                .thenReturn(mapResponseEntity);

        //then
        ResponseEntity<Object> result = globalExceptionHandler.handleDataAllReadyExistsException(e);

        assertEquals(mapResponseEntity, result);

        verify(responseCreator, times(1)).createResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

}