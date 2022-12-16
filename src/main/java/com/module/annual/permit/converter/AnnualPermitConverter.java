package com.module.annual.permit.converter;

import com.module.annual.permit.dto.AnnualPermitResponseDto;
import com.module.annual.permit.model.AnnualPermit;
import org.springframework.stereotype.Component;

@Component
public class AnnualPermitConverter {
    public AnnualPermitResponseDto convertAnnualPermitToAnnualPermitResponseDto(AnnualPermit annualPermit) {
        return AnnualPermitResponseDto.builder()
                .startDate(annualPermit.getStartDate())
                .endDate(annualPermit.getEndDate())
                .annualPermitDays(annualPermit.getAnnualPermitDays())
                .annualPermitStatus(annualPermit.getAnnualPermitStatus())
                .createdOn(annualPermit.getCreatedOn())
                .build();
    }
}
