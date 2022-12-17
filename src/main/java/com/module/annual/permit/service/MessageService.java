package com.module.annual.permit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageService {

	private final MessageSource messageSource;

	/**
	 * @param code @Description Key of the message you want to show in messages.properties.
	 * @return String
	 *
	 * This method returns an error or warning message with language support according to the received code.
	 */
	public String getMessage(String code) {
		Locale locale = LocaleContextHolder.getLocale();
		return this.messageSource.getMessage(code, null, locale);
	}

	/**
	 * @param code @Description Key of the message you want to show in messages.properties.
	 * @param params @Description Parameters that you want to add as a variable to the message.
	 * @return String
	 *
	 * This method adds parameters to be added to messages with language support according to the received code
	 * and returns error or warning messages.
	 */
	public String getMessage(String code, String... params) {
		Locale locale = LocaleContextHolder.getLocale();
		return this.messageSource.getMessage(code, params, locale);
	}

}
