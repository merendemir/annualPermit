//package com.module.annual.permit.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class PublicHolidayService {
//
//    static List<String> publicHolidayAsFormatList = new ArrayList<>();
//
//    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
//
//    @PostConstruct
//    public void init() {
//        this.updatePublicHolidayAsFormatList();
//    }
//
//    private final PublicHolidayRepository publicHolidayRepository;
//
//    public PublicHoliday savePublicHoliday(Date date) {
//        PublicHoliday publicHoliday = publicHolidayRepository.save(new PublicHoliday(date));
//        this.updatePublicHolidayAsFormatList();
//        return publicHoliday;
//    }
//
//    public List<PublicHoliday> getAllPublicHolidayByYear(int year) {
//        return publicHolidayRepository.findAllByDate_Year(year);
//    }
//
//    public static Boolean isDayPublicHoliday(Date date) {
//        try {
//            String formattedDate = simpleDateFormat.format(date);
//
//            if (publicHolidayAsFormatList.toString().contains(formattedDate)) {
//                return true;
//            }
//        } catch (Exception e) {
//            log.error("date format exception: {}", e.getMessage());
//            e.printStackTrace();
//        }
//
//        return false;
//    }
//
//    public void updatePublicHolidayAsFormatList() {
//        Calendar calendar = Calendar.getInstance();
//        this.getAllPublicHolidayByYear(calendar.get(Calendar.YEAR)).forEach(publicHoliday -> {
//            try {
//                publicHolidayAsFormatList.add(
//                        simpleDateFormat.format(publicHoliday.getDate()));
//            } catch (Exception e) {
//                log.error("date format exception: {}", e.getMessage());
//                e.printStackTrace();
//            }
//        });
//    }
//
//}
