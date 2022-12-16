package com.module.annual.permit.dto;

import com.module.annual.permit.enums.AnnualPermitStatus;
import lombok.Builder;
import lombok.ToString;

import java.util.Date;


@Builder
@ToString
public class AnnualPermitResponseDto {

    private Date startDate;

    private Date endDate;

    private int annualPermitDays;

    private AnnualPermitStatus annualPermitStatus;

    private Date createdOn;
}
