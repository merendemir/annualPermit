package com.module.annual.permit.service;

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

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnualPermitService {


    @Value("${number-of.annual.permit.day.for.new.employee}")
    private int numberOfAnnualPermitDayForNewEmployee;

    @Value("${number-of.annual.permit.day.for.between.one-and-five-equal}")
    private int numberOfAnnualPermitDayForBetweenOneAndFiveEqual;

    @Value("${number-of.annual.permit.day.for.between.five-and-ten-equal}")
    private int numberOfAnnualPermitDayForBetweenFiveAndTenEqual;

    @Value("${number-of.annual.permit.day.for.greater-ten}")
    private int numberOfAnnualPermitDayForGreaterTen;

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private final AnnualPermitRepository annualPermitRepository;

    private final EmployeeService employeeService;

    private AnnualPermit save(AnnualPermit annualPermit) {
        return annualPermitRepository.save(annualPermit);
    }

    private AnnualPermit findEmployePendingAnnualPermit(Long employeeId) {
        return annualPermitRepository.findByEmployeeIdAndAnnualPermitStatus(employeeId, AnnualPermitStatus.PENDING)
                .orElseThrow(
                        () ->  new DataNotFoundException("employee.not.exists.pending.request"));
    }

    public AnnualPermit createAnnualPermitRequest (Long employeeId, String annualPermitStartDate, String annualPermitEndDate) {
        Employee employee = employeeService.findByIdOrElseThrow(employeeId);

        Date endOfPeriod = this.getEndOfPeriodByEmployeStartDate(employee.getStartDate());
        String endOfPeriodAsFormat = "";

        try {
            endOfPeriodAsFormat = simpleDateFormat.format(endOfPeriod);
        } catch (Exception e) {
            log.error("simpleDateFormat format: {}", e.getMessage());
        }

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
            startDate = simpleDateFormat.parse(annualPermitStartDate);
        } catch (Exception e) {
            throw new DataNotAcceptableException("invalid.annual.permit.start.date");
        }

        try {
            endDate = simpleDateFormat.parse(annualPermitEndDate);
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
            throw new DataNotAcceptableException("start.date.must.be.after.today");
        }

        if (endDate.after(this.getEndOfPeriodByEmployeStartDate(employee.getStartDate()))) {
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
            throw new DataNotAcceptableException("invalid.annual.permit.request.day");
        }

        return this.save(AnnualPermit.builder()
                            .employeeId(employee.getId())
                            .startDate(startDate)
                            .endDate(endDate)
                            .annualPermitDays(requiredAnnualPermitDays)
                            .annualPermitStatus(AnnualPermitStatus.PENDING)
                        .build());
    }

    public AnnualPermitStatus updateEmployeeAnnualPermitRequestByDecision(Long employeeId, AnnualPermitStatus annualPermitStatus) {
        AnnualPermit annualPermit = this.findEmployePendingAnnualPermit(employeeId);
        annualPermit.setAnnualPermitStatus(annualPermitStatus);

        return this.save(annualPermit).getAnnualPermitStatus();
    }


    public int getAvailableAnnualPermitByEmployeeId(Long employeeId) {
        Employee employee = employeeService.findByIdOrElseThrow(employeeId);

        int usedAnnualPermit = this.getUsedAnnualPermitDayInPeriodByEmployee(employee);
        int entitlementInPeriod =
                this.getAnnualPermitEntitlementByWorkedYear(
                        this.getTotalWorkedYearByStartDate(employee.getStartDate()));

        return entitlementInPeriod - usedAnnualPermit;
    }

    private int getUsedAnnualPermitDayInPeriodByEmployee(Employee employee) {
        Date startDate = employee.getStartDate();

        Date beginOfPeriod = this.getBeginOfPeriodByEmployeStartDate(startDate);
        Date endOfPeriod = this.getEndOfPeriodByEmployeStartDate(startDate);

        return annualPermitRepository.findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                employee.getId(), AnnualPermitStatus.APPROVED, beginOfPeriod, endOfPeriod)
                .stream()
                .map(AnnualPermit::getAnnualPermitDays)
                .reduce(0, Integer::sum);
    }

    private int getAnnualPermitEntitlementByWorkedYear(int workedYear) {
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

    private int getTotalWorkedYearByStartDate(Date startDate) {
        return DateUtil.getYearDifferenceBetweenDates(new Date(), startDate);
    }

    private Date getBeginOfPeriodByEmployeStartDate(Date startDate) {
        int workedYear = this.getTotalWorkedYearByStartDate(startDate);
        return DateUtil.getFutureYearByDateAndYear(startDate, workedYear);
    }

    private Date getEndOfPeriodByEmployeStartDate(Date startDate) {
        int workedYear = this.getTotalWorkedYearByStartDate(startDate);
        return DateUtil.getFutureYearByDateAndYear(startDate, workedYear + 1);
    }

}
