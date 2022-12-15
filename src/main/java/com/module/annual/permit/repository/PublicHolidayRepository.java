package com.module.annual.permit.repository;

import com.module.annual.permit.model.PublicHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PublicHolidayRepository extends JpaRepository<PublicHoliday, Long> {

    @Query("select publicHoliday from PublicHoliday publicHoliday where year (publicHoliday.date) = ?1")
    List<PublicHoliday> findAllByDate_Year(int year);


}
