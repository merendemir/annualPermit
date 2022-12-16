package com.module.annual.permit.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "publicHoliday", url = "https://api.ubilisim.com/")
public interface FeignCallForPublicHoliday {

	@GetMapping("/resmitatiller")
	ResponseEntity<Object> getPublicHolidaysFromExternal();
}
