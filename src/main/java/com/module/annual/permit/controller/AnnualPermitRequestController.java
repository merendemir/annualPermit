package com.module.annual.permit.controller;

import com.module.annual.permit.enums.AnnualPermitRequestStatus;
import com.module.annual.permit.service.AnnualPermitRequestService;
import com.module.annual.permit.util.ResponseCreator;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/annual/permit")
public class AnnualPermitRequestController {

    private final AnnualPermitRequestService annualPermitRequestService;

    private final ResponseCreator responseCreator;



    @Operation(description = "Date format it shoult be dd.mm.yyyy")
    @PostMapping("/employee/request")
    public ResponseEntity<Object> createAnnualPermitRequest(@RequestParam String lang,
                                                            @RequestParam Long employeeId,
                                                            @RequestParam String annualPermitStartDate,
                                                            @RequestParam String annualPermitEndDate) {

        return responseCreator.createResponse(HttpStatus.CREATED, "annual.permit.request.success",
                annualPermitRequestService.createAnnualPermitRequest(employeeId, annualPermitStartDate, annualPermitEndDate));
    }

    @PostMapping("/admin/decision")
    public ResponseEntity<Object> ApproveOrRejectPermitRequest(@RequestParam String lang,
                                                               @RequestParam Long annualPermitRequestId,
                                                               @RequestParam AnnualPermitRequestStatus decision) {

        return responseCreator.createResponse(HttpStatus.OK, "annual.permit.request.success",
                annualPermitRequestService.updateEmployeeAnnualPermitRequestByDecision(annualPermitRequestId, decision));
    }

}