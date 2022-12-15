package com.module.annual.permit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Builder
@Getter
@ToString
public class EmployeeResponseDto {

    private Long id;

    private String name;

    private String lastName;

    private Date startDate;

}
