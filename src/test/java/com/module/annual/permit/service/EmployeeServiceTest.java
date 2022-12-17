package com.module.annual.permit.service;

import com.module.annual.permit.converter.EmployeeConverter;
import com.module.annual.permit.dto.EmployeeResponseDto;
import com.module.annual.permit.dto.NewEmployeeRequestDto;
import com.module.annual.permit.exceptions.DataNotFoundException;
import com.module.annual.permit.model.Employee;
import com.module.annual.permit.repository.EmployeeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {

    private EmployeeService employeeService;

    private EmployeeRepository employeeRepository;

    private EmployeeConverter employeeConverter;

    @Before
    public void setUp() throws Exception {
        employeeRepository = mock(EmployeeRepository.class);
        employeeConverter = mock(EmployeeConverter.class);

        employeeService = new EmployeeService(
                employeeRepository,
                employeeConverter);
    }

    @Test
    public void whenCreateNewEmployeeCalled_thenItShouldReturnEmployeeResponseDto() {
        //given

        NewEmployeeRequestDto newEmployeeRequestDto = NewEmployeeRequestDto.builder()
                .name("testName")
                .lastName("testLastName")
                .build();

        Employee employee = Employee.builder()
                .id(1L)
                .name(newEmployeeRequestDto.getName())
                .lastName(newEmployeeRequestDto.getLastName())
                .startDate(new Date())
                .build();

        EmployeeResponseDto employeeResponseDto = EmployeeResponseDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .lastName(employee.getLastName())
                .startDate(employee.getStartDate())
                .build();

        //when
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeConverter.convertEmployeeToEmployeeResponseDto(employee)).thenReturn(employeeResponseDto);

        //then
        EmployeeResponseDto actual = employeeService.createNewEmployee(newEmployeeRequestDto);

        assertEquals(employeeResponseDto, actual);

        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(employeeConverter, times(1)).convertEmployeeToEmployeeResponseDto(employee);
    }

    @Test
    public void whenFindByIdOrElseThrowCalledWithExistsEmployeeId_itShouldReturnEmployee() {
        //given
        Employee employee = Employee.builder()
                .id(1L)
                .build();

        //when
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        //then
        Employee actual = employeeService.findByIdOrElseThrow(employee.getId());

        assertEquals(employee, actual);

        verify(employeeRepository, times(1)).findById(employee.getId());
    }

    @Test(expected = DataNotFoundException.class)
    public void whenFindByIdOrElseThrowCalledWithNonExistsEmployeeId_itShouldThrowDataNotFoundException() {
        //given
        long employeeId = -1;

        //when
        when(employeeRepository.findById(employeeId)).thenThrow(new DataNotFoundException("employee.not.found.by.id"));

        //then
        Employee actual = employeeService.findByIdOrElseThrow(employeeId);

        assertNull(actual);

        Mockito.verify(employeeRepository, times(1)).findById(employeeId);
    }

}