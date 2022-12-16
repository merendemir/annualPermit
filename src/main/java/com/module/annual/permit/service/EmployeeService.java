package com.module.annual.permit.service;

import com.module.annual.permit.converter.EmployeeConverter;
import com.module.annual.permit.dto.NewEmployeeRequestDto;
import com.module.annual.permit.dto.EmployeeResponseDto;
import com.module.annual.permit.exceptions.DataNotFoundException;
import com.module.annual.permit.model.AnnualPermit;
import com.module.annual.permit.model.Employee;
import com.module.annual.permit.repository.EmployeeRepository;
import com.module.annual.permit.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Value("${number-of.annual.permit.day.for.new.employee}")
    private int numberOfAnnualPermitDayForNewEmployee;

    @Value("${number-of.annual.permit.day.for.between.one-and-five-equal}")
    private int numberOfAnnualPermitDayForBetweenOneAndFiveEqual;

    @Value("${number-of.annual.permit.day.for.between.five-and-ten-equal}")
    private int numberOfAnnualPermitDayForBetweenFiveAndTenEqual;

    @Value("${number-of.annual.permit.day.for.greater-ten}")
    private int numberOfAnnualPermitDayForGreaterTen;


    private final EmployeeRepository employeeRepository;

    private final EmployeeAnnualPermitService employeeAnnualPermitService;

    private final EmployeeConverter employeeConverter;

    public EmployeeResponseDto createNewEmployee(NewEmployeeRequestDto requestDto) {
        Employee employee = new Employee();

        employee.setName(requestDto.getName());
        employee.setLastName(requestDto.getLastName());
        employee.setAnnualPermit(employeeAnnualPermitService.createNewAnnualPermit());

        return employeeConverter.convertEmployeeToEmployeeResponseDto(
                    this.save(employee));
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public EmployeeResponseDto getEmployeeById(Long employeeId) {
        return employeeConverter.convertEmployeeToEmployeeResponseDto(
                this.findByIdOrElseThrow(employeeId));
    }

    public Employee findByIdOrElseThrow(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(
                        () ->  new DataNotFoundException("employee.not.found.by.id", employeeId.toString()));
    }

    public int getAnnualPermitByEmployeeId(Long employeeId) {
        return this.findByIdOrElseThrow(employeeId).getAnnualPermit().getRemainingDaysOff();
    }

    @Scheduled(cron = "0 0 0 * * ?")  //every midnight
    public void updateEmployeePermit() {
        this.findAllEmployeeWhoseEmploymentAnniversaryIsToday().forEach(employee -> {
            AnnualPermit annualPermit = employee.getAnnualPermit();

            annualPermit.setRemainingDaysOff(this.calculateEmployeeNewAnnualPermit(employee));

            employeeAnnualPermitService.save(annualPermit);
        });
    }

    public List<Employee> findAllEmployeeWhoseEmploymentAnniversaryIsToday() {
        Calendar calendar = Calendar.getInstance();

        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return employeeRepository.findAllByStartDate_DayAndStartDate_Month(day, month);
    }

    private int calculateEmployeeNewAnnualPermit(Employee employee) {
        int totalWorkedYear = DateUtil.getYearDifferenceBetweenDates(new Date(), employee.getStartDate());
        int newAnnualPermit = employee.getAnnualPermit().getRemainingDaysOff();

        if (totalWorkedYear == 1) {
            newAnnualPermit = numberOfAnnualPermitDayForBetweenOneAndFiveEqual;
        } else if (totalWorkedYear > 1 && totalWorkedYear <= 5) {
            newAnnualPermit = newAnnualPermit + numberOfAnnualPermitDayForBetweenOneAndFiveEqual;
        } else if (totalWorkedYear > 5 && totalWorkedYear <= 10) {
            newAnnualPermit = newAnnualPermit + numberOfAnnualPermitDayForBetweenFiveAndTenEqual;
        } else if (totalWorkedYear > 10) {
            newAnnualPermit = newAnnualPermit + numberOfAnnualPermitDayForGreaterTen;
        }

        return newAnnualPermit;
    }

}
