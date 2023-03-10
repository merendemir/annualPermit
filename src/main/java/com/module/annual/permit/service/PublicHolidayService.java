package com.module.annual.permit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.module.annual.permit.feign.FeignCallForPublicHoliday;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicHolidayService {

    public static List<String> publicHolidayAsFormatList = new ArrayList<>();

    public static SimpleDateFormat publicHolidayExternalFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final FeignCallForPublicHoliday feignCallForPublicHoliday;

    /**
     * This method runs before the application is run
     */
    @PostConstruct
    public void init() {
        this.updatePublicHolidayAsFormatList();
    }

    /**
     *
     * @param date @Description The day you want to know whether it is a public holiday or not
     * @return Boolean
     *
     * This method checks whether the given date is in the publicHolidayAsFormatList
     * and decides whether the date is a public holiday or not.
     */
    public static Boolean isDayPublicHoliday(Date date) {
        try {
            String formattedDate = publicHolidayExternalFormat.format(date);

            if (publicHolidayAsFormatList.toString().contains(formattedDate)) {
                return true;
            }
        } catch (Exception e) {
            log.error("date format exception: {}", e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * This method saves public holiday dates in the publicHolidayAsFormatList list by making a FeignCall.
     */
    public void updatePublicHolidayAsFormatList() {
        try {

            log.info("fetching public holidays from external source");

            ResponseEntity<Object> response = feignCallForPublicHoliday.getPublicHolidaysFromExternal();

            ObjectMapper mapper = new ObjectMapper();
            JSONObject jsonObject = new JSONObject(mapper.writeValueAsString(response.getBody()));

            mapper.readValue(jsonObject.get("resmitatiller").toString(), List.class)
                    .forEach(data -> {
                        try {
                            JSONObject entity = new JSONObject(mapper.writeValueAsString(data));

                            publicHolidayAsFormatList.add(entity.get("tarih").toString());
                        } catch (JsonProcessingException e) {
                            log.error("public holidays could not be parsed");
                        }
                    });

            log.info("fetched public holidays from external source");
        } catch (Exception e) {
            log.error("could not get public holidays please check your internet connection");
        }
    }

}
