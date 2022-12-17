package com.module.annual.permit.util;

import com.module.annual.permit.service.MessageService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ResponseCreatorTest {

    private ResponseCreator responseCreator;

    private MessageService messageService;

    @Before
    public void setUp() {
        messageService = mock(MessageService.class);
        responseCreator = new ResponseCreator(
                messageService);
    }

    @Test
    public void whenCreateResponseCalled_itShouldReturnResponseEntity() {
        //given
        HttpStatus httpStatus = HttpStatus.OK;
        String messageCode = "message.code";
        String messageContext = "message context";
        String data = "data";

        //when
        when(messageService.getMessage(messageCode)).thenReturn(messageContext);

        Map<String, Object> map = new HashMap<>();
        map.put("message", messageContext);
        map.put("data", data);
        ResponseEntity<Object> expected = new ResponseEntity<>(map, httpStatus);

        //then
        ResponseEntity<Object> actual = responseCreator.createResponse(httpStatus, messageCode, data);

        assertEquals(expected, actual);

        verify(messageService, times(1)).getMessage(messageCode);
    }

    @Test
    public void whenCreateResponseCalledWithMessageParams_itShouldReturnResponseEntity() {
        //given
        HttpStatus httpStatus = HttpStatus.OK;
        String messageCode = "message.code";
        String[] params = new String[]{"context"};
        String messageContext = "message " + Arrays.toString(params);
        String data = "data";

        //when
        when(messageService.getMessage(messageCode, params)).thenReturn(messageContext);

        Map<String, Object> map = new HashMap<>();
        map.put("message", messageContext);
        map.put("data", data);
        ResponseEntity<Object> expected = new ResponseEntity<>(map, httpStatus);

        //then
        ResponseEntity<Object> actual = responseCreator.createResponse(httpStatus, messageCode, data, params);

        assertEquals(expected, actual);

        verify(messageService, times(1)).getMessage(messageCode, params);
    }

}