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

    /**
     * @param newEmployeeRequestDto @Description Containing the necessary information to create a new employee.
     * @return EmployeeResponseDto
     *
     * This method creates a new employee with name and last name in newEmployeeRequestDto
     * And then converts from Employee to Employee ResponseDto
     */
    public EmployeeResponseDto createNewEmployee(NewEmployeeRequestDto newEmployeeRequestDto) {
        Employee savedEmployee = employeeRepository.save(
                Employee.builder()
                        .name(newEmployeeRequestDto.getName())
                        .lastName(newEmployeeRequestDto.getLastName())
                        .build());

        return employeeConverter.convertEmployeeToEmployeeResponseDto(savedEmployee);
    }


    /**
     * @param employeeId @Description The id of the employee whose information is desired to be accessed.
     * @return Employee
     *
     * This method searches employee in database with employee Id
     * If found, returns Employee, otherwise throws DataNotFoundException.
     */
    public Employee findByIdOrElseThrow(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(
                        () ->  new DataNotFoundException("employee.not.found.by.id", employeeId.toString()));
    }

}
