package com.module.annual.permit.converter;

import com.module.annual.permit.dto.EmployeeResponseDto;
import com.module.annual.permit.model.Employee;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class EmployeeConverterTest {

    private EmployeeConverter employeeConverter;

    @Before
    public void setUp() throws Exception {
        employeeConverter = new EmployeeConverter();
    }

    @Test
    public void whenConvertEmployeeToEmployeeResponseDtoCalled_itShouldReturnEmployeeResponseDto() {
        //given
        Employee employee = Employee.builder()
                .id(1L)
                .name("testName")
                .lastName("testLastName")
                .startDate(new Date())
                .build();

        EmployeeResponseDto employeeResponseDto = EmployeeResponseDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .lastName(employee.getLastName())
                .startDate(employee.getStartDate())
                .build();

        //then
        EmployeeResponseDto actual = employeeConverter.convertEmployeeToEmployeeResponseDto(employee);

        assertEquals(employeeResponseDto, actual);
    }
}