package com.module.annual.permit.controller;

import com.module.annual.permit.dto.NewEmployeeRequestDto;
import com.module.annual.permit.service.EmployeeService;
import com.module.annual.permit.util.ResponseCreator;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    private final ResponseCreator responseCreator;

    @Operation(summary = "This api for create new employee", description = "name and lastName cannot be blank")
    @PostMapping("/create")
    public ResponseEntity<Object> createNewEmployee(@RequestParam String lang,
                                                    @Valid @RequestBody NewEmployeeRequestDto newEmployeeRequestDto) {

        return responseCreator.createResponse(HttpStatus.CREATED, "employee.created.success",
                employeeService.createNewEmployee(newEmployeeRequestDto));
    }

}