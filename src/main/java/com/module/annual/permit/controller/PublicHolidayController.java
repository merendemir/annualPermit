package com.module.annual.permit.controller;

import com.module.annual.permit.service.PublicHolidayService;
import com.module.annual.permit.util.ResponseCreator;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/holiday")
public class PublicHolidayController {

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private final PublicHolidayService publicHolidayService;

    private final ResponseCreator responseCreator;

    @Operation(description = "Date format it shoult be dd.mm.yyyy")
    @PostMapping
    public ResponseEntity<Object> savePublicHoliday(@RequestParam String lang,
                                                    @RequestParam String date) {
        Date convertedDate;

        try {
            convertedDate = simpleDateFormat.parse(date);
        } catch (Exception e) {
            return responseCreator.createResponse(HttpStatus.BAD_REQUEST, "employee.created.success",
                    null);
        }

        return responseCreator.createResponse(HttpStatus.CREATED, "employee.created.success",
                publicHolidayService.savePublicHoliday(convertedDate));
    }


}