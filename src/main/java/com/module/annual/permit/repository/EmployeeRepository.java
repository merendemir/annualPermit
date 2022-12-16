package com.module.annual.permit.repository;

import com.module.annual.permit.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("select employee from Employee employee where day (employee.startDate) = ?1 and month(employee.startDate) = ?2")
    List<Employee> findAllByStartDate_DayAndStartDate_Month(int day, int month);

}
