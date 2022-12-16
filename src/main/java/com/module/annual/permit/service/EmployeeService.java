package com.module.annual.permit.service;

import com.module.annual.permit.converter.EmployeeConverter;
import com.module.annual.permit.dto.EmployeeResponseDto;
import com.module.annual.permit.dto.NewEmployeeRequestDto;
import com.module.annual.permit.exceptions.DataNotFoundException;
import com.module.annual.permit.model.Employee;
import com.module.annual.permit.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final EmployeeConverter employeeConverter;

    public EmployeeResponseDto createNewEmployee(NewEmployeeRequestDto requestDto) {
        Employee savedEmployee = employeeRepository.save(
                Employee.builder()
                        .name(requestDto.getName())
                        .lastName(requestDto.getLastName())
                        .build());

        return employeeConverter.convertEmployeeToEmployeeResponseDto(savedEmployee);
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee findByIdOrElseThrow(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(
                        () ->  new DataNotFoundException("employee.not.found.by.id", employeeId.toString()));
    }

}
