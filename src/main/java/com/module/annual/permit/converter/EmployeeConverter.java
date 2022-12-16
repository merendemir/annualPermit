package com.module.annual.permit.converter;

import com.module.annual.permit.dto.EmployeeResponseDto;
import com.module.annual.permit.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeConverter {

    public EmployeeResponseDto convertEmployeeToEmployeeResponseDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .lastName(employee.getLastName())
                .startDate(employee.getStartDate())
                .build();
    }
}
