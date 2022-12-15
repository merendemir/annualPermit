package com.module.annual.permit.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class NewEmployeeRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String lastName;

}
