package com.module.annual.permit.service;

import com.module.annual.permit.model.PublicHoliday;
import com.module.annual.permit.repository.PublicHolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicHolidayService {

    private final PublicHolidayRepository publicHolidayRepository;

    public PublicHoliday savePublicHoliday(Date date) {
        return publicHolidayRepository.save(new PublicHoliday(date));
    }

    public List<PublicHoliday> getAllPublicHolidayByYear(int year) {
        return publicHolidayRepository.findAllByDate_Year(year);
    }

}
