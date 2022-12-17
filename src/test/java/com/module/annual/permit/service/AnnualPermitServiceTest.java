package com.module.annual.permit.service;

import com.module.annual.permit.converter.AnnualPermitConverter;
import com.module.annual.permit.dto.AnnualPermitResponseDto;
import com.module.annual.permit.enums.AnnualPermitStatus;
import com.module.annual.permit.exceptions.DataAllReadyExistsException;
import com.module.annual.permit.exceptions.DataNotAcceptableException;
import com.module.annual.permit.exceptions.DataNotFoundException;
import com.module.annual.permit.model.AnnualPermit;
import com.module.annual.permit.model.Employee;
import com.module.annual.permit.repository.AnnualPermitRepository;
import com.module.annual.permit.util.DateUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class AnnualPermitServiceTest {

    private AnnualPermitService annualPermitService;

    private AnnualPermitRepository annualPermitRepository;

    private EmployeeService employeeService;

    private AnnualPermitConverter annualPermitConverter;


    @BeforeClass
    public static void staticSetUp() {
        mockStatic(DateUtil.class);
        mockStatic(PublicHolidayService.class);
        mockStatic(SimpleDateFormat.class);
    }

    @Before
    public void setUp() {
        annualPermitRepository = mock(AnnualPermitRepository.class);
        employeeService = mock(EmployeeService.class);
        annualPermitConverter = mock(AnnualPermitConverter.class);

        annualPermitService = new AnnualPermitService(
                annualPermitRepository,
                employeeService,
                annualPermitConverter);

    }

    @Test
    public void whenSaveCalledWithAnnualPermit_itShouldReturnAnnualPermit() {
        //given
        AnnualPermit annualPermit = AnnualPermit.builder()
                .id(1L)
                .build();

        //when
        when(annualPermitRepository.save(any(AnnualPermit.class))).thenReturn(annualPermit);

        //then
        AnnualPermit actual = annualPermitService.save(annualPermit);

        assertEquals(annualPermit, actual);

        verify(annualPermitRepository, times(1)).save(any(AnnualPermit.class));
    }

    @Test
    public void whenFindEmployeePendingAnnualPermitCalledAndExistsEmployeePendingRequest_itShouldReturnAnnualPermit() {
        //given
        Employee employee = Employee.builder()
                .id(2L)
                .build();

        AnnualPermit annualPermit = AnnualPermit.builder()
                .id(1L)
                .employeeId(employee.getId())
                .annualPermitStatus(AnnualPermitStatus.PENDING)
                .build();

        //when
        when(annualPermitRepository.findByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING))
                .thenReturn(Optional.of(annualPermit));

        //then
        AnnualPermit actual = annualPermitService.findEmployeePendingAnnualPermit(employee.getId());

        assertEquals(annualPermit, actual);
        assertEquals(employee.getId(), actual.getEmployeeId());

        verify(annualPermitRepository, times(1))
                .findByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING);
    }

    @Test(expected = DataNotFoundException.class)
    public void whenFindEmployeePendingAnnualPermitCalledAndNonExistsEmployeePendingRequest_itShouldThrowDataNotFoundException() {
        //given
        Employee employee = Employee.builder()
                .id(2L)
                .build();

        //when
        when(annualPermitRepository.findByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING))
                .thenThrow(new DataNotFoundException("employee.not.exists.pending.request"));

        //then
        AnnualPermit actual = annualPermitService.findEmployeePendingAnnualPermit(employee.getId());

        assertNull(actual);

        verify(annualPermitRepository, times(1))
                .findByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING);
    }

    @Test
    public void whenGetTotalWorkedYearByStartDateCalled_itShouldReturnTotalWorkedYear() {
        //given
        Date startDate = new Date();

        //when
        when(DateUtil.getYearDifferenceBetweenDates(startDate, new Date())).thenReturn(0);

        //then
        int actual = annualPermitService.getTotalWorkedYearByStartDate(startDate);

        assertEquals(0, actual);
    }

    @Test
    public void whenGetBeginOfPeriodByEmployeeStartDateCalled_itShouldReturnDate() {
        //given
        Date startDate = new Date();
        int workedYear = 0;

        //when
        when(DateUtil.getYearDifferenceBetweenDates(new Date(), startDate)).thenReturn(workedYear);
        when(DateUtil.getFutureYearByDateAndYear(startDate, workedYear)).thenReturn(startDate);

        //then
        Date actual = annualPermitService.getBeginOfPeriodByEmployeeStartDate(startDate);

        assertEquals(startDate, actual);
    }

    @Test
    public void whenGetEndOfPeriodByEmployeeStartDateCalled_itShouldReturnDate() {
        //given
        Date startDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.YEAR, Calendar.YEAR + 1);

        Date expected = calendar.getTime();

        int workedYear = 0;

        //when
        when(DateUtil.getYearDifferenceBetweenDates(new Date(), startDate)).thenReturn(workedYear);
        when(DateUtil.getFutureYearByDateAndYear(startDate, workedYear + 1)).thenReturn(expected);

        //then
        Date actual = annualPermitService.getEndOfPeriodByEmployeeStartDate(startDate);

        assertEquals(expected, actual);
    }

    @Test
    public void whenUpdateEmployeeAnnualPermitRequestByDecisionCalled_itShouldReturnAnnualPermitResponseDto() {
        //given
        AnnualPermitStatus decision = AnnualPermitStatus.APPROVED;
        Long employeeId = 1L;

        AnnualPermit annualPermit = AnnualPermit.builder()
                .employeeId(employeeId)
                .annualPermitStatus(AnnualPermitStatus.PENDING)
                .build();

        AnnualPermit updatedAnnualPermit = AnnualPermit.builder()
                .employeeId(annualPermit.getEmployeeId())
                .annualPermitStatus(decision)
                .build();

        AnnualPermitResponseDto annualPermitResponseDto = AnnualPermitResponseDto.builder()
                .annualPermitStatus(updatedAnnualPermit.getAnnualPermitStatus())
                .build();

        //when
        when(annualPermitRepository
                .findByEmployeeIdAndAnnualPermitStatus(annualPermit.getEmployeeId(), AnnualPermitStatus.PENDING))
                .thenReturn(Optional.of(annualPermit));
        when(annualPermitRepository.save(any(AnnualPermit.class))).thenReturn(updatedAnnualPermit);
        when(annualPermitConverter.convertAnnualPermitToAnnualPermitResponseDto(updatedAnnualPermit))
                .thenReturn(annualPermitResponseDto);

        //then
        AnnualPermitResponseDto actual = annualPermitService.updateEmployeeAnnualPermitRequestByDecision(employeeId, decision);

        assertEquals(annualPermitResponseDto, actual);

        verify(annualPermitRepository, times(1))
                .findByEmployeeIdAndAnnualPermitStatus(employeeId, AnnualPermitStatus.PENDING);
        verify(annualPermitRepository, times(1)).save(any(AnnualPermit.class));
        verify(annualPermitConverter, times(1))
                .convertAnnualPermitToAnnualPermitResponseDto(updatedAnnualPermit);
    }

    @Test
        public void whenGetAnnualPermitDeservesByWorkedYearCalled_itShouldReturnYear() {
        //given
        annualPermitService.numberOfAnnualPermitDayForNewEmployee = 5;
        annualPermitService.numberOfAnnualPermitDayForBetweenOneAndFiveEqual = 15;
        annualPermitService.numberOfAnnualPermitDayForBetweenFiveAndTenEqual = 18;
        annualPermitService.numberOfAnnualPermitDayForGreaterTen = 24;

        // for numberOfAnnualPermitDayForNewEmployee
        int forNewEmployee = annualPermitService.getAnnualPermitDeservesByWorkedYear(0);
        assertEquals(5, forNewEmployee);

        // for numberOfAnnualPermitDayForBetweenOneAndFiveEqual
        int forBetweenOneAndFiveEqual = annualPermitService.getAnnualPermitDeservesByWorkedYear(4);
        assertEquals(15, forBetweenOneAndFiveEqual);

        // for numberOfAnnualPermitDayForBetweenFiveAndTenEqual
        int forBetweenFiveAndTenEqual = annualPermitService.getAnnualPermitDeservesByWorkedYear(10);
        assertEquals(18, forBetweenFiveAndTenEqual);

        // for numberOfAnnualPermitDayForGreaterTen
        int forGreaterTen = annualPermitService.getAnnualPermitDeservesByWorkedYear(15);
        assertEquals(24, forGreaterTen);
    }

    @Test
    public void whenGetUsedAnnualPermitDayInPeriodByEmployeeCalled_itShouldReturnUsedAnnualPermitDay() {
        //given
        Date currentDate = new Date();
        Employee employee = Employee.builder()
                .id(1L)
                .startDate(currentDate)
                .build();

        AnnualPermit annualPermit = AnnualPermit.builder()
                .annualPermitDays(4)
                .build();

        Date oneYearLater = this.getOneYearLater(currentDate);

        //when
        when(DateUtil.getYearDifferenceBetweenDates(currentDate, employee.getStartDate())).thenReturn(0); //this is for getTotalWorkedYearByStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 0)).thenReturn(currentDate); // this is for getBeginOfPeriodByEmployeeStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 1)).thenReturn(oneYearLater); //this is for getEndOfPeriodByEmployeeStartDate
        when(annualPermitRepository.findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                employee.getId(),
                AnnualPermitStatus.APPROVED,
                currentDate,
                oneYearLater)).thenReturn(Collections.singletonList(annualPermit));

        int actual = annualPermitService.getUsedAnnualPermitDayInPeriodByEmployee(employee);

        assertEquals(annualPermit.getAnnualPermitDays(), actual);

        verify(annualPermitRepository, times(1))
                .findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                        employee.getId(),
                        AnnualPermitStatus.APPROVED,
                        currentDate,
                        oneYearLater);
    }

    @Test
    public void whenGetAvailableAnnualPermitByEmployeeIdCalled_itShouldReturnAvailableAnnualPermitDay() {
        //given
        annualPermitService.numberOfAnnualPermitDayForNewEmployee = 5;
        Date currentDate = new Date();
        Employee employee = Employee.builder()
                .id(1L)
                .startDate(currentDate)
                .build();

        AnnualPermit annualPermit = AnnualPermit.builder()
                .annualPermitDays(3)
                .build();

        Date oneYearLater = this.getOneYearLater(currentDate);

        //when
        when(employeeService.findByIdOrElseThrow(employee.getId())).thenReturn(employee);
        when(DateUtil.getYearDifferenceBetweenDates(currentDate, employee.getStartDate())).thenReturn(0); //this is for getTotalWorkedYearByStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 0)).thenReturn(currentDate); // this is for getBeginOfPeriodByEmployeeStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 1)).thenReturn(oneYearLater); //this is for getEndOfPeriodByEmployeeStartDate
        when(annualPermitRepository.findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                employee.getId(),
                AnnualPermitStatus.APPROVED,
                currentDate,
                oneYearLater)).thenReturn(Collections.singletonList(annualPermit));

        int actual = annualPermitService.getAvailableAnnualPermitByEmployeeId(employee.getId());
        assertEquals(2, actual);

        verify(employeeService, times(1)).findByIdOrElseThrow(employee.getId());

        verify(annualPermitRepository, times(1))
                .findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                        employee.getId(),
                        AnnualPermitStatus.APPROVED,
                        currentDate,
                        oneYearLater);
    }

    @Test
    public void whenCreateAnnualPermitRequestCalledWithValidParams_itShouldReturnAnnualPermitResponseDto() throws ParseException {
        //given
        annualPermitService.numberOfAnnualPermitDayForNewEmployee = 5;
        String annualPermitStartDate = "12.12.2022";
        String annualPermitEndDate = "13.12.2022";

        Date currentDate = new Date();
        Date oneYearLater = this.getOneYearLater(currentDate);
        Date oneDayLater = this.getOneDayLater(currentDate);

        Employee employee = Employee.builder()
                .id(1L)
                .startDate(currentDate)
                .build();

        AnnualPermit annualPermit = AnnualPermit.builder()
                .startDate(currentDate)
                .endDate(oneDayLater)
                .annualPermitDays(1)
                .annualPermitStatus(AnnualPermitStatus.PENDING)
                .employeeId(employee.getId())
                .build();

        AnnualPermitResponseDto annualPermitResponseDto = AnnualPermitResponseDto.builder()
                .startDate(annualPermit.getStartDate())
                .endDate(annualPermit.getEndDate())
                .annualPermitDays(annualPermit.getAnnualPermitDays())
                .annualPermitStatus(annualPermit.getAnnualPermitStatus())
                .build();

        //when
        when(employeeService.findByIdOrElseThrow(employee.getId())).thenReturn(employee);
        when(annualPermitRepository.existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING)).thenReturn(false);

        when(DateUtil.getYearDifferenceBetweenDates(currentDate, employee.getStartDate())).thenReturn(0); //this is for getTotalWorkedYearByStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 0)).thenReturn(currentDate); // this is for getBeginOfPeriodByEmployeeStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 1)).thenReturn(oneYearLater); //this is for getEndOfPeriodByEmployeeStartDate
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitStartDate)).thenReturn(currentDate);
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitEndDate)).thenReturn(oneDayLater);
        when(DateUtil.getStartOfDay(any(Date.class))).thenReturn(new Date(0));
        when(DateUtil.getFutureDate(currentDate, 1)).thenReturn(oneDayLater);
        when(DateUtil.isDayWeekend(currentDate)).thenReturn(false);

        when(PublicHolidayService.isDayPublicHoliday(currentDate)).thenReturn(false);
        when(annualPermitRepository.findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                employee.getId(),
                AnnualPermitStatus.APPROVED,
                currentDate,
                oneYearLater)).thenReturn(new ArrayList<>());

        when(annualPermitRepository.save(any(AnnualPermit.class))).thenReturn(annualPermit);
        when(annualPermitConverter.convertAnnualPermitToAnnualPermitResponseDto(annualPermit)).thenReturn(annualPermitResponseDto);

        AnnualPermitResponseDto actual = annualPermitService.createAnnualPermitRequest(employee.getId(), annualPermitStartDate, annualPermitEndDate);
        assertEquals(annualPermitResponseDto, actual);

        verify(employeeService, times(2)).findByIdOrElseThrow(employee.getId());
        verify(annualPermitRepository, times(1))
                .existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING);
        verify(annualPermitRepository, times(1))
                .findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                        employee.getId(),
                        AnnualPermitStatus.APPROVED,
                        currentDate,
                        oneYearLater);

        verify(annualPermitRepository, times(1)).save(any(AnnualPermit.class));
        verify(annualPermitConverter, times(1)).convertAnnualPermitToAnnualPermitResponseDto(annualPermit);
    }

    @Test(expected = DataAllReadyExistsException.class)
    public void whenCreateAnnualPermitRequestCalledAndEmployeeHasPendingRequest_itShouldThrowDataAllReadyExistsException() {
        //given
        Employee employee = Employee.builder()
                .id(2L)
                .build();

        //when
        when(employeeService.findByIdOrElseThrow(employee.getId())).thenReturn(employee);
        when(annualPermitRepository.existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING)).thenReturn(true);

        //then
        AnnualPermitResponseDto actual = annualPermitService.createAnnualPermitRequest(employee.getId() , null, null);

        assertNull(actual);

        verify(employeeService, times(1)).findByIdOrElseThrow(employee.getId());
        verify(annualPermitRepository, times(1))
                .existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING);

        verifyNoInteractions(annualPermitRepository.save(any(AnnualPermit.class)));
        verifyNoInteractions(annualPermitConverter);
    }

    @Test(expected = DataNotAcceptableException.class)
    public void whenCreateAnnualPermitRequestCalledAndNonExistsAvailableAnnualPermit_itShouldThrowDataNotAcceptableException() {
        //given
        annualPermitService.numberOfAnnualPermitDayForNewEmployee = 5;

        Date currentDate = new Date();
        Date oneYearLater = this.getOneYearLater(currentDate);
        Employee employee = Employee.builder()
                .id(1L)
                .startDate(currentDate)
                .build();

        AnnualPermit annualPermit = AnnualPermit.builder()
                .annualPermitDays(5)
                .build();


        //when
        when(employeeService.findByIdOrElseThrow(employee.getId())).thenReturn(employee);
        when(annualPermitRepository.existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING)).thenReturn(false);

        when(DateUtil.getYearDifferenceBetweenDates(currentDate, employee.getStartDate())).thenReturn(0); //this is for getTotalWorkedYearByStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 0)).thenReturn(currentDate); // this is for getBeginOfPeriodByEmployeeStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 1)).thenReturn(oneYearLater); //this is for getEndOfPeriodByEmployeeStartDate

        when(annualPermitRepository.findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                employee.getId(),
                AnnualPermitStatus.APPROVED,
                currentDate,
                oneYearLater)).thenReturn(Collections.singletonList(annualPermit));

        AnnualPermitResponseDto actual = annualPermitService.createAnnualPermitRequest(employee.getId(), null, null);
        assertNull(actual);

        verify(employeeService, times(2)).findByIdOrElseThrow(employee.getId());
        verify(annualPermitRepository, times(1))
                .existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING);
        verify(annualPermitRepository, times(1))
                .findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                        employee.getId(),
                        AnnualPermitStatus.APPROVED,
                        currentDate,
                        oneYearLater);

        verifyNoInteractions(annualPermitRepository.save(any(AnnualPermit.class)));
        verifyNoInteractions(annualPermitConverter);
    }

    @Test(expected = DataNotAcceptableException.class)
    public void whenCreateAnnualPermitRequestCalledWithWrongStartDateFormat_itShouldThrowDataNotAcceptableException() throws ParseException {
        //given
        annualPermitService.numberOfAnnualPermitDayForNewEmployee = 5;
        String annualPermitStartDate = "12-12-2022";
        Date currentDate = new Date();
        Date oneYearLater = this.getOneYearLater(currentDate);
        Employee employee = Employee.builder()
                .id(1L)
                .startDate(currentDate)
                .build();

        //when
        when(employeeService.findByIdOrElseThrow(employee.getId())).thenReturn(employee);
        when(annualPermitRepository.existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING)).thenReturn(false);

        when(DateUtil.getYearDifferenceBetweenDates(currentDate, employee.getStartDate())).thenReturn(0); //this is for getTotalWorkedYearByStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 0)).thenReturn(currentDate); // this is for getBeginOfPeriodByEmployeeStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 1)).thenReturn(oneYearLater); //this is for getEndOfPeriodByEmployeeStartDate
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitStartDate)).thenThrow(new RuntimeException());

        when(annualPermitRepository.findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                employee.getId(),
                AnnualPermitStatus.APPROVED,
                currentDate,
                oneYearLater)).thenReturn(new ArrayList<>());

        AnnualPermitResponseDto actual = annualPermitService.createAnnualPermitRequest(employee.getId(), annualPermitStartDate, null);
        assertNull(actual);

        verify(employeeService, times(2)).findByIdOrElseThrow(employee.getId());
        verify(annualPermitRepository, times(1))
                .existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING);
        verify(annualPermitRepository, times(1))
                .findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                        employee.getId(),
                        AnnualPermitStatus.APPROVED,
                        currentDate,
                        oneYearLater);

        verifyNoInteractions(annualPermitRepository.save(any(AnnualPermit.class)));
        verifyNoInteractions(annualPermitConverter);
    }

    @Test(expected = DataNotAcceptableException.class)
    public void whenCreateAnnualPermitRequestCalledWithWrongEndDateFormat_itShouldThrowDataNotAcceptableException() throws ParseException {
        //given
        annualPermitService.numberOfAnnualPermitDayForNewEmployee = 5;
        String annualPermitStartDate = "12.12.2022";
        String annualPermitEndDate = "13-12-2022";
        Date currentDate = new Date();
        Date oneYearLater = this.getOneYearLater(currentDate);
        Employee employee = Employee.builder()
                .id(1L)
                .startDate(currentDate)
                .build();

        //when
        when(employeeService.findByIdOrElseThrow(employee.getId())).thenReturn(employee);
        when(annualPermitRepository.existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING)).thenReturn(false);

        when(DateUtil.getYearDifferenceBetweenDates(currentDate, employee.getStartDate())).thenReturn(0); //this is for getTotalWorkedYearByStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 0)).thenReturn(currentDate); // this is for getBeginOfPeriodByEmployeeStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 1)).thenReturn(oneYearLater); //this is for getEndOfPeriodByEmployeeStartDate
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitStartDate)).thenReturn(currentDate);
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitEndDate)).thenThrow(new RuntimeException());

        when(annualPermitRepository.findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                employee.getId(),
                AnnualPermitStatus.APPROVED,
                currentDate,
                oneYearLater)).thenReturn(new ArrayList<>());

        AnnualPermitResponseDto actual = annualPermitService.createAnnualPermitRequest(employee.getId(), annualPermitStartDate, annualPermitEndDate);
        assertNull(actual);

        verify(employeeService, times(2)).findByIdOrElseThrow(employee.getId());
        verify(annualPermitRepository, times(1))
                .existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING);
        verify(annualPermitRepository, times(1))
                .findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                        employee.getId(),
                        AnnualPermitStatus.APPROVED,
                        currentDate,
                        oneYearLater);

        verifyNoInteractions(annualPermitRepository.save(any(AnnualPermit.class)));
        verifyNoInteractions(annualPermitConverter);
    }

    @Test(expected = DataNotAcceptableException.class)
    public void whenCreateAnnualPermitRequestCalledWithEndDateBeforeStart_itShouldThrowDataNotAcceptableException() throws ParseException {
        //given
        annualPermitService.numberOfAnnualPermitDayForNewEmployee = 5;
        String annualPermitStartDate = "13.12.2022";
        String annualPermitEndDate = "12-12-2022";
        Date currentDate = new Date();
        Date oneYearLater = this.getOneYearLater(currentDate);
        Employee employee = Employee.builder()
                .id(1L)
                .startDate(currentDate)
                .build();

        //when
        when(employeeService.findByIdOrElseThrow(employee.getId())).thenReturn(employee);
        when(annualPermitRepository.existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING)).thenReturn(false);

        when(DateUtil.getYearDifferenceBetweenDates(currentDate, employee.getStartDate())).thenReturn(0); //this is for getTotalWorkedYearByStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 0)).thenReturn(currentDate); // this is for getBeginOfPeriodByEmployeeStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 1)).thenReturn(oneYearLater); //this is for getEndOfPeriodByEmployeeStartDate
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitStartDate)).thenReturn(currentDate);
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitEndDate)).thenReturn(this.getOneDayEarlier(currentDate));

        when(annualPermitRepository.findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                employee.getId(),
                AnnualPermitStatus.APPROVED,
                currentDate,
                oneYearLater)).thenReturn(new ArrayList<>());

        AnnualPermitResponseDto actual = annualPermitService.createAnnualPermitRequest(employee.getId(), annualPermitStartDate, annualPermitEndDate);
        assertNull(actual);

        verify(employeeService, times(2)).findByIdOrElseThrow(employee.getId());
        verify(annualPermitRepository, times(1))
                .existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING);
        verify(annualPermitRepository, times(1))
                .findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                        employee.getId(),
                        AnnualPermitStatus.APPROVED,
                        currentDate,
                        oneYearLater);

        verifyNoInteractions(annualPermitRepository.save(any(AnnualPermit.class)));
        verifyNoInteractions(annualPermitConverter);
    }

    @Test(expected = DataNotAcceptableException.class)
    public void whenCreateAnnualPermitRequestCalledWithStartDateBeforeToday_itShouldThrowDataNotAcceptableException() throws ParseException {
        //given
        annualPermitService.numberOfAnnualPermitDayForNewEmployee = 5;
        String annualPermitStartDate = "13.12.2022";
        String annualPermitEndDate = "14.12.2022";
        Date currentDate = new Date();
        Date oneYearLater = this.getOneYearLater(currentDate);
        Employee employee = Employee.builder()
                .id(1L)
                .startDate(currentDate)
                .build();

        //when
        when(employeeService.findByIdOrElseThrow(employee.getId())).thenReturn(employee);
        when(annualPermitRepository.existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING)).thenReturn(false);

        when(DateUtil.getYearDifferenceBetweenDates(currentDate, employee.getStartDate())).thenReturn(0); //this is for getTotalWorkedYearByStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 0)).thenReturn(currentDate); // this is for getBeginOfPeriodByEmployeeStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 1)).thenReturn(oneYearLater); //this is for getEndOfPeriodByEmployeeStartDate
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitStartDate)).thenReturn(currentDate);
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitEndDate)).thenReturn(this.getOneDayLater(currentDate));
        when(DateUtil.getStartOfDay(any(Date.class))).thenReturn(this.getOneDayLater(currentDate));

        when(annualPermitRepository.findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                employee.getId(),
                AnnualPermitStatus.APPROVED,
                currentDate,
                oneYearLater)).thenReturn(new ArrayList<>());

        AnnualPermitResponseDto actual = annualPermitService.createAnnualPermitRequest(employee.getId(), annualPermitStartDate, annualPermitEndDate);
        assertNull(actual);

        verify(employeeService, times(2)).findByIdOrElseThrow(employee.getId());
        verify(annualPermitRepository, times(1))
                .existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING);
        verify(annualPermitRepository, times(1))
                .findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                        employee.getId(),
                        AnnualPermitStatus.APPROVED,
                        currentDate,
                        oneYearLater);

        verifyNoInteractions(annualPermitRepository.save(any(AnnualPermit.class)));
        verifyNoInteractions(annualPermitConverter);
    }

    @Test(expected = DataNotAcceptableException.class)
    public void whenCreateAnnualPermitRequestCalledWithEndDateLaterEndOfPeriod_itShouldThrowDataNotAcceptableException() throws ParseException {
        //given
        annualPermitService.numberOfAnnualPermitDayForNewEmployee = 5;
        String annualPermitStartDate = "13.12.2022";
        String annualPermitEndDate = "14.12.2022";
        Date currentDate = new Date();
        Date oneYearLater = this.getOneYearLater(currentDate);
        Employee employee = Employee.builder()
                .id(1L)
                .startDate(currentDate)
                .build();

        //when
        when(employeeService.findByIdOrElseThrow(employee.getId())).thenReturn(employee);
        when(annualPermitRepository.existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING)).thenReturn(false);

        when(DateUtil.getYearDifferenceBetweenDates(currentDate, employee.getStartDate())).thenReturn(0); //this is for getTotalWorkedYearByStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 0)).thenReturn(currentDate); // this is for getBeginOfPeriodByEmployeeStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 1)).thenReturn(currentDate); //this is for getEndOfPeriodByEmployeeStartDate
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitStartDate)).thenReturn(currentDate);
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitEndDate)).thenReturn(this.getOneDayLater(currentDate));
        when(DateUtil.getStartOfDay(any(Date.class))).thenReturn(new Date(0));

        when(annualPermitRepository.findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                employee.getId(),
                AnnualPermitStatus.APPROVED,
                currentDate,
                oneYearLater)).thenReturn(new ArrayList<>());

        AnnualPermitResponseDto actual = annualPermitService.createAnnualPermitRequest(employee.getId(), annualPermitStartDate, annualPermitEndDate);
        assertNull(actual);

        verify(employeeService, times(2)).findByIdOrElseThrow(employee.getId());
        verify(annualPermitRepository, times(1))
                .existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING);
        verify(annualPermitRepository, times(1))
                .findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                        employee.getId(),
                        AnnualPermitStatus.APPROVED,
                        currentDate,
                        oneYearLater);

        verifyNoInteractions(annualPermitRepository.save(any(AnnualPermit.class)));
        verifyNoInteractions(annualPermitConverter);
    }

    @Test(expected = DataNotAcceptableException.class)
    public void whenCreateAnnualPermitRequestCalledWithHolidayDates_itShouldThrowDataNotAcceptableException() throws ParseException {
        //given
        annualPermitService.numberOfAnnualPermitDayForNewEmployee = 5;
        String annualPermitStartDate = "12.12.2022";
        String annualPermitEndDate = "13.12.2022";

        Date currentDate = new Date();
        Date oneYearLater = this.getOneYearLater(currentDate);
        Date oneDayLater = this.getOneDayLater(currentDate);

        Employee employee = Employee.builder()
                .id(1L)
                .startDate(currentDate)
                .build();

        //when
        when(employeeService.findByIdOrElseThrow(employee.getId())).thenReturn(employee);
        when(annualPermitRepository.existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING)).thenReturn(false);

        when(DateUtil.getYearDifferenceBetweenDates(currentDate, employee.getStartDate())).thenReturn(0); //this is for getTotalWorkedYearByStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 0)).thenReturn(currentDate); // this is for getBeginOfPeriodByEmployeeStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 1)).thenReturn(oneYearLater); //this is for getEndOfPeriodByEmployeeStartDate
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitStartDate)).thenReturn(currentDate);
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitEndDate)).thenReturn(oneDayLater);
        when(DateUtil.getStartOfDay(any(Date.class))).thenReturn(new Date(0));
        when(DateUtil.getFutureDate(currentDate, 1)).thenReturn(oneDayLater);
        when(DateUtil.isDayWeekend(currentDate)).thenReturn(true);

        when(PublicHolidayService.isDayPublicHoliday(currentDate)).thenReturn(true);
        when(annualPermitRepository.findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                employee.getId(),
                AnnualPermitStatus.APPROVED,
                currentDate,
                oneYearLater)).thenReturn(new ArrayList<>());

        AnnualPermitResponseDto actual = annualPermitService.createAnnualPermitRequest(employee.getId(), annualPermitStartDate, annualPermitEndDate);
        assertNull(actual);

        verify(employeeService, times(2)).findByIdOrElseThrow(employee.getId());
        verify(annualPermitRepository, times(1))
                .existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING);
        verify(annualPermitRepository, times(1))
                .findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                        employee.getId(),
                        AnnualPermitStatus.APPROVED,
                        currentDate,
                        oneYearLater);

        verifyNoInteractions(annualPermitRepository.save(any(AnnualPermit.class)));
        verifyNoInteractions(annualPermitConverter);
    }

    @Test(expected = DataNotAcceptableException.class)
    public void whenCreateAnnualPermitRequestCalledWithNonExistsAvailableDay_itShouldThrowDataNotAcceptableException() throws ParseException {
        //given
        annualPermitService.numberOfAnnualPermitDayForNewEmployee = 5;
        String annualPermitStartDate = "12.12.2022";
        String annualPermitEndDate = "14.12.2022";

        Date currentDate = new Date();
        Date oneYearLater = this.getOneYearLater(currentDate);
        Date oneDayLater = this.getOneDayLater(currentDate);

        Employee employee = Employee.builder()
                .id(1L)
                .startDate(currentDate)
                .build();

        AnnualPermit usedAnnualPermit = AnnualPermit.builder()
                .annualPermitDays(4)
                .createdOn(currentDate)
                .startDate(currentDate)
                .endDate(this.getOneDayLater(currentDate))
                .build();

        //when
        when(employeeService.findByIdOrElseThrow(employee.getId())).thenReturn(employee);
        when(annualPermitRepository.existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING)).thenReturn(false);

        when(DateUtil.getYearDifferenceBetweenDates(currentDate, employee.getStartDate())).thenReturn(0); //this is for getTotalWorkedYearByStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 0)).thenReturn(currentDate); // this is for getBeginOfPeriodByEmployeeStartDate
        when(DateUtil.getFutureYearByDateAndYear(employee.getStartDate(), 1)).thenReturn(oneYearLater); //this is for getEndOfPeriodByEmployeeStartDate

        when(DateUtil.parseDateToSimpleDateFormat(annualPermitStartDate)).thenReturn(currentDate); //this is for startDate
        when(DateUtil.parseDateToSimpleDateFormat(annualPermitEndDate)).thenReturn(this.getOneDayLater(oneDayLater)); //this is for endDate

        when(DateUtil.getStartOfDay(any(Date.class))).thenReturn(new Date(0));
        when(DateUtil.getFutureDate(currentDate, 1)).thenReturn(oneDayLater);
        when(DateUtil.getFutureDate(oneDayLater, 1)).thenReturn(this.getOneDayLater(oneDayLater));

        when(DateUtil.isDayWeekend(any(Date.class))).thenReturn(false);
        when(PublicHolidayService.isDayPublicHoliday(any(Date.class))).thenReturn(false);

        when(annualPermitRepository.findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                employee.getId(),
                AnnualPermitStatus.APPROVED,
                currentDate,
                oneYearLater)).thenReturn(Collections.singletonList(usedAnnualPermit));

        AnnualPermitResponseDto actual = annualPermitService.createAnnualPermitRequest(employee.getId(), annualPermitStartDate, annualPermitEndDate);
        assertNull(actual);

        verify(employeeService, times(2)).findByIdOrElseThrow(employee.getId());
        verify(annualPermitRepository, times(1))
                .existsByEmployeeIdAndAnnualPermitStatus(employee.getId(), AnnualPermitStatus.PENDING);
        verify(annualPermitRepository, times(1))
                .findAllByEmployeeIdAndAnnualPermitStatusAndCreatedOnBetween(
                        employee.getId(),
                        AnnualPermitStatus.APPROVED,
                        currentDate,
                        oneYearLater);

        verifyNoInteractions(annualPermitRepository.save(any(AnnualPermit.class)));
        verifyNoInteractions(annualPermitConverter);
    }

    public Date getOneYearLater(Date date) {
        return new Date(date.getTime() + 366 * 24 * 60 * 60 *1000L);
    }

    public Date getOneDayLater(Date date) {
        return new Date(date.getTime() + 25 * 60 * 61 * 1000L);
    }
    public Date getOneDayEarlier(Date date) {
        return new Date(date.getTime() - 25 * 60 * 61 * 1000L);
    }

}