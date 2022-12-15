package com.module.annual.permit.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class AnnualPermit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int remainingDaysOff;

    public AnnualPermit(int remainingDaysOff) {
        this.remainingDaysOff = remainingDaysOff;
    }
}
