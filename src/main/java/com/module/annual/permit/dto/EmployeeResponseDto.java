package com.module.annual.permit.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
@EqualsAndHashCode
public class EmployeeResponseDto {

    private Long id;

    private String name;

    private String lastName;

    private Date startDate;

}
