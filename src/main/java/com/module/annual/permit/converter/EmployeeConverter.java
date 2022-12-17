package com.module.annual.permit.converter;

import com.module.annual.permit.dto.EmployeeResponseDto;
import com.module.annual.permit.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeConverter {

    /**
     * @param employee @Description Employee model to be converted.
     * @return EmployeeResponseDto
     *
     * This method converts Employee to EmployeeResponseDto.
     */
    public EmployeeResponseDto convertEmployeeToEmployeeResponseDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .lastName(employee.getLastName())
                .startDate(employee.getStartDate())
                .build();
    }
}
