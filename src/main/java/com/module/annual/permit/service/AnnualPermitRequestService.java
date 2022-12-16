package com.module.annual.permit.service;

import com.module.annual.permit.enums.AnnualPermitRequestStatus;
import com.module.annual.permit.exceptions.DataNotAcceptableException;
import com.module.annual.permit.exceptions.DataNotFoundException;
import com.module.annual.permit.model.AnnualPermit;
import com.module.annual.permit.model.AnnualPermitRequest;
import com.module.annual.permit.model.Employee;
import com.module.annual.permit.repository.AnnualPermitRequestRepository;
import com.module.annual.permit.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnualPermitRequestService {

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private final AnnualPermitRequestRepository annualPermitRequestRepository;

    private final EmployeeService employeeService;
    private final EmployeeAnnualPermitService employeeAnnualPermitService;

    public AnnualPermitRequest save(AnnualPermitRequest annualPermitRequest) {
        return annualPermitRequestRepository.save(annualPermitRequest);
    }

    public AnnualPermitRequest findByIdOrElseThrow(Long requestId) {
        return annualPermitRequestRepository.findById(requestId)
                .orElseThrow(
                        () ->  new DataNotFoundException("annual.permit.not.found.by.id", requestId.toString()));
    }

    public AnnualPermitRequest createAnnualPermitRequest (Long employeeId, String annualPermitStartDate, String annualPermitEndDate) {
        Employee employee = employeeService.findByIdOrElseThrow(employeeId);

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

        AnnualPermit employeeAnnualPermit = employee.getAnnualPermit();

        int remainingNewAnnualPermit = employeeAnnualPermit.getRemainingDaysOff() - requiredAnnualPermitDays;

        if (remainingNewAnnualPermit <= 0) {
            throw new DataNotAcceptableException("invalid.annual.permit.request.day");
        }

        employeeAnnualPermit.setRemainingDaysOff(remainingNewAnnualPermit);

        employeeAnnualPermitService.save(employeeAnnualPermit);

        return this.save(AnnualPermitRequest.builder()
                            .annualPermitStartDate(startDate)
                            .annualPermitEndDate(endDate)
                            .requiredAnnualPermitDays(requiredAnnualPermitDays)
                            .employee(employee)
                            .annualPermitRequestStatus(AnnualPermitRequestStatus.AWAITING_APPROVAL)
                        .build());

    }

    public AnnualPermitRequestStatus updateEmployeeAnnualPermitRequestByDecision(Long annualPermitRequestId, AnnualPermitRequestStatus requestStatus) {
        AnnualPermitRequest annualPermitRequest = this.findByIdOrElseThrow(annualPermitRequestId);

        annualPermitRequest.setAnnualPermitRequestStatus(requestStatus);
        AnnualPermitRequest savedRequest = this.save(annualPermitRequest);

        if (requestStatus.equals(AnnualPermitRequestStatus.DENIED)) {
            AnnualPermit annualPermit = savedRequest.getEmployee().getAnnualPermit();
            annualPermit.setRemainingDaysOff(annualPermit.getRemainingDaysOff() + savedRequest.getRequiredAnnualPermitDays());
            employeeAnnualPermitService.save(annualPermit);
        }

        return savedRequest.getAnnualPermitRequestStatus();
    }




}
