package com.module.annual.permit.controller;

import com.module.annual.permit.dto.NewEmployeeRequestDto;
import com.module.annual.permit.service.AnnualPermitRequestService;
import com.module.annual.permit.service.EmployeeService;
import com.module.annual.permit.util.ResponseCreator;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    private final AnnualPermitRequestService annualPermitRequestService;

    private final ResponseCreator responseCreator;

    @PostMapping
    public ResponseEntity<Object> createNewEmployee(@RequestParam String lang,
                                                    @Valid @RequestBody NewEmployeeRequestDto newEmployeeRequestDto) {

        return responseCreator.createResponse(HttpStatus.CREATED, "employee.created.success",
                employeeService.createNewEmployee(newEmployeeRequestDto));
    }

    @GetMapping
    public ResponseEntity<Object> getEmployee(@RequestParam String lang,
                                              @RequestParam Long employeeId) {

        return responseCreator.createResponse(HttpStatus.CREATED, "employee.fetch.success",
                employeeService.getEmployeeById(employeeId));
    }

    @GetMapping("/annual/permit")
    public ResponseEntity<Object> getAnnualPermitByEmployeeId(@RequestParam String lang,
                                                              @RequestParam Long employeeId) {

        return responseCreator.createResponse(HttpStatus.CREATED, "employee.annual.permit.fetch.success",
                employeeService.getAnnualPermitByEmployeeId(employeeId));
    }

}