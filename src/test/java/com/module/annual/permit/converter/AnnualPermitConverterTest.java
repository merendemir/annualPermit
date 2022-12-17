package com.module.annual.permit.converter;

import com.module.annual.permit.dto.AnnualPermitResponseDto;
import com.module.annual.permit.enums.AnnualPermitStatus;
import com.module.annual.permit.model.AnnualPermit;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AnnualPermitConverterTest {

    private AnnualPermitConverter annualPermitConverter;

    @Before
    public void setUp() {
        annualPermitConverter = new AnnualPermitConverter();
    }

    @Test
    public void whenConvertAnnualPermitToAnnualPermitResponseDtoCalled_itShouldReturnAnnualPermitResponseDto() {
        //given
        AnnualPermit annualPermit = AnnualPermit.builder()
                .annualPermitDays(5)
                .annualPermitStatus(AnnualPermitStatus.PENDING)
                .build();

        AnnualPermitResponseDto annualPermitResponseDto = AnnualPermitResponseDto.builder()
                .annualPermitDays(annualPermit.getAnnualPermitDays())
                .annualPermitStatus(annualPermit.getAnnualPermitStatus())
                .build();

        //then
        AnnualPermitResponseDto actual = annualPermitConverter.convertAnnualPermitToAnnualPermitResponseDto(annualPermit);

        assertEquals(annualPermitResponseDto, actual);
    }

}