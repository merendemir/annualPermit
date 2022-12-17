package com.module.annual.permit.service;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MessageServiceTest {

    private MessageService messageService;

    private MessageSource messageSource;

    @BeforeClass
    public static void staticSetUp() {
        mockStatic(LocaleContextHolder.class);
    }

    @Before
    public void setUp() throws Exception {
        messageSource = mock(MessageSource.class);
        messageService = new MessageService(messageSource);
    }

    @Test
    public void whenGetMessageCalledWithMessageCode_itShouldReturnMessage() {
        //given
        String messageCode = "message.code";
        String messageContext = "message context";

        //when
        when(LocaleContextHolder.getLocale()).thenReturn(Locale.US);
        when(messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale())).thenReturn(messageContext);

        //then
        String actual = messageService.getMessage(messageCode);

        assertEquals(messageContext, actual);

        verify(messageSource, times(1)).getMessage(messageCode, null, LocaleContextHolder.getLocale());
    }

    @Test
    public void whenGetMessageCalledWithMessageCodeAndParams_itShouldReturnMessage() {
        //given
        String messageCode = "message.code";
        String[] param = new String[]{"context"};
        String messageContext = "message " + Arrays.toString(param);

        //when
        when(LocaleContextHolder.getLocale()).thenReturn(Locale.US);
        when(messageSource.getMessage(messageCode, param, LocaleContextHolder.getLocale())).thenReturn(messageContext);

        //then
        String actual = messageService.getMessage(messageCode, param);

        assertEquals(messageContext, actual);

        verify(messageSource, times(1)).getMessage(messageCode, param, LocaleContextHolder.getLocale());
    }
}