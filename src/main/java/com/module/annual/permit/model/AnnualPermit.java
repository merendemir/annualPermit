package com.module.annual.permit.model;

import com.module.annual.permit.enums.AnnualPermitStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AnnualPermit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long employeeId;

    private Date startDate;

    private Date endDate;

    private int annualPermitDays;

    @Enumerated(EnumType.STRING)
    private AnnualPermitStatus annualPermitStatus;

    @CreationTimestamp
    private Date createdOn;
}
