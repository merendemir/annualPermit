package com.module.annual.permit.controller;

import com.module.annual.permit.enums.AnnualPermitStatus;
import com.module.annual.permit.service.AnnualPermitService;
import com.module.annual.permit.util.ResponseCreator;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/annual/permit")
public class AnnualPermitController {

    private final AnnualPermitService annualPermitService;

    private final ResponseCreator responseCreator;

    @Operation(description = "Date format it should be dd.mm.yyyy")
    @PostMapping("/request")
    public ResponseEntity<Object> createAnnualPermitRequest(@RequestParam String lang,
                                                            @RequestParam Long employeeId,
                                                            @RequestParam String annualPermitStartDate,
                                                            @RequestParam String annualPermitEndDate) {

        return responseCreator.createResponse(HttpStatus.CREATED, "annual.permit.request.success",
                annualPermitService.createAnnualPermitRequest(employeeId, annualPermitStartDate, annualPermitEndDate));
    }

    @GetMapping("/available")
    public ResponseEntity<Object> getAvailableAnnualPermitByEmployeeId(@RequestParam String lang,
                                                                       @RequestParam Long employeeId) {

        return responseCreator.createResponse(HttpStatus.OK, "employee.annual.permit.fetch.success",
                annualPermitService.getAvailableAnnualPermitByEmployeeId(employeeId));
    }

    @PostMapping("/admin/decision")
    public ResponseEntity<Object> ApproveOrRejectPermitRequest(@RequestParam String lang,
                                                               @RequestParam Long employeeId,
                                                               @RequestParam AnnualPermitStatus decision) {

        return responseCreator.createResponse(HttpStatus.OK, "annual.permit.answered.success",
                annualPermitService.updateEmployeeAnnualPermitRequestByDecision(employeeId, decision));
    }

}