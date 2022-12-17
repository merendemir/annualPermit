package com.module.annual.permit.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class NewEmployeeRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String lastName;

}
