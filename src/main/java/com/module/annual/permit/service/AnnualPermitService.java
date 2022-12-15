package com.module.annual.permit.service;

import com.module.annual.permit.model.AnnualPermit;
import com.module.annual.permit.repository.AnnualPermitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnualPermitService {


    @Value("${number-of.annual.permit.day.for.new.employee}")
    private int numberOfAnnualPermitDayForNewEmployee;

    private final AnnualPermitRepository annualPermitRepository;

    public AnnualPermit createNewAnnualPermit() {
        return annualPermitRepository.save(
                new AnnualPermit(numberOfAnnualPermitDayForNewEmployee));
    }

    public AnnualPermit save (AnnualPermit annualPermit) {
        return annualPermitRepository.save(annualPermit);
    }

}
