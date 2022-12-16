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

    @Operation(summary = "This api, generates annual permit request for employees", description = "startDate and endDate must be in the format dd.mm.yyyy")
    @PostMapping("/request")
    public ResponseEntity<Object> createAnnualPermitRequest(@RequestParam String lang,
                                                            @RequestParam Long employeeId,
                                                            @RequestParam String startDate,
                                                            @RequestParam String endDate) {

        return responseCreator.createResponse(HttpStatus.CREATED, "annual.permit.request.success",
                annualPermitService.createAnnualPermitRequest(employeeId, startDate, endDate));
    }

    @Operation(summary = "This api, fetch available annual permit for employees", description = "employeeId cannot be null")
    @GetMapping("/available")
    public ResponseEntity<Object> getAvailableAnnualPermitByEmployeeId(@RequestParam String lang,
                                                                       @RequestParam Long employeeId) {

        return responseCreator.createResponse(HttpStatus.OK, "employee.annual.permit.fetch.success",
                annualPermitService.getAvailableAnnualPermitByEmployeeId(employeeId));
    }

    @Operation(summary = "This api for approving or denying annual permit", description = "This api is for admin only!")
    @PostMapping("/admin/decision")
    public ResponseEntity<Object> ApproveOrRejectPermitRequest(@RequestParam String lang,
                                                               @RequestParam Long employeeId,
                                                               @RequestParam AnnualPermitStatus decision) {

        return responseCreator.createResponse(HttpStatus.OK, "annual.permit.answered.success",
                annualPermitService.updateEmployeeAnnualPermitRequestByDecision(employeeId, decision));
    }

}