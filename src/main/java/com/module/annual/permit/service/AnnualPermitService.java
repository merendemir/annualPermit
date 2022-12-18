package com.module.annual.permit.service;

import com.module.annual.permit.converter.AnnualPermitConverter;
import com.module.annual.permit.dto.AnnualPermitResponseDto;
import com.module.annual.permit.enums.AnnualPermitStatus;
import com.module.annual.permit.exceptions.DataAllReadyExistsException;
import com.module.annual.permit.exceptions.DataNotAcceptableException;
import com.module.annual.permit.exceptions.DataNotFoundException;
import com.module.annual.permit.model.AnnualPermit;
import com.module.annual.permit.model.Employee;
import com.module.annual.permit.repository.AnnualPermitRepository;
import com.module.annual.permit.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnualPermitService {

    @Value("${number-of.annual.permit.day.for.new.employee}")
    public int numberOfAnnualPermitDayForNewEmployee;

    @Value("${number-of.annual.permit.day.for.between.one-and-five-equal}")
    public int numberOfAnnualPermitDayForBetweenOneAndFiveEqual;

    @Value("${number-of.annual.permit.day.for.between.five-and-ten-equal}")
    public int numberOfAnnualPermitDayForBetweenFiveAndTenEqual;

    @Value("${number-of.annual.permit.day.for.greater-ten}")
    public int numberOfAnnualPermitDayForGreaterTen;

    public final AnnualPermitRepository annualPermitRepository;

    public final EmployeeService employeeService;

    public final AnnualPermitConverter annualPermitConverter;

    /**
     * @param annualPermit @Description Annual permit to be recorded in the database.
     * @return AnnualPermit
     *
     * This method takes AnnualPermit model, saves it to database and then returns the saved model.
     */
    public AnnualPermit save(AnnualPermit annualPermit) {
        return annualPermitRepository.save(annualPermit);
    }

    /**
     * @param employeeId @Description Id of the employee who has an annual permit request pending approval
     * @return AnnualPermit
     *
     * This method searches the database whether the employee has a pending request or not.
     * If found, it returns Annual Permit, otherwise it throws a DataNotFoundException.
     */
    public AnnualPermit findEmployeePendingAnnualPermit(Long employeeId) {
        return annualPermitRepository.findByEmployeeIdAndAnnualPermitStatus(employeeId, AnnualPermitStatus.PENDING)
                .orElseThrow(() ->  new DataNotFoundException("employee.not.exists.pending.request"));
    }

    /**
     *
     * @param employeeId @Description Id of the employee who will create the annual permit request.
     * @param annualPermitStartDate @Description The date on to start annual permit
     * @param annualPermitEndDate @DescriptionThe date on to end annual permit
     * @return AnnualPermitResponseDto
     *
     * This method allows the employee to create annual permit request
     *  steps:
     *    Step 1: Checks in every way that the dates entered for the annual permit are valid.
     *    Step 2: Calculates the net number of days
     *            the employee requests annual leave by deducting weekends and public holidays,
     *            if any, between the entered dates.
     *    Step 3: If the annual leave days requested by the employee are not more than the remaining annual leave days,
     *            it is saved as PENDING in the database.
     */
    public AnnualPermitResponseDto createAnnualPermitRequest (Long employeeId, String annualPermitStartDate, String annualPermitEndDate) {
        Employee employee = employeeService.findByIdOrElseThrow(employeeId);

        Date endOfPeriod = this.getEndOfPeriodByEmployeeStartDate(employee.getStartDate());
        String endOfPeriodAsFormat = DateUtil.formatDateToSimpleDateFormat(endOfPeriod);

        Boolean employeeHasPendingRequest = annualPermitRepository.existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING);

        if (employeeHasPendingRequest) {
            throw new DataAllReadyExistsException("employee.has.pending.request");
        }

        int availableAnnualPermit = this.getAvailableAnnualPermitByEmployeeId(employeeId);

        if (availableAnnualPermit <= 0) {
            throw new DataNotAcceptableException("employee.not.have.annual.permit", endOfPeriodAsFormat);
        }

        Date startDate;
        Date endDate;

        try {
            startDate = DateUtil.parseDateToSimpleDateFormat(annualPermitStartDate);
        } catch (Exception e) {
            throw new DataNotAcceptableException("invalid.annual.permit.start.date");
        }

        try {
            endDate = DateUtil.parseDateToSimpleDateFormat(annualPermitEndDate);
        } catch (Exception e) {
            throw new DataNotAcceptableException("invalid.annual.permit.end.date");
        }

        if (startDate.equals(endDate)) {
            throw new DataNotAcceptableException("annual.permit.start.end.date.cannot.equal");
        }

        if (endDate.before(startDate)) {
            throw new DataNotAcceptableException("annual.permit.date.cannot.earlier.start.date");
        }

        if (startDate.before(DateUtil.getStartOfDay(new Date()))){
            throw new DataNotAcceptableException("start.date.cannot.be.earlier.today");
        }

        if (endDate.after(this.getEndOfPeriodByEmployeeStartDate(employee.getStartDate()))) {
            throw new DataNotAcceptableException("end.date.must.be.before", endOfPeriodAsFormat);
        }

        Date annualPermitDate = startDate;

        int requiredAnnualPermitDays = 0;

        while (annualPermitDate.before(endDate)) {

            if (!DateUtil.isDayWeekend(annualPermitDate) && !PublicHolidayService.isDayPublicHoliday(annualPermitDate)) {
                requiredAnnualPermitDays ++;
            }

            annualPermitDate = DateUtil.getFutureDate(annualPermitDate, 1);
        }

        if (requiredAnnualPermitDays == 0) {
            throw new DataNotAcceptableException("annual.permit.weekend.or.public.holiday");
        }

        if (availableAnnualPermit < requiredAnnualPermitDays) {
            throw new DataNotAcceptableException("invalid.annual.permit.request.day", String.valueOf(availableAnnualPermit));
        }

        AnnualPermit savedAnnualPermit = this.save(AnnualPermit.builder()
                .employeeId(employee.getId())
                .startDate(startDate)
                .endDate(endDate)
                .annualPermitDays(requiredAnnualPermitDays)
                .annualPermitStatus(AnnualPermitStatus.PENDING)
                .build());

        return annualPermitConverter.convertAnnualPermitToAnnualPermitResponseDto(savedAnnualPermit);
    }

    /**
     * @param employeeId @Description The employee id whose available annual permit are to be brought.
     * @return int
     *
     * This method returns the employee's remaining annual permit in the relevant year.
     *  steps:
     *    Step 1: calculate how much annual leave you have based on how many years you have worked.
     *    Step 2: It finds from the database how much annual leave is used in the relevant year.
     *    Step 3: Calculates how much usage rights you have left by subtracting
     *            the usage in the relevant year from the annual Permit usage rights in the relevant year.
     */
    public int getAvailableAnnualPermitByEmployeeId(Long employeeId) {
        Employee employee = employeeService.findByIdOrElseThrow(employeeId);

        int entitlementInPeriod =
                this.getAnnualPermitDeservesByWorkedYear(
                        this.getTotalWorkedYearByStartDate(employee.getStartDate()));

        int usedAnnualPermit = this.getUsedAnnualPermitDayInPeriodByEmployee(employee);

        return entitlementInPeriod - usedAnnualPermit;
    }

    /**
     * @param employee @Description The employee you want to know how much used the annual permit
     * @return int
     *
     * This method, calculates how much annual permit the employee takes in the relevant year.
     */
    public int getUsedAnnualPermitDayInPeriodByEmployee(Employee employee) {
        Date startDate = employee.getStartDate();

        Date beginOfPeriod = this.getBeginOfPeriodByEmployeeStartDate(startDate);
        Date endOfPeriod = this.getEndOfPeriodByEmployeeStartDate(startDate);

        return annualPermitRepository.findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                employee.getId(), AnnualPermitStatus.APPROVED, beginOfPeriod, endOfPeriod)
                .stream()
                .map(AnnualPermit::getAnnualPermitDays)
                .reduce(0, Integer::sum);
    }

    /**
     * @param workedYear @Description The total number of years worked by the employee.
     * @return int
     *
     * This method, calculates the number of days of annual permit it deserves according to the number of years worked.
     */
    public int getAnnualPermitDeservesByWorkedYear(int workedYear) {
        if (workedYear < 1) {
            return numberOfAnnualPermitDayForNewEmployee;
        } else if (workedYear <= 5) {
            return numberOfAnnualPermitDayForBetweenOneAndFiveEqual;
        } else if (workedYear <= 10) {
            return numberOfAnnualPermitDayForBetweenFiveAndTenEqual;
        } else {        //that means more than ten years
            return numberOfAnnualPermitDayForGreaterTen;
        }
    }

    /**
     * @param startDate @Description Employee start date.
     * @return
     *
     * This method, calculate how many years worked by start date
     */
    public int getTotalWorkedYearByStartDate(Date startDate) {
        return DateUtil.getYearDifferenceBetweenDates(new Date(), startDate);
    }

    /**
     * @param startDate @Description employee start date
     * @return Date
     *
     * This method, calculates period start based on employee start date.
     */
    public Date getBeginOfPeriodByEmployeeStartDate(Date startDate) {
        int workedYear = this.getTotalWorkedYearByStartDate(startDate);
        return DateUtil.getFutureYearByDateAndYear(startDate, workedYear);
    }

    /**
     * @param endDate @Description Employee start date.
     * @return Date
     *
     * This method, calculates period end based on employee start date.
     */
    public Date getEndOfPeriodByEmployeeStartDate(Date endDate) {
        int workedYear = this.getTotalWorkedYearByStartDate(endDate);
        return DateUtil.getFutureYearByDateAndYear(endDate, workedYear + 1);
    }

    /**
     * @param employeeId @Description Employee Id to respond to annual permit request.
     * @param annualPermitStatus @Description Approval status of the request.
     * @return AnnualPermitResponseDto
     *
     * This method updates the employee's annual Permit request according to the admin decision regarding annual permit.
     */
    public AnnualPermitResponseDto updateEmployeeAnnualPermitRequestByDecision(Long employeeId, AnnualPermitStatus annualPermitStatus) {
        AnnualPermit annualPermit = this.findEmployeePendingAnnualPermit(employeeId);
        annualPermit.setAnnualPermitStatus(annualPermitStatus);

        return annualPermitConverter.convertAnnualPermitToAnnualPermitResponseDto(this.save(annualPermit));
    }
}
