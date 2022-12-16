package com.module.annual.permit.model;

import com.module.annual.permit.enums.AnnualPermitRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnualPermitRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date annualPermitStartDate;

    private Date annualPermitEndDate;

    private int requiredAnnualPermitDays;

    @Enumerated(EnumType.STRING)
    private AnnualPermitRequestStatus annualPermitRequestStatus;

    @JoinColumn
    @OneToOne
    private Employee employee;

}
